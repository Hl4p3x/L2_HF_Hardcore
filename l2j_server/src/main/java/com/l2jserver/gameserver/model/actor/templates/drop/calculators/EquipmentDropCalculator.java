package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.datatables.categorized.GradedItemsDropDataTable;
import com.l2jserver.gameserver.datatables.categorized.ItemPartsDropDataTable;
import com.l2jserver.gameserver.datatables.categorized.ItemRecipesDropDataTable;
import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.graded.GradeCategory;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.gameserver.model.items.parts.ItemPart;
import com.l2jserver.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EquipmentDropCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentDropCalculator.class);

    public List<ItemHolder> calculate(GradeInfo gradeInfo, DynamicDropData dynamicDropData) {
        List<ItemHolder> drop = new ArrayList<>();
        drop.addAll(calculateEquipmentDrop(
                gradeInfo,
                findItemsByGradeInfo(
                        gradeInfo, GradedItemsDropDataTable.getInstance().getGradedWeaponsMap()
                ),
                findChanceCountByGradeInfo(gradeInfo, dynamicDropData.getEquipment().getWeapon())
        ));
        drop.addAll(calculateEquipmentDrop(
                gradeInfo,
                findItemsByGradeInfo(
                        gradeInfo, GradedItemsDropDataTable.getInstance().getGradedArmorMap()
                ),
                findChanceCountByGradeInfo(gradeInfo, dynamicDropData.getEquipment().getArmor())
        ));
        drop.addAll(calculateEquipmentDrop(
                gradeInfo,
                findItemsByGradeInfo(
                        gradeInfo, GradedItemsDropDataTable.getInstance().getGradedJewelsMap()
                ),
                findChanceCountByGradeInfo(gradeInfo, dynamicDropData.getEquipment().getJewels())
        ));


        calculatePartsDrop(
                ItemPartsDropDataTable.getInstance().getWeaponPartsByGradeInfo(gradeInfo),
                findChanceCountByGradeInfo(gradeInfo, dynamicDropData.getParts().getWeapon())
        ).ifPresent(drop::add);

        calculatePartsDrop(
                ItemPartsDropDataTable.getInstance().getArmorPartsByGradeInfo(gradeInfo),
                findChanceCountByGradeInfo(gradeInfo, dynamicDropData.getParts().getArmor())
        ).ifPresent(drop::add);

        calculatePartsDrop(
                ItemPartsDropDataTable.getInstance().getJewelPartsByGradeInfo(gradeInfo),
                findChanceCountByGradeInfo(gradeInfo, dynamicDropData.getParts().getJewels())
        ).ifPresent(drop::add);


        calculateRecipesDrop(
                ItemRecipesDropDataTable.getInstance().getWeaponRecipesByGradeInfo(gradeInfo),
                findChanceCountByGradeInfo(gradeInfo, dynamicDropData.getRecipes().getWeapon())
        ).ifPresent(drop::add);

        calculateRecipesDrop(
                ItemRecipesDropDataTable.getInstance().getArmorRecipesByGrade(gradeInfo),
                findChanceCountByGradeInfo(gradeInfo, dynamicDropData.getRecipes().getArmor())
        ).ifPresent(drop::add);

        calculateRecipesDrop(
                ItemRecipesDropDataTable.getInstance().getJewelRecipesByGrade(gradeInfo),
                findChanceCountByGradeInfo(gradeInfo, dynamicDropData.getRecipes().getJewels())
        ).ifPresent(drop::add);
        return drop;
    }

    private Optional<ItemHolder> calculateRecipesDrop(List<L2RecipeList> itemsByGradeInfo, ChanceCountPair dropStats) {
        Optional<L2RecipeList> part = Rnd.getOneRandom(itemsByGradeInfo);
        if (part.isPresent() && Rnd.rollAgainst(dropStats.getChance())) {
            return Optional.of(new ItemHolder(part.get().getRecipeId(), dropStats.getCount().randomWithin()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<ItemHolder> calculatePartsDrop(List<ItemPart> parts, ChanceCountPair dropStats) {
        Optional<ItemPart> part = Rnd.getOneRandom(parts);
        if (part.isPresent() && Rnd.rollAgainst(dropStats.getChance())) {
            return Optional.of(new ItemHolder(part.get().getPartId(), dropStats.getCount().randomWithin()));
        } else {
            return Optional.empty();
        }
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

    private List<ItemHolder> calculateEquipmentDrop(GradeInfo gradeInfo, List<GradedItem> gradedItems, ChanceCountPair chanceCountPair) {
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
