package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.datatables.categorized.CraftResourcesData;
import com.l2jserver.gameserver.datatables.categorized.GradedItemsData;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.ItemGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.ResourceGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.EquipmentDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.AllScrollsDropData;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.craft.CraftResource;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.util.Rnd;

import java.util.*;

public class BasicDropCalculator {

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
        drop.addAll(calculateScrollDrop(gradeInfo, dynamicDropData.getScrolls()));

        return drop;
    }

    private List<ItemHolder> calculateScrollDrop(GradeInfo gradeInfo, AllScrollsDropData allScrollsDropData) {
        return null;
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
            List<CraftResource> resources = CraftResourcesData.getInstance().getResourcesByGrade(resourceGrade);
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
        drop.addAll(calculateChanceCountPairDrop(gradeInfo, equipmentDropData.getWeapon()));
        drop.addAll(calculateChanceCountPairDrop(gradeInfo, equipmentDropData.getArmor()));
        drop.addAll(calculateChanceCountPairDrop(gradeInfo, equipmentDropData.getJewels()));
        return drop;
    }

    private List<ItemHolder> calculateChanceCountPairDrop(GradeInfo gradeInfo, Map<GradeInfo, ChanceCountPair> chanceCountPairs) {
        ChanceCountPair chanceCountPair = chanceCountPairs.get(gradeInfo);
        List<GradedItem> gradedItems = GradedItemsData.getInstance().getGradedItemsMap().get(gradeInfo);
        if (chanceCountPair == null || gradedItems.isEmpty()) {
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
