package com.l2jserver.gameserver.datatables.categorized;

import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemRecipesData {

    private static final Logger LOG = LoggerFactory.getLogger(ItemRecipesData.class);

    private Map<GradeInfo, List<L2RecipeList>> recipesByGrade = new HashMap<>();

    public ItemRecipesData() {
        load();
    }

    private void load() {
        GradedItemsData.getInstance().getGradedItemsMap().forEach((gradeInfo, gradedItems) -> {
            List<L2RecipeList> recipeIds = gradedItems
                    .stream()
                    .map(gradedItem -> {
                        Optional<L2RecipeList> recipeOption = RecipeData.getInstance().getRecipeByProductionItem(gradedItem.getItemId());
                        if (!recipeOption.isPresent()) {
                            LOG.warn("Could not find recipe for item ", gradedItem);
                        }
                        return recipeOption;
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            recipesByGrade.put(
                    gradeInfo,
                    recipeIds
            );
        });
    }

    public List<L2RecipeList> getRecipesByGradeInfo(GradeInfo gradeInfo) {
        return recipesByGrade.get(gradeInfo);
    }

    public static ItemRecipesData getInstance() {
        return ItemRecipesData.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final ItemRecipesData _instance = new ItemRecipesData();
    }

}
