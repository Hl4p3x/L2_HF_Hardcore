package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.l2jserver.gameserver.datatables.categorized.*;
import com.l2jserver.gameserver.datatables.categorized.interfaces.EquipmentProvider;
import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.*;
import com.l2jserver.gameserver.model.actor.templates.drop.custom.CustomDropEntry;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.EquipmentDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.MiscScrollStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;
import com.l2jserver.gameserver.model.items.craft.CraftResource;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.gameserver.model.items.parts.ItemPart;
import com.l2jserver.gameserver.model.items.scrolls.CategorizedScrolls;
import com.l2jserver.gameserver.model.items.scrolls.Scroll;
import com.l2jserver.util.CollectionUtil;
import com.l2jserver.util.ObjectMapperYamlSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DynamicDropTable {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicDropTable.class);

    private AllDynamicDropData allDynamicDropData;

    public DynamicDropTable() {
        load();
    }

    private void load() {
        final String path = "./config/DynamicDropRates.yml";
        try {
            InputStream configStream = new FileInputStream(new File(path));

            allDynamicDropData = ObjectMapperYamlSingleton.getInstance().readValue(
                    configStream,
                    AllDynamicDropData.class
            );
        } catch (IOException e) {
            throw new IllegalStateException("Could not load Dynamic Drop Rates configuration from " + path);
        }
    }

    public AllDynamicDropData getAllDynamicDropData() {
        return allDynamicDropData;
    }

    public <T> DynamicDropEquipmentCategory convertEquipmentCategory(int level, EquipmentProvider<T> equipmentProvider, EquipmentDropStats equipmentDropStats, Function<T, Integer> idExtractor) {
        Optional<ItemGradeRange> itemGradeRange = ItemGradeRange.byLevel(level);
        if (!itemGradeRange.isPresent()) {
            return DynamicDropEquipmentCategory.empty();
        }

        GradeInfo gradeInfo = itemGradeRange.get().getGradeInfo();
        DynamicDropCategory weaponsCategory = convertDropCategory(gradeInfo, equipmentDropStats::getWeapon, equipmentProvider::getWeaponsByGrade, idExtractor);
        DynamicDropCategory armorCategory = convertDropCategory(gradeInfo, equipmentDropStats::getArmor, equipmentProvider::getArmorByGrade, idExtractor);
        DynamicDropCategory jewelsCategory = convertDropCategory(gradeInfo, equipmentDropStats::getJewels, equipmentProvider::getJewelsByGrade, idExtractor);
        return new DynamicDropEquipmentCategory(gradeInfo, weaponsCategory, armorCategory, jewelsCategory);
    }

    private <T> DynamicDropCategory convertDropCategory(
            GradeInfo gradeInfo,
            Function<GradeInfo, Optional<DropStats>> findDropStats,
            Function<GradeInfo, List<T>> itemsProvider,
            Function<T, Integer> idExtractor
    ) {
        Optional<DropStats> dropStatsOptional = findDropStats.apply(gradeInfo);
        if (!dropStatsOptional.isPresent()) {
            LOG.warn("Could not find drop data for {}", gradeInfo);
            return DynamicDropCategory.empty();
        }

        List<T> items = itemsProvider.apply(gradeInfo);
        Set<Integer> ids = CollectionUtil.extract(items, idExtractor);

        return new DynamicDropCategory(ids, dropStatsOptional.get());
    }

    private List<DynamicDropResourcesCategory> convertResourcesCategory(int level, ResourceDropStats resourceDropStats) {
        Set<ResourceGrade> resourceGrades = ResourceGradeRange.byLevel(level);

        List<DynamicDropResourcesCategory> drop = new ArrayList<>();
        for (ResourceGrade resourceGrade : resourceGrades) {
            DropStats dropStats = resourceDropStats.getDropByResourceGrade(resourceGrade);
            List<CraftResource> craftResources = CraftResourcesDropDataTable.getInstance().getResourcesByGrade(resourceGrade);
            drop.add(new DynamicDropResourcesCategory(resourceGrade, new DynamicDropCategory(CollectionUtil.extractIds(craftResources), dropStats)));
        }
        return drop;
    }

    private DynamicDropScrollCategory convertScrollCategory(
            int level,
            Function<ScrollGrade, List<Scroll>> normalScrollProvider,
            Function<ScrollGrade, List<Scroll>> blessedScrollProvider,
            Function<ScrollGrade, ScrollDropStats> scrollDropStatsProvider) {
        Optional<ScrollGrade> scrollGradeOptional = ScrollGradeRange.byLevel(level);
        if (scrollGradeOptional.isPresent()) {
            ScrollGrade scrollGrade = scrollGradeOptional.get();
            ScrollDropStats scrollDropStats = scrollDropStatsProvider.apply(scrollGrade);
            Set<Integer> normalScrollsIds = CollectionUtil.extractIds(normalScrollProvider.apply(scrollGrade));
            Set<Integer> blessedScrollsIds = CollectionUtil.extractIds(blessedScrollProvider.apply(scrollGrade));
            return new DynamicDropScrollCategory(
                    scrollGrade,
                    new DynamicDropCategory(normalScrollsIds, scrollDropStats.getNormal()),
                    new DynamicDropCategory(blessedScrollsIds, scrollDropStats.getBlessed())
            );
        } else {
            if (level > ScrollGradeRange.D.getLowLevel()) {
                LOG.warn("Could not find scroll drop data for level {}", level);
            }
            return DynamicDropScrollCategory.empty();
        }
    }

    public DynamicDropGradeData getDynamicNpcDropData(L2Character victim) {
        if (victim.isRaid()) {
            return getDynamicRaidDropData(victim.getLevel());
        } else {
            return getDynamicMobDropData(victim.getLevel());
        }
    }

    public List<CustomDropEntry> getCustomDrop(L2Character victim) {
        DynamicDropData dynamicDropData;
        if (victim.isRaid()) {
            dynamicDropData = allDynamicDropData.getRaid();
        } else {
            dynamicDropData = allDynamicDropData.getMobs();
        }
        return dynamicDropData.getCustomDropEntries().stream().filter(drop -> drop.getLevelRange().isWithin(victim.getLevel())).collect(Collectors.toList());
    }

    public DynamicDropGradeData getDynamicMobDropData(int level) {
        return getDynamicDropData(level, allDynamicDropData.getMobs());
    }

    public DynamicDropGradeData getDynamicRaidDropData(int level) {
        return getDynamicDropData(level, allDynamicDropData.getRaid());
    }

    public DynamicDropGradeData getDynamicDropData(int level, DynamicDropData dynamicDropData) {
        DynamicDropEquipmentCategory equipment = convertEquipmentCategory(
                level,
                GradedItemsDropDataTable.getInstance(),
                dynamicDropData.getEquipment(),
                GradedItem::getItemId
        );

        DynamicDropEquipmentCategory parts = convertEquipmentCategory(
                level,
                ItemPartsDropDataTable.getInstance(),
                dynamicDropData.getParts(),
                ItemPart::getPartId
        );

        DynamicDropEquipmentCategory recipes = convertEquipmentCategory(
                level,
                ItemRecipesDropDataTable.getInstance(),
                dynamicDropData.getRecipes(),
                L2RecipeList::getRecipeId
        );

        List<DynamicDropResourcesCategory> resources = convertResourcesCategory(level, dynamicDropData.getResources());

        CategorizedScrolls categorizedScrolls = ScrollDropDataTable.getInstance().getCategorizedScrolls();

        DynamicDropScrollCategory weaponScrolls = convertScrollCategory(level,
                categorizedScrolls::findAllNormalWeaponScroll,
                categorizedScrolls::findAllBlessedWeaponScroll,
                dynamicDropData.getScrolls()::getWeaponByGrade);

        DynamicDropScrollCategory armorScrolls = convertScrollCategory(level,
                categorizedScrolls::findAllNormalArmorScroll,
                categorizedScrolls::findAllBlessedArmorScroll,
                dynamicDropData.getScrolls()::getArmorByGrade);

        DynamicDropScrollCategory miscScrolls = convertMiscScrollCategory(
                categorizedScrolls,
                dynamicDropData.getScrolls().getMisc()
        );

        return new DynamicDropGradeData(
                equipment, parts, recipes,
                resources,
                weaponScrolls,
                armorScrolls,
                miscScrolls
        );
    }

    private DynamicDropScrollCategory convertMiscScrollCategory(CategorizedScrolls categorizedScrolls, MiscScrollStats misc) {
        Set<Integer> normalScrollIds = CollectionUtil.extractIds(categorizedScrolls.getNormalMiscScrolls());
        Set<Integer> blessedScrollIds = CollectionUtil.extractIds(categorizedScrolls.getBlessedMiscScrolls());
        return new DynamicDropScrollCategory(ScrollGrade.ALL, new DynamicDropCategory(normalScrollIds, misc.getNormal()), new DynamicDropCategory(blessedScrollIds, misc.getBlessed()));
    }

    public static DynamicDropTable getInstance() {
        return DynamicDropTable.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final DynamicDropTable _instance = new DynamicDropTable();
    }

}
