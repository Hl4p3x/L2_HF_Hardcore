package com.l2jserver.util.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.Config;
import com.l2jserver.Server;
import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.L2RecipeInstance;
import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;
import com.l2jserver.gameserver.model.buylist.L2BuyList;
import com.l2jserver.gameserver.model.buylist.Product;
import com.l2jserver.gameserver.model.items.*;
import com.l2jserver.gameserver.model.items.craft.CraftResource;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;
import com.l2jserver.gameserver.model.items.graded.Grade;
import com.l2jserver.gameserver.model.items.graded.GradeCategory;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.gameserver.model.items.parts.ItemPart;
import com.l2jserver.gameserver.model.items.scrolls.CategorizedScrolls;
import com.l2jserver.gameserver.model.items.scrolls.MiscScroll;
import com.l2jserver.gameserver.model.items.scrolls.Scroll;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.model.multisell.Ingredient;
import com.l2jserver.gameserver.model.multisell.ListContainer;
import com.l2jserver.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GradedEquipmentGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(GradedEquipmentGenerator.class);

    private static final List<Integer> BLACKLISTED_EQUIPMENT = Arrays.asList(
            2465, // Chain Gloves of Silence
            2467, // Gloves of Blessing
            2466, // Guardian's Gloves
            612, // Sealed Zubei's Gauntlets
            595, // Boots of Blessing
            572, // Boots of Silence
            578, // Guardian's Boots
            2480, // Elemental Gloves
            2481, // Gloves of Grace
            2485, // Implosion Gauntlets
            2486, // Paradia Gloves
            590, // Boots of Grace
            588, // Elemental Boots
            564, // Implosion Boots
            582, // Paradia Boots
            324, // Tears of Fairy
            327, // Hex Doll
            329, // Blessed Branch
            328, // Candle of Wisdom
            331, // Cerberus Eye
            333, // Claws of Black Dragon
            330, // Phoenix Feather
            332, // Scroll of Destruction
            334, // Three Eyed Crow's Feather
            325, // Horn of Glory
            230, // Wolverine Needle
            531, // Paradia Hood
            541, // Phoenix Hood
            549, // Helm of Avadon
            551, // Helmet of Pledge
            543, // Hood of Aid
            539, // Hood of Grace
            533, // Hood of Solar Eclipse
            535, // Hood of Summoning
            529, // Cap of Mana
            537, // Elemental Hood
            545, // Flame Helm
            1128, // Adamantite Boots
            609, // Gauntlets of Ghost
            2468, // Blessed Gloves
            1127, // Forgotten Boots
            1126, // Crimson Boots
            1120, // Pa'agrian Hand
            319, // Eye of Infinity
            320, // Blue Crystal Skull
            323, // Ancient Reagent
            322, // Vajra Wands
            6368, // Shining Bow
            891, // Sage's Ring
            922, // Sage's Necklace
            9577, // Cotton Shirt
            9583, // Striped Cotton Shirt
            9578, // Linen Shirt
            9584 // Stripe Linen Shirt
    );

    private static final List<Integer> BLACKLISTED_MATERIALS = Arrays.asList(
            8658, // Mysterious Solvent
            8355, // Blue Colored Fish Scale
            8358, // Purple Colored Fish Scale
            8351, // Black Colored Fish Scale
            6905, // Hair Pin Piece
            8376, // Red Horn of Victory Fragment
            2012 // Sword of Reflection Blade
    );
    // Magic constant of full armor 0.616 to chest price ration
    private static final double FULL_TO_CHEST_PRICE_RATION = 0.616;

    private static CategorizedItems collectCategorizedItems() {
        Map<Integer, L2Weapon> weaponsMap = ItemTable.getInstance().getWeapons();
        Map<Integer, L2Armor> armorsMap = ItemTable.getInstance().getArmor();
        Map<Integer, L2EtcItem> etcItemsMap = ItemTable.getInstance().getEtcItems();

        Set<Integer> craftableIds = RecipeData.getInstance().getAllCraftableIds();

        List<L2Weapon> craftableWeapons = weaponsMap.values()
                .stream()
                .filter(weapon ->
                        !BLACKLISTED_EQUIPMENT.contains(weapon.getId()) &&
                                craftableIds.contains(weapon.getId()) &&
                        !weapon.getCrystalType().equals(CrystalType.NONE))
                .collect(Collectors.toList());

        List<L2Armor> craftableEquipment = armorsMap.values()
                .stream()
                .filter(armor ->
                        !BLACKLISTED_EQUIPMENT.contains(armor.getId()) &&
                                (craftableIds.contains(armor.getId()) && !armor.isHair()) &&
                        !armor.isBracelet() && !armor.isCloak() && !armor.isBelt() &&
                        !armor.getCrystalType().equals(CrystalType.NONE))
                .collect(Collectors.toList());

        List<L2Armor> hairAccessories = armorsMap.values().stream()
                .filter(armor ->
                        !BLACKLISTED_EQUIPMENT.contains(armor.getId()) && armor.isHair())
                .collect(Collectors.toList());

        // Add NG
        BuyListData buyListData = BuyListData.getInstance();
        L2BuyList wizardArmorShopTop = buyListData.getBuyList(3008800);
        L2BuyList fighterArmorShopTop = buyListData.getBuyList(3008701);
        L2BuyList wizardArmorShopLow = buyListData.getBuyList(3055901);
        L2BuyList fighterArmorShopLow = buyListData.getBuyList(3055900);
        L2BuyList accessoryShopLow = buyListData.getBuyList(3000300);
        L2BuyList accessoryShopTop = buyListData.getBuyList(3009000);
        L2BuyList wizardWeaponShopTop = buyListData.getBuyList(3008500);
        L2BuyList fighterWeaponShopTop = buyListData.getBuyList(3008400);
        L2BuyList wizardWeaponShopLow = buyListData.getBuyList(3055801);
        L2BuyList fighterWeaponShopLow = buyListData.getBuyList(3055800);

        Stream<L2BuyList> shops = Stream.of(wizardArmorShopTop, fighterArmorShopTop, wizardArmorShopLow, fighterArmorShopLow, accessoryShopLow, accessoryShopTop, fighterWeaponShopLow, fighterWeaponShopTop, wizardWeaponShopLow, wizardWeaponShopTop);
        List<L2Item> itemsFromShops = shops.flatMap(l2BuyList -> Product.convertToItems(l2BuyList.getProducts()).stream()).filter(item -> !BLACKLISTED_EQUIPMENT.contains(item.getId())).collect(Collectors.toList());

        List<L2Weapon> weaponsFromShops = itemsFromShops.stream()
                .filter((L2Item item) -> item instanceof L2Weapon)
                .map(item -> (L2Weapon) item)
                .distinct()
                .collect(Collectors.toList());

        List<L2Armor> armorFromShops = itemsFromShops.stream()
                .filter((L2Item item) -> item instanceof L2Armor)
                .map(item -> (L2Armor) item)
                .distinct()
                .collect(Collectors.toList());

        craftableWeapons.addAll(weaponsFromShops);
        craftableWeapons = craftableWeapons.stream().distinct().collect(Collectors.toList());

        craftableEquipment.addAll(armorFromShops);
        craftableEquipment = craftableEquipment.stream().distinct().collect(Collectors.toList());

        List<L2Armor> craftableArmor = craftableEquipment.stream().filter(armor -> L2Item.TYPE2_ACCESSORY != armor.getType2()).collect(Collectors.toList());
        List<L2Armor> craftableJewels = craftableEquipment.stream().filter(armor -> L2Item.TYPE2_ACCESSORY == armor.getType2()).collect(Collectors.toList());

        List<ItemPart> weaponParts =
                craftableWeapons.stream()
                        .map(GradedEquipmentGenerator::findPartByItem)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        List<ItemPart> armorParts =
                craftableArmor.stream()
                        .map(GradedEquipmentGenerator::findPartByItem)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        List<ItemPart> jewelParts =
                craftableJewels.stream()
                        .map(GradedEquipmentGenerator::findPartByItem)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        List<ItemPart> blacklistedParts = BLACKLISTED_EQUIPMENT.stream()
                .map(id -> ItemTable.getInstance().getTemplate(id)).map(GradedEquipmentGenerator::findPartByItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<ItemPart> accessoriesParts = hairAccessories.stream()
                .map(GradedEquipmentGenerator::findPartByItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        ListContainer fishermanMultisell = MultisellData.getInstance().getEntries().get(9);
        Set<Integer> fishingCraftIds = fishermanMultisell.getEntries()
                .stream().flatMap(entry -> Stream.concat(
                        CollectionUtil.extract(entry.getIngredients(), Ingredient::getItemId).stream(), CollectionUtil.extract(entry.getProducts(), Ingredient::getItemId).stream())
                ).filter(id -> !id.equals(Adena.ID)).collect(Collectors.toSet());

        List<L2EtcItem> recipes = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.RECIPE == etcItem.getItemType()).collect(Collectors.toList());
        Set<Integer> partsIds = CollectionUtil.extract(Stream.of(weaponParts, armorParts, jewelParts).flatMap(List::stream).collect(Collectors.toList()), ItemPart::getPartId);
        Set<Integer> recipeIds = CollectionUtil.extractIds(recipes);
        Set<Integer> blacklistedPartIds = CollectionUtil.extract(blacklistedParts, ItemPart::getPartId);
        Set<Integer> accessoriesPartsIds = CollectionUtil.extract(accessoriesParts, ItemPart::getPartId);

        Set<Integer> allNonResourceCraftIds = new HashSet<>(
                partsIds.size() +
                        recipeIds.size() +
                        blacklistedPartIds.size() +
                        fishingCraftIds.size() +
                        accessoriesPartsIds.size()
        );
        allNonResourceCraftIds.addAll(partsIds);
        allNonResourceCraftIds.addAll(recipeIds);
        allNonResourceCraftIds.addAll(blacklistedPartIds);
        allNonResourceCraftIds.addAll(fishingCraftIds);
        allNonResourceCraftIds.addAll(accessoriesPartsIds);

        List<L2EtcItem> allIngredients = RecipeData.getInstance().getAllIngredientIds().stream().map(id -> ItemTable.getInstance().getEtcItems().get(id)).collect(Collectors.toList());
        List<L2EtcItem> craftMaterials = allIngredients.stream().filter(resource -> !BLACKLISTED_MATERIALS.contains(resource.getId()) && resource.getReferencePrice() > 0 && !resource.getName().contains("(Not In Use)") && resource.getItemType() == EtcItemType.MATERIAL && !allNonResourceCraftIds.contains(resource.getId())).collect(Collectors.toList());

        List<L2EtcItem> weaponEnchants = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.SCRL_ENCHANT_WP == etcItem.getItemType() || EtcItemType.BLESS_SCRL_ENCHANT_WP == etcItem.getItemType()).collect(Collectors.toList());
        List<L2EtcItem> armorEnchants = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.SCRL_ENCHANT_AM == etcItem.getItemType() || EtcItemType.BLESS_SCRL_ENCHANT_AM == etcItem.getItemType()).collect(Collectors.toList());

        return new CategorizedItems(craftableWeapons, craftableArmor, craftableJewels, weaponParts, armorParts, jewelParts, craftMaterials, recipes, weaponEnchants, armorEnchants);
    }

    private static ItemPart findPartByItem(L2Item item) {
        Optional<L2RecipeList> recipeList = RecipeData.getInstance().getRecipeByProductionItem(item.getId());
        if (!recipeList.isPresent()) {
            return null;
        }

        Optional<ItemPart> hardcodedPart = HardcodedPartsForMismatchedItems.PARTS.stream().filter(part -> part.getItemId() == item.getId()).findFirst();
        if (hardcodedPart.isPresent()) {
            return hardcodedPart.get();
        }

        Optional<L2RecipeInstance> instance = Stream.of(recipeList.get().getRecipes())
                .filter(recipe -> {
                    L2Item recipeItem = ItemTable.getInstance().getTemplate(recipe.getItemId());
                    return (crossContainsIgnoreCase(removeEndingS(joinLongbowWorkTogether(item.getName())), removeEndingS(joinLongbowWorkTogether(recipeItem.getName())))
                            || recipeItem.getName().toLowerCase().contains("Sealed".toLowerCase())
                    ) && !recipeItem.getName().toLowerCase().contains("Recipe".toLowerCase());
                })
                .findFirst();
        if (instance.isPresent()) {
            return instance.map(l2RecipeInstance -> new ItemPart(
                    item.getId(), item.getName(),
                    l2RecipeInstance.getItemId(), ItemTable.getInstance().getTemplate(l2RecipeInstance.getItemId()).getName())
            ).orElse(null);
        } else {
            LOG.warn("Could not find part for {}", item);
            return null;
        }
    }

    private static boolean crossContainsIgnoreCase(String left, String right) {
        return left.toLowerCase().contains(right.toLowerCase()) ||
                right.toLowerCase().contains(left.toLowerCase());
    }

    private static String removeEndingS(String text) {
        char character = text.charAt(text.length() - 1);
        if (character == 's' || character == 'S') {
            return text.substring(0, text.length() - 1);
        }
        {
            return text;
        }
    }

    private static String joinLongbowWorkTogether(String text) {
        return text.replaceAll("(?i)Long bow", "Longbow");
    }

    private static <T extends L2Item> List<L2Item> sort(List<T> items) {
        Comparator<L2Item> crystalCompare = Comparator.comparing(item -> item.getCrystalType().getId());
        Comparator<L2Item> bodyPartCompare = Comparator.comparing(L2Item::getBodyPart);
        Comparator<L2Item> priceCompare = Comparator.comparing(L2Item::getReferencePrice);
        Comparator<L2Item> nameCompare = Comparator.comparing(L2Item::getName);

        return items.stream()
                .sorted(
                        crystalCompare.thenComparing(priceCompare).thenComparing(bodyPartCompare).thenComparing(nameCompare)
                ).collect(Collectors.toList());
    }

    private static List<GradedItem> convert(List<L2Item> items) {
        return items.stream()
                .map(item -> {
                    int bodyPart = item.getBodyPart();
                    int price = item.getReferencePrice();
                    // Grade full armor as a partly priced chest armor
                    if (bodyPart == L2Item.SLOT_FULL_ARMOR) {
                        bodyPart = L2Item.SLOT_CHEST;
                        price = (int) Math.round(price * FULL_TO_CHEST_PRICE_RATION);
                    }
                    return new GradedItem(item.getId(), item.getName(), price, bodyPart,
                            new GradeInfo(Grade.fromCrystalType(item.getCrystalType()), GradeCategory.UNSET));
                })
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
        Config.DATAPACK_ROOT = new File("l2j_datapack/dist/game");
        Server.serverMode = Server.MODE_GAMESERVER;
        Config.load();

        CategorizedItems categorizedItems = collectCategorizedItems();

        List<GradedItem> allWeapon = regradeDynastyToS80(convert(sort(categorizedItems.getNonMasterworkWeapons())));
        List<GradedItem> allArmor = regradeDynastyToS80(convert(sort(categorizedItems.getNonMasterworkArmors())));
        List<GradedItem> allJewels = regradeDynastyToS80(convert(sort(categorizedItems.getNonMasterworkJewels())));

        gradeItems(allWeapon, weaponsGradeParts());
        gradeItems(allArmor, commonGradeParts());
        gradeItems(allJewels, commonGradeParts());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("data/stats/categorized/graded_weapon.json"), allWeapon);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("data/stats/categorized/graded_armor.json"), allArmor);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("data/stats/categorized/graded_jewels.json"), allJewels);

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("data/stats/categorized/parts/weapon_parts.json"), categorizedItems.getWeaponParts());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("data/stats/categorized/parts/armor_parts.json"), categorizedItems.getArmorParts());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("data/stats/categorized/parts/jewel_parts.json"), categorizedItems.getJewelParts());

        List<CraftResource> gradedCraftResources = gradeCraftResources(categorizedItems.getCraftResources());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("data/stats/categorized/craft_resources.json"), gradedCraftResources);

        Set<Integer> blacklistedWeaponScrolls = new HashSet<>(Arrays.asList(
                20517, // Scroll: Enchant Weapon (S-Grade)
                22006, // Scroll: Enchant Weapon (C-Grade)
                22007, // Scroll: Enchant Weapon (C-Grade)
                22008, // Scroll: Enchant Weapon (B-Grade)
                22009 // Scroll: Enchant Weapon (A-Grade)
        ));

        Set<Integer> blacklistedArmorScrolls = new HashSet<>(Arrays.asList(
                20518, // Scroll: Enchant Armor (S-Grade)
                22010, // Scroll: Enchant Armor (C-Grade)
                22011, // Scroll: Enchant Armor (C-Grade)
                22012, // Scroll: Enchant Armor (B-Grade)
                22013 // Scroll: Enchant Armor (A-Grade)
        ));

        List<Scroll> armorScrolls = categorizedItems.getArmorEnchantScrolls()
                .stream().map(Scroll::armorScrollFromEtc)
                .filter(scroll -> !blacklistedArmorScrolls.contains(scroll.getId()) && !scroll.getGrade().equals(ScrollGrade.UNSET))
                .sorted(Comparator.comparing(Scroll::getId))
                .collect(Collectors.toList());

        List<Scroll> weaponScrolls = categorizedItems.getWeaponEnchantScrolls()
                .stream().map(Scroll::weaponScrollFromEtc)
                .filter(scroll -> !blacklistedWeaponScrolls.contains(scroll.getId()) && !scroll.getGrade().equals(ScrollGrade.UNSET))
                .sorted(Comparator.comparing(Scroll::getId))
                .collect(Collectors.toList());

        List<MiscScroll> miscScrolls = categorizedItems.getResurrectionAndEscapeScrolls().stream().map(MiscScroll::fromItem).collect(Collectors.toList());

        CategorizedScrolls categorizedScrolls = new CategorizedScrolls(
                weaponScrolls.stream().filter(Scroll::isNotBlessed).collect(Collectors.toList()),
                weaponScrolls.stream().filter(Scroll::isBlessed).collect(Collectors.toList()),
                armorScrolls.stream().filter(Scroll::isNotBlessed).collect(Collectors.toList()),
                armorScrolls.stream().filter(Scroll::isBlessed).collect(Collectors.toList()),
                miscScrolls.stream().filter(MiscScroll::isNotBlessed).collect(Collectors.toList()),
                miscScrolls.stream().filter(MiscScroll::isBlessed).collect(Collectors.toList())
        );
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("data/stats/categorized/scrolls.json"), categorizedScrolls);
    }

    private static List<CraftResource> gradeCraftResources(List<L2EtcItem> craftResources) {
        List<ResourceGrade> resourceGrades = Arrays.asList(ResourceGrade.values());

        Comparator<L2EtcItem> priceCompare = Comparator.comparing(L2EtcItem::getReferencePrice);
        Comparator<L2EtcItem> nameCompare = Comparator.comparing(L2EtcItem::getName);
        craftResources.sort(priceCompare.thenComparing(nameCompare));

        List<CraftResource> result = new ArrayList<>(craftResources.size());
        List<List<L2EtcItem>> splitItems = CollectionUtil.splitList(craftResources, resourceGrades.size() - 1);
        int gradeIndex = 0;
        for (List<L2EtcItem> items : splitItems) {
            for (L2EtcItem item : items) {
                result.add(new CraftResource(item.getId(), item.getName(), item.getReferencePrice(), resourceGrades.get(gradeIndex)));
            }
            gradeIndex += 1;
        }
        return result;
    }

    private static List<GradedItem> regradeDynastyToS80(List<GradedItem> items) {
        items.stream().filter(item -> item.getItemName().contains("Dynasty")).forEach(item -> item.getGradeInfo().setGrade(Grade.S80));
        return items;
    }

    private static Map<Grade, Integer> commonGradeParts() {
        Map<Grade, Integer> gradeParts = new HashMap<>();
        gradeParts.put(Grade.NG, 3);
        gradeParts.put(Grade.D, 3);
        gradeParts.put(Grade.C, 3);
        gradeParts.put(Grade.B, 2);
        gradeParts.put(Grade.A, 2);
        gradeParts.put(Grade.S, 1);
        gradeParts.put(Grade.S80, 2); // Dynasty is regraded from S to S80
        gradeParts.put(Grade.S84, 1);
        return gradeParts;
    }

    private static Map<Grade, Integer> weaponsGradeParts() {
        Map<Grade, Integer> gradeParts = new HashMap<>();
        gradeParts.put(Grade.NG, 3);
        gradeParts.put(Grade.D, 3);
        gradeParts.put(Grade.C, 3);
        gradeParts.put(Grade.B, 2);
        gradeParts.put(Grade.A, 3);
        gradeParts.put(Grade.S, 1);
        gradeParts.put(Grade.S80, 2); // Dynasty is regraded from S to S80
        gradeParts.put(Grade.S84, 1);
        return gradeParts;
    }

    private static Map<Integer, Map<Integer, GradeCategory>> categoriesForParts() {
        Map<Integer, Map<Integer, GradeCategory>> categoriesForParts = new HashMap<>();
        Map<Integer, GradeCategory> single = new HashMap<>();
        single.put(1, GradeCategory.ALL);
        categoriesForParts.put(1, single);

        Map<Integer, GradeCategory> twoPart = new HashMap<>();
        twoPart.put(1, GradeCategory.LOW);
        twoPart.put(2, GradeCategory.TOP);
        categoriesForParts.put(2, twoPart);

        Map<Integer, GradeCategory> threePart = new HashMap<>();
        threePart.put(1, GradeCategory.LOW);
        threePart.put(2, GradeCategory.MID);
        threePart.put(3, GradeCategory.TOP);
        categoriesForParts.put(3, threePart);
        return categoriesForParts;
    }

    private static void gradeItems(List<GradedItem> itemsToBeGraded, Map<Grade, Integer> gradeParts) {
        Map<Integer, Map<Integer, GradeCategory>> categoriesForParts = categoriesForParts();

        Multimap<GradeAndBody, GradedItem> gradedItemsByGrade = LinkedHashMultimap.create();
        itemsToBeGraded.forEach(item -> gradedItemsByGrade.put(new GradeAndBody(item.getGradeInfo().getGrade(), item.getItemSlot()), item));

        gradedItemsByGrade.asMap().forEach((key, value) -> {
            List<List<GradedItem>> items = CollectionUtil.splitList(new ArrayList<>(value), gradeParts.get(key.getGrade()));
            Map<Integer, GradeCategory> categoryMapper = categoriesForParts.get(items.size());
            int grade = 1;
            for (List<GradedItem> gradedItem : items) {
                for (GradedItem it : gradedItem) {
                    it.getGradeInfo().setCategory(categoryMapper.get(grade));
                }
                grade += 1;
            }
        });
    }

}
