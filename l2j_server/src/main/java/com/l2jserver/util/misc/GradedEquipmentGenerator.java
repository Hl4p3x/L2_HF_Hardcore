package com.l2jserver.util.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.Config;
import com.l2jserver.Server;
import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.categorized.CategorizedItems;
import com.l2jserver.gameserver.model.buylist.L2BuyList;
import com.l2jserver.gameserver.model.buylist.Product;
import com.l2jserver.gameserver.model.items.L2Armor;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.graded.Grade;
import com.l2jserver.gameserver.model.items.graded.GradeCategory;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.util.CollectionUtil;
import com.l2jserver.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GradedEquipmentGenerator {

    private static final List<Integer> blacklistIds = Arrays.asList(
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
            6368 // Shining Bow
    );

    private static CategorizedItems collectCategorizedItems() {
        Map<Integer, L2Weapon> weaponsMap = ItemTable.getInstance().getWeapons();
        Map<Integer, L2Armor> armorsMap = ItemTable.getInstance().getArmor();
        Map<Integer, L2EtcItem> etcItemsMap = ItemTable.getInstance().getEtcItems();

        Set<Integer> craftableIds = RecipeData.getInstance().getAllCraftableIds();

        List<L2Weapon> craftableWeapons = weaponsMap.values()
                .stream()
                .filter(weapon ->
                        !blacklistIds.contains(weapon.getId()) &&
                                craftableIds.contains(weapon.getId()) &&
                        !weapon.getCrystalType().equals(CrystalType.NONE))
                .collect(Collectors.toList());
        List<L2Armor> craftableArmors = armorsMap.values()
                .stream()
                .filter(armor ->
                        !blacklistIds.contains(armor.getId()) &&
                                (craftableIds.contains(armor.getId()) && !armor.isHair()) &&
                        !armor.isBracelet() && !armor.isCloak() && !armor.isBelt() &&
                        !armor.getCrystalType().equals(CrystalType.NONE))
                .collect(Collectors.toList());

        // Add NG
        BuyListData buyListData = BuyListData.getInstance();
        L2BuyList wizardArmorShopTopNg = buyListData.getBuyList(3008800);
        L2BuyList fighterArmorShopTopNg = buyListData.getBuyList(3008701);
        L2BuyList wizardArmorShopLopNg = buyListData.getBuyList(3055901);
        L2BuyList fighterArmorShopLopNg = buyListData.getBuyList(3055900);
        L2BuyList accessoryShop = buyListData.getBuyList(3000300);
        L2BuyList wizardWeaponShopTopNg = buyListData.getBuyList(3008500);
        L2BuyList fighterWeaponShopTopNg = buyListData.getBuyList(3008400);
        L2BuyList wizardWeaponShopLowNg = buyListData.getBuyList(3055801);
        L2BuyList fighterWeaponShopLowNg = buyListData.getBuyList(3055800);

        Stream<L2BuyList> shops = Stream.of(wizardArmorShopTopNg, fighterArmorShopTopNg, wizardArmorShopLopNg, fighterArmorShopLopNg, accessoryShop, fighterWeaponShopLowNg, fighterWeaponShopTopNg, wizardWeaponShopLowNg, wizardWeaponShopTopNg);
        List<L2Item> ngItemsFromShops = shops.flatMap(l2BuyList -> Product.convertToItems(l2BuyList.getProducts()).stream()).filter(item -> item.getCrystalType().equals(CrystalType.NONE)).collect(Collectors.toList());

        List<L2Weapon> weaponsFromShops = ngItemsFromShops.stream()
                .filter((L2Item item) -> item instanceof L2Weapon)
                .map(item -> (L2Weapon) item)
                .distinct()
                .collect(Collectors.toList());
        List<L2Armor> armorFromShops = ngItemsFromShops.stream()
                .filter((L2Item item) -> item instanceof L2Armor)
                .map(item -> (L2Armor) item)
                .distinct()
                .collect(Collectors.toList());

        craftableWeapons.addAll(weaponsFromShops);
        craftableArmors.addAll(armorFromShops);

        List<L2Armor> nonMasterworkArmors = craftableArmors.stream().filter(armor -> L2Item.TYPE2_ACCESSORY != armor.getType2()).collect(Collectors.toList());
        List<L2Armor> nonMasterworkJewels = craftableArmors.stream().filter(armor -> L2Item.TYPE2_ACCESSORY == armor.getType2()).collect(Collectors.toList());

        Set<String> weaponNames = craftableWeapons.stream().map(L2Weapon::getName).collect(Collectors.toSet());
        Set<String> armorNames = craftableArmors.stream().map(L2Armor::getName).collect(Collectors.toSet());
        Set<String> weaponsAndArmorNames = new HashSet<>(weaponNames.size() + armorNames.size());
        weaponsAndArmorNames.addAll(weaponNames);
        weaponsAndArmorNames.addAll(armorNames);

        List<L2EtcItem> allMaterials = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.MATERIAL == etcItem.getItemType()).collect(Collectors.toList());

        List<L2EtcItem> weaponAndArmorParts = allMaterials.stream().filter(material -> weaponsAndArmorNames.contains(StringUtil.removeLastWord(material.getName()))).collect(Collectors.toList());
        List<L2EtcItem> craftMaterials = allMaterials.stream().filter(material -> !weaponsAndArmorNames.contains(StringUtil.removeLastWord(material.getName()))).collect(Collectors.toList());

        List<L2EtcItem> recipes = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.RECIPE == etcItem.getItemType()).collect(Collectors.toList());
        List<L2EtcItem> weaponEnchants = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.SCRL_ENCHANT_WP == etcItem.getItemType()).collect(Collectors.toList());
        List<L2EtcItem> armorEnchants = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.SCRL_ENCHANT_AM == etcItem.getItemType()).collect(Collectors.toList());

        return new CategorizedItems(craftableWeapons, nonMasterworkArmors, nonMasterworkJewels, weaponAndArmorParts, craftMaterials, recipes, weaponEnchants, armorEnchants);
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
                .map(item -> new GradedItem(item.getId(), item.getName(), item.getReferencePrice(),
                        new GradeInfo(Grade.fromCrystalType(item.getCrystalType()), GradeCategory.UNSET)))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
        Config.DATAPACK_ROOT = new File("l2j_datapack/dist/game");
        Server.serverMode = Server.MODE_GAMESERVER;
        Config.load();

        List<GradedItem> allWeapon = convert(sort(collectCategorizedItems().getNonMasterworkWeapons()));
        List<GradedItem> allArmor = convert(sort(collectCategorizedItems().getNonMasterworkArmors()));
        List<GradedItem> allJewels = convert(sort(collectCategorizedItems().getNonMasterworkJewels()));

        gradeItems(allWeapon);
        gradeItems(allArmor);
        gradeItems(allJewels);

        List<GradedItem> allItems = new ArrayList<>();
        allItems.addAll(allWeapon);
        allItems.addAll(allArmor);
        allItems.addAll(allJewels);

        File gradedEquipment = new File("data/stats/categorized/graded_equipment.json");
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(gradedEquipment, allItems);
    }

    private static void gradeItems(List<GradedItem> itemsToBeGraded) {
        Map<Grade, Integer> gradeParts = new HashMap<>();
        gradeParts.put(Grade.NG, 3);
        gradeParts.put(Grade.D, 3);
        gradeParts.put(Grade.C, 3);
        gradeParts.put(Grade.B, 2);
        gradeParts.put(Grade.A, 2);
        gradeParts.put(Grade.S, 2);
        gradeParts.put(Grade.S80, 1);
        gradeParts.put(Grade.S84, 1);

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

        Multimap<Grade, GradedItem> gradedItemsByGrade = LinkedHashMultimap.create();
        itemsToBeGraded.forEach(item -> gradedItemsByGrade.put(item.getGradeInfo().getGrade(), item));

        gradedItemsByGrade.asMap().forEach((key, value) -> {
            List<List<GradedItem>> items = CollectionUtil.splitList(new ArrayList<>(value), gradeParts.get(key));
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
