package com.l2jserver.gameserver.datatables.categorized;

import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.items.L2Armor;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.util.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CategorizedDataTable {

    private final CategorizedItems categorizedItems;

    public CategorizedDataTable() {
        this.categorizedItems = collectCategorizedItems();
    }

    public CategorizedItems getCategorizedItems() {
        return categorizedItems;
    }

    private CategorizedItems collectCategorizedItems() {
        Map<Integer, L2Weapon> weaponsMap = ItemTable.getInstance().getWeapons();
        Map<Integer, L2Armor> armorsMap = ItemTable.getInstance().getArmor();
        Map<Integer, L2EtcItem> etcItemsMap = ItemTable.getInstance().getEtcItems();

        Set<Integer> craftableIds = RecipeData.getInstance().getAllCraftableIds();

        List<L2Weapon> craftableWeapons = weaponsMap.values().stream().filter(weapon -> craftableIds.contains(weapon.getId())).collect(Collectors.toList());
        List<L2Armor> craftableArmors = armorsMap.values().stream().filter(armor -> craftableIds.contains(armor.getId())).collect(Collectors.toList());

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

    public static CategorizedDataTable getInstance() {
        return CategorizedDataTable.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final CategorizedDataTable _instance = new CategorizedDataTable();
    }

}
