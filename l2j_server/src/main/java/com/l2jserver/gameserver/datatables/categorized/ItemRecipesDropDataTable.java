package com.l2jserver.gameserver.datatables.categorized;

import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.datatables.categorized.interfaces.EquipmentProvider;
import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.items.graded.Grade;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemRecipesDropDataTable implements EquipmentProvider<L2RecipeList> {

    private static final Logger LOG = LoggerFactory.getLogger(ItemRecipesDropDataTable.class);

    private Set<Integer> recipeIds = new HashSet<>();
    private Map<GradeInfo, List<L2RecipeList>> weaponRecipesByGrade = new HashMap<>();
    private Map<GradeInfo, List<L2RecipeList>> armorRecipesByGrade = new HashMap<>();
    private Map<GradeInfo, List<L2RecipeList>> jewelRecipesByGrade = new HashMap<>();

    public ItemRecipesDropDataTable() {
        load();
    }

    public void load() {
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

    List<Integer> RECIPELESS_ITEMS = Arrays.asList(
            185, // Staff of Mana
            293, // War Hammer
            143, // Sword of Mystic
            296, // Dwarven Pike
            292, // Pike
            316, // Sage's Blood
            130, // Elven Sword
            86, // Tomahawk
            240, // Conjurer's Knife
            222, // Poniard Dagger
            179, // Mace of Prayer
            83, // Sword of Magic
            259, // Single-Edged Jamadhr
            144, // Sword of Occult
            186, // Staff of Magic
            127, // Crimson Sword
            223, // Kukuri
            238, // Dagger of Mana
            239, // Mystic Knife
            260, // Triple-Edged Jamadhr
            312, // Branch of Life
            178, // Bone Staff
            69, // Bastard Sword
            182, // Doom Hammer
            101, // Scroll of Wisdom
            258, // Bagh-Nakh
            275, // Long Bow
            220, // Crafted Dagger
            168, // Work Hammer
            183, // Mystic Staff
            126, // Artisan's Sword
            274, // Reinforced Bow
            166, // Heavy Mace
            156, // Hand Axe
            315, // Divine Tome
            314, // Proof of Revenge
            277, // Dark Elven Bow
            436, // Tunic of Knowledge
            469, // Stockings of Knowledge
            378, // Compound Scale Gaiters
            351, // Blast Plate
            416, // Reinforced Leather Gaiters
            349, // Compound Scale Mail
            394, // Reinforced Leather Shirt
            59, // Mithril Gaiters
            2446, // Reinforced Leather Gloves
            58, // Mithril Breastplate
            379, // Dwarven Scale Gaiters
            350, // Dwarven Scale Mail
            47, // Helmet
            2422, // Reinforced Leather Boots
            414, // Lion Skin Gaiters
            627, // Aspis
            2447, // Gloves of Knowledge
            465, // Cursed Stockings
            376, // Iron Plate Gaiters
            2423, // Boots of Knowledge
            605, // Leather Gauntlets
            1123, // Blue Buckskin Boots
            435, // Mystic's Tunic
            46, // Bronze Helmet
            391, // Puma Skin Shirt
            347, // Ring Mail Breastplate
            628, // Hoplon
            392, // Lion Skin Shirt
            468, // Mystic's Stockings
            413, // Puma Skin Gaiters
            432, // Cursed Tunic
            626, // Bronze Shield
            63, // Gauntlets
            912, // Near Forest Necklace
            880, // Black Pearl Ring
            847, // Red Crescent Earring
            890, // Ring of Devotion
            879, // Enchanted Ring
            848 // Enchanted Earring
    );


    private Map<GradeInfo, List<L2RecipeList>> buildRecipeData(Map<GradeInfo, List<GradedItem>> getGradedWeaponsMap) {
        Map<GradeInfo, List<L2RecipeList>> recipesByGrade = new HashMap<>();
        getGradedWeaponsMap.forEach((gradeInfo, gradedItems) -> {
            List<L2RecipeList> recipes = gradedItems
                    .stream()
                    .map(gradedItem -> {
                        Optional<L2RecipeList> recipeOption = RecipeData.getInstance().getRecipeByProductionItem(gradedItem.getItemId());
                        if (recipeOption.isEmpty() && !gradedItem.getGradeInfo().getGrade().equals(Grade.NG) && !RECIPELESS_ITEMS.contains(gradedItem.getItemId())) {
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

    public List<L2RecipeList> getWeaponsByGrade(GradeInfo gradeInfo) {
        return weaponRecipesByGrade.getOrDefault(gradeInfo, new ArrayList<>());
    }

    public List<L2RecipeList> getArmorByGrade(GradeInfo gradeInfo) {
        return armorRecipesByGrade.getOrDefault(gradeInfo, new ArrayList<>());
    }

    public List<L2RecipeList> getJewelsByGrade(GradeInfo gradeInfo) {
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
