package com.l2jserver.gameserver.model.actor.templates.drop;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.datatables.categorized.GradedItemsData;
import com.l2jserver.gameserver.datatables.categorized.ItemPartsData;
import com.l2jserver.gameserver.datatables.categorized.ItemRecipesData;
import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.chances.ItemGradeChanceMods;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.util.Rnd;

import java.util.*;
import java.util.stream.Collectors;

public class DynamicDropCalculator {

    private Set<Integer> managedItemIds = new HashSet<>();

    public DynamicDropCalculator() {
        load();
    }

    private void load() {
        managedItemIds.addAll(GradedItemsData.getInstance().getGradedItemsIds());
        managedItemIds.addAll(ItemPartsData.getInstance().getItemPartsIds());
        managedItemIds.addAll(ItemRecipesData.getInstance().getRecipeIds());
    }

    public List<ItemHolder> calculate(L2Character victim, L2Character killer) {
        Optional<ItemGradeRange> range = ItemGradeRange.byLevel(victim.getLevel());
        if (!range.isPresent()) {
            return Lists.newArrayList();
        }

        GradeInfo gradeInfo = range.get().getGradeInfo();

        Collection<GradedItem> possibleDropItems = GradedItemsData.getInstance().getGradedItemsMap().get(gradeInfo);

        List<ItemHolder> partHolders = ItemPartsData.getInstance().getItemPartsByGradeInfo(gradeInfo).stream().filter(part -> Rnd.getDouble(100) <= 70).map(itemPart -> new ItemHolder(itemPart.getPartId(), Rnd.get(2) + 1)).collect(Collectors.toList());
        List<ItemHolder> itemHolders = possibleDropItems.stream()
                .filter(gradedItem -> Rnd.getDouble(100) <= Math.max(ItemGradeChanceMods.valueOf(gradedItem.getGradeInfo().getGrade().name()).getMod() * 10, 100))
                .map(gradedItem -> new ItemHolder(gradedItem.getItemId(), 1))
                .collect(Collectors.toList());


        Collection<L2RecipeList> recipes = ItemRecipesData.getInstance().getRecipesByGradeInfo(gradeInfo);
        List<ItemHolder> recipeHolders = recipes.stream().filter(recipe -> Rnd.getDouble(100) <= 10).map(recipe -> new ItemHolder(recipe.getRecipeId(), 1)).collect(Collectors.toList());


        List<ItemHolder> allDrop = new ArrayList<>(partHolders.size() + itemHolders.size() + recipeHolders.size());
        allDrop.addAll(partHolders);
        allDrop.addAll(itemHolders);
        allDrop.addAll(recipeHolders);
        return allDrop;
    }

    public Set<Integer> getAllDynamicItemsIds() {
        return managedItemIds;
    }

    public static DynamicDropCalculator getInstance() {
        return DynamicDropCalculator.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final DynamicDropCalculator INSTANCE = new DynamicDropCalculator();
    }

}
