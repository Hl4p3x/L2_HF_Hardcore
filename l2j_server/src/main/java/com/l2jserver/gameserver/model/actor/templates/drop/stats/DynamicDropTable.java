package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.l2jserver.gameserver.datatables.categorized.CraftResourcesDropDataTable;
import com.l2jserver.gameserver.datatables.categorized.GradedItemsDropDataTable;
import com.l2jserver.gameserver.datatables.categorized.ItemPartsDropDataTable;
import com.l2jserver.gameserver.datatables.categorized.ItemRecipesDropDataTable;
import com.l2jserver.gameserver.datatables.categorized.interfaces.EquipmentProvider;
import com.l2jserver.gameserver.model.actor.templates.drop.*;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.EquipmentDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollDropStats;
import com.l2jserver.gameserver.model.items.craft.CraftResource;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.interfaces.HasItemId;
import com.l2jserver.util.CollectionUtil;
import com.l2jserver.util.ObjectMapperYamlSingleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DynamicDropTable {

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

    public <T extends HasItemId> DynamicDropEquipmentCategory convertEquipmentCategory(GradeInfo gradeInfo, EquipmentProvider<T> equipmentProvider, EquipmentDropStats equipmentDropStats) {
        DynamicDropCategory weaponsCategory = new DynamicDropCategory(
                CollectionUtil.extract(equipmentProvider.getWeaponsByGrade(gradeInfo), HasItemId::getItemId),
                equipmentDropStats.getWeapon(gradeInfo)
        );

        DynamicDropCategory armorCategory = new DynamicDropCategory(
                CollectionUtil.extract(equipmentProvider.getArmorByGrade(gradeInfo), HasItemId::getItemId),
                equipmentDropStats.getArmor(gradeInfo)
        );


        Optional<DropStats> jewelsDropStats = equipmentDropStats.getJewels(gradeInfo);
        if (jewelsDropStats.isPresent()) {
            DynamicDropCategory jewelsCategory = new DynamicDropCategory(
                    CollectionUtil.extract(equipmentProvider.getJewelsByGrade(gradeInfo), HasItemId::getItemId),
                    jewelsDropStats.get()
            );
        }
        return new DynamicDropEquipmentCategory(weaponsCategory, armorCategory, jewelsCategory);
    }


    private List<DynamicDropCategory> convertResourcesCategory(int level, ResourceDropStats resourceDropStats) {
        Set<ResourceGrade> resourceGrades = ResourceGradeRange.byLevel(level);

        List<DynamicDropCategory> drop = new ArrayList<>();
        for (ResourceGrade resourceGrade : resourceGrades) {
            DropStats dropStats = resourceDropStats.getDropByResourceGrade(resourceGrade);
            List<CraftResource> craftResources = CraftResourcesDropDataTable.getInstance().getResourcesByGrade(resourceGrade);
            drop.add(new DynamicDropCategory(CollectionUtil.extractIds(craftResources), dropStats));
        }
        return drop;
    }

    private DynamicDropScrollCategory convertScrollCategory(int level, ScrollDropStats scrollDropStats) {
        Optional<ScrollGradeRange> scrollGradeRangeOptional = ScrollGradeRange.byLevel(level);


    }

    public Optional<DynamicDropGradeData> getMobsDynamicDropData(int level) {
        Optional<ItemGradeRange> itemGradeRange = ItemGradeRange.byLevel(level);
        if (!itemGradeRange.isPresent()) {
            return Optional.empty();
        }

        GradeInfo gradeInfo = itemGradeRange.get().getGradeInfo();

        DynamicDropEquipmentCategory equipment = convertEquipmentCategory(
                gradeInfo,
                GradedItemsDropDataTable.getInstance(),
                allDynamicDropData.getMobs().getEquipment());

        DynamicDropEquipmentCategory parts = convertEquipmentCategory(
                gradeInfo,
                ItemPartsDropDataTable.getInstance(),
                allDynamicDropData.getMobs().getParts());

        DynamicDropEquipmentCategory recipes = convertEquipmentCategory(
                gradeInfo,
                ItemRecipesDropDataTable.getInstance(),
                allDynamicDropData.getMobs().getRecipes());

        List<DynamicDropCategory> resources = convertResourcesCategory(level, allDynamicDropData.getMobs().getResources());

        DynamicDropScrollCategory weaponScrolls = convertScrollCategory(level, allDynamicDropData.getMobs().getScrolls().getWeapon());
        DynamicDropScrollCategory armorScrolls = convertScrollCategory(level, allDynamicDropData.getMobs().getScrolls().getArmor());

        return Optional.of(new DynamicDropGradeData(
                equipment, parts, recipes,
                resources,
                weaponScrolls,
                armorScrolls,
                miscScrolls)
        );
    }

    public static DynamicDropTable getInstance() {
        return DynamicDropTable.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final DynamicDropTable _instance = new DynamicDropTable();
    }

}
