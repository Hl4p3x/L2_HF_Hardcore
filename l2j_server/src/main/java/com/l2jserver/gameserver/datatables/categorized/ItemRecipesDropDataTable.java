package com.l2jserver.gameserver.datatables.categorized;

import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ItemRecipesDropDataTable {

    private static final Logger LOG = LoggerFactory.getLogger(ItemRecipesDropDataTable.class);

    private Set<Integer> recipeIds = new HashSet<>();
    private Map<GradeInfo, List<L2RecipeList>> recipesByGrade = new HashMap<>();

    public ItemRecipesDropDataTable() {
        load();
    }

    private void load() {
        GradedItemsDropDataTable.getInstance().getAllGradedItemsMap().forEach((gradeInfo, gradedItems) -> {
            List<L2RecipeList> recipes = gradedItems
                    .stream()
                    .map(gradedItem -> {
                        Optional<L2RecipeList> recipeOption = RecipeData.getInstance().getRecipeByProductionItem(gradedItem.getItemId());
                        if (!recipeOption.isPresent()) {
                            LOG.warn("Could not find recipe for item {}", gradedItem);
                        }
                        return recipeOption;
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            recipesByGrade.put(
                    gradeInfo,
                    recipes
            );
            recipeIds.addAll(recipes.stream().map(L2RecipeList::getRecipeId).collect(Collectors.toSet()));
        });
        LOG.info("Loaded {} item recipes for dynamic loot", recipeIds.size());
    }

    public List<L2RecipeList> getRecipesByGradeInfo(GradeInfo gradeInfo) {
        return recipesByGrade.get(gradeInfo);
    }

    public static ItemRecipesDropDataTable getInstance() {
        return ItemRecipesDropDataTable.SingletonHolder._instance;
    }

    public Set<Integer> getRecipeIds() {
        return recipeIds;
    }

    private static class SingletonHolder {
        protected static final ItemRecipesDropDataTable _instance = new ItemRecipesDropDataTable();
    }

}
