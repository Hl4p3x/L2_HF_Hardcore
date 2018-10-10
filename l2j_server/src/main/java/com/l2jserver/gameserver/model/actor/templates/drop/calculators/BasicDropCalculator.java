package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.datatables.categorized.CraftResourcesDropDataTable;
import com.l2jserver.gameserver.datatables.categorized.GradedItemsDropDataTable;
import com.l2jserver.gameserver.datatables.categorized.ScrollDropDataTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.ItemGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.ResourceGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.ScrollGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.EquipmentDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.AllScrollsDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.MiscScrollStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.craft.CraftResource;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;
import com.l2jserver.gameserver.model.items.graded.GradeCategory;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.gameserver.model.items.scrolls.CategorizedScrolls;
import com.l2jserver.gameserver.model.items.scrolls.MiscScroll;
import com.l2jserver.gameserver.model.items.scrolls.Scroll;
import com.l2jserver.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BasicDropCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(BasicDropCalculator.class);

    public List<ItemHolder> calculate(L2Character victim, DynamicDropData dynamicDropData) {
        Optional<ItemGradeRange> range = ItemGradeRange.byLevel(victim.getLevel());
        if (!range.isPresent()) {
            return Lists.newArrayList();
        }

        GradeInfo gradeInfo = range.get().getGradeInfo();
        List<ItemHolder> drop = new ArrayList<>();
        drop.addAll(calculateEquipmentDrop(gradeInfo, dynamicDropData.getEquipment()));
        drop.addAll(calculateEquipmentDrop(gradeInfo, dynamicDropData.getParts()));
        drop.addAll(calculateEquipmentDrop(gradeInfo, dynamicDropData.getRecipes()));
        drop.addAll(calculateResourceDrop(victim, dynamicDropData.getResources()));
        drop.addAll(calculateScrollDrop(victim, dynamicDropData.getScrolls()));

        return drop;
    }

    private List<ItemHolder> calculateScrollDrop(L2Character victim, AllScrollsDropData allScrollsDropData) {
        Optional<ScrollGrade> grade = ScrollGradeRange.byLevel(victim.getLevel());
        if (!grade.isPresent()) {
            return Lists.newArrayList();
        }

        List<ItemHolder> drop = new ArrayList<>();

        CategorizedScrolls categorizedScrolls = ScrollDropDataTable.getInstance().getCategorizedScrolls();

        drop.addAll(calculateScrollDropData(
                categorizedScrolls.getNormalWeaponScrolls(), categorizedScrolls.getBlessedWeaponScrolls(),
                allScrollsDropData.getWeapon().get(grade.get()))
        );
        drop.addAll(calculateScrollDropData(
                categorizedScrolls.getNormalArmorScrolls(), categorizedScrolls.getBlessedArmorScrolls(),
                allScrollsDropData.getArmor().get(grade.get()))
        );
        drop.addAll(calculateMiscScrolls(categorizedScrolls, allScrollsDropData.getMisc()));

        return drop;
    }

    private List<ItemHolder> calculateScrollDropData(List<Scroll> normalScrolls, List<Scroll> blessedScrolls, ScrollDropData scrollDropData) {
        List<ItemHolder> drop = new ArrayList<>();
        calculateSingleScroll(normalScrolls, scrollDropData.getNormal()).ifPresent(drop::add);
        calculateSingleScroll(blessedScrolls, scrollDropData.getBlessed()).ifPresent(drop::add);
        return drop;
    }

    private List<ItemHolder> calculateMiscScrolls(CategorizedScrolls categorizedScrolls, MiscScrollStats miscScrollStats) {
        List<ItemHolder> drop = new ArrayList<>();
        Optional<ItemHolder> normalScroll = calculateSingleMiscScroll(categorizedScrolls.getNormalMiscScrolls(), miscScrollStats.getNormal());
        normalScroll.ifPresent(drop::add);

        Optional<ItemHolder> blessedScroll = calculateSingleMiscScroll(categorizedScrolls.getBlessedMiscScrolls(), miscScrollStats.getBlessed());
        blessedScroll.ifPresent(drop::add);
        return drop;
    }

    private Optional<ItemHolder> calculateSingleMiscScroll(List<MiscScroll> scrolls, ChanceCountPair chanceCountPair) {
        Optional<MiscScroll> randomScrollOption = Rnd.getOneRandom(scrolls);
        if (!randomScrollOption.isPresent()) {
            return Optional.empty();
        }

        if (Rnd.rollAgainst(chanceCountPair.getChance())) {
            return Optional.of(new ItemHolder(randomScrollOption.get().getId(), chanceCountPair.getCount().randomWithin()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<ItemHolder> calculateSingleScroll(List<Scroll> scrolls, ChanceCountPair chanceCountPair) {
        Optional<Scroll> randomScrollOption = Rnd.getOneRandom(scrolls);
        if (!randomScrollOption.isPresent()) {
            return Optional.empty();
        }

        if (Rnd.rollAgainst(chanceCountPair.getChance())) {
            return Optional.of(new ItemHolder(randomScrollOption.get().getId(), chanceCountPair.getCount().randomWithin()));
        } else {
            return Optional.empty();
        }
    }

    private List<ItemHolder> calculateResourceDrop(L2Character victim, ResourceDropData resourceDropData) {
        Set<ResourceGrade> resourceGrades = ResourceGradeRange.byLevel(victim.getLevel());
        if (resourceGrades.isEmpty()) {
            return Lists.newArrayList();
        }

        List<ItemHolder> drop = new ArrayList<>();
        for (ResourceGrade resourceGrade : resourceGrades) {
            // Add Overgrade logic
            ChanceCountPair chanceCountPair = resourceDropData.getDrop().get(resourceGrade);
            List<CraftResource> resources = CraftResourcesDropDataTable.getInstance().getResourcesByGrade(resourceGrade);
            Optional<CraftResource> randomResource = Rnd.getOneRandom(resources);
            if (!randomResource.isPresent()) {
                continue;
            }

            if (Rnd.rollAgainst(chanceCountPair.getChance())) {
                drop.add(new ItemHolder(randomResource.get().getId(), chanceCountPair.getCount().randomWithin()));
            }
        }

        return drop;
    }

    private List<ItemHolder> calculateEquipmentDrop(GradeInfo gradeInfo, EquipmentDropData equipmentDropData) {
        List<ItemHolder> drop = new ArrayList<>();
        drop.addAll(calculateChanceCountPairDrop(
                gradeInfo,
                findItemsByGradeInfo(
                        gradeInfo, GradedItemsDropDataTable.getInstance().getGradedWeaponsMap()
                ),
                findChanceCountByGradeInfo(gradeInfo, equipmentDropData.getWeapon())
        ));
        drop.addAll(calculateChanceCountPairDrop(
                gradeInfo,
                findItemsByGradeInfo(
                        gradeInfo, GradedItemsDropDataTable.getInstance().getGradedArmorMap()
                ),
                findChanceCountByGradeInfo(gradeInfo, equipmentDropData.getArmor())
        ));
        drop.addAll(calculateChanceCountPairDrop(
                gradeInfo,
                findItemsByGradeInfo(
                        gradeInfo, GradedItemsDropDataTable.getInstance().getGradedJewelsMap()
                ),
                findChanceCountByGradeInfo(gradeInfo, equipmentDropData.getJewels())
        ));
        return drop;
    }

    private List<GradedItem> findItemsByGradeInfo(GradeInfo gradeInfo, Map<GradeInfo, List<GradedItem>> gradedItemsMap) {
        List<GradedItem> gradedItems = Optional.ofNullable(gradedItemsMap.get(gradeInfo)).orElse(new ArrayList<>());
        if (gradeInfo.getCategory() != GradeCategory.ALL) {
            Optional.ofNullable(gradedItemsMap.get(new GradeInfo(gradeInfo.getGrade(), GradeCategory.ALL))).ifPresent(gradedItems::addAll);
        }
        return gradedItems;
    }

    private ChanceCountPair findChanceCountByGradeInfo(GradeInfo gradeInfo, Map<GradeInfo, ChanceCountPair> chanceCountPairs) {
        ChanceCountPair chanceCountPair = chanceCountPairs.get(gradeInfo);
        if (chanceCountPair == null && gradeInfo.getCategory() != GradeCategory.ALL) {
            LOG.debug("Could not find drop stats for {}, trying to search for All category", gradeInfo);
            chanceCountPair = chanceCountPairs.get(new GradeInfo(gradeInfo.getGrade(), GradeCategory.ALL));
        }
        return chanceCountPair;
    }

    private List<ItemHolder> calculateChanceCountPairDrop(GradeInfo gradeInfo, List<GradedItem> gradedItems, ChanceCountPair chanceCountPair) {
        if (chanceCountPair == null || gradedItems.isEmpty()) {
            LOG.warn("Skipping drop calculation for grade {} because there was drop no chances or items found", gradeInfo);
            return new ArrayList<>();
        }

        List<ItemHolder> drop = new ArrayList<>();
        Collection<GradedItem> randomItems = Rnd.getFewRandom(gradedItems, chanceCountPair.getCount().randomWithin());
        for (GradedItem item : randomItems) {
            if (Rnd.rollAgainst(chanceCountPair.getChance())) {
                drop.add(new ItemHolder(item.getItemId(), 1));
            }
        }
        return drop;
    }

}
