package com.l2jserver.gameserver.datatables.categorized;

import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemRecipesDropDataTable {

    private static final Logger LOG = LoggerFactory.getLogger(ItemRecipesDropDataTable.class);

    private Set<Integer> recipeIds = new HashSet<>();
    private Map<GradeInfo, List<L2RecipeList>> weaponRecipesByGrade = new HashMap<>();
    private Map<GradeInfo, List<L2RecipeList>> armorRecipesByGrade = new HashMap<>();
    private Map<GradeInfo, List<L2RecipeList>> jewelRecipesByGrade = new HashMap<>();

    public ItemRecipesDropDataTable() {
        load();
    }

    private void load() {
        weaponRecipesByGrade = buildRecipeData(GradedItemsDropDataTable.getInstance().getGradedWeaponsMap());
        armorRecipesByGrade = buildRecipeData(GradedItemsDropDataTable.getInstance().getGradedArmorMap());
        jewelRecipesByGrade = buildRecipeData(GradedItemsDropDataTable.getInstance().getGradedJewelsMap());

        recipeIds = Stream.of(
                weaponRecipesByGrade.values(),
                armorRecipesByGrade.values(),
                jewelRecipesByGrade.values()
        )
                .flatMap(value -> value.stream().flatMap(List::stream))
                .map(L2RecipeList::getRecipeId)
                .collect(Collectors.toSet());

        LOG.info("Loaded {} item recipes for dynamic loot", recipeIds.size());
    }

    private Map<GradeInfo, List<L2RecipeList>> buildRecipeData(Map<GradeInfo, List<GradedItem>> getGradedWeaponsMap) {
        Map<GradeInfo, List<L2RecipeList>> recipesByGrade = new HashMap<>();
        getGradedWeaponsMap.forEach((gradeInfo, gradedItems) -> {
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
        });

        return recipesByGrade;
    }

    public List<L2RecipeList> getWeaponRecipesByGradeInfo(GradeInfo gradeInfo) {
        return weaponRecipesByGrade.getOrDefault(gradeInfo, new ArrayList<>());
    }

    public List<L2RecipeList> getArmorRecipesByGrade(GradeInfo gradeInfo) {
        return armorRecipesByGrade.getOrDefault(gradeInfo, new ArrayList<>());
    }

    public List<L2RecipeList> getJewelRecipesByGrade(GradeInfo gradeInfo) {
        return jewelRecipesByGrade.getOrDefault(gradeInfo, new ArrayList<>());
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
