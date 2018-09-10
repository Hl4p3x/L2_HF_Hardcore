package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.items.L2Armor;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.type.EtcItemType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicDropCalculator {

  /*  public static Collection<ItemHolder> calculate(L2Character victim, L2Character killer) {
    }
*/


    public static Map<String, List<L2Item>> collectPossibleDropByCategory() {
        Map<Integer, L2Weapon> weaponsMap = ItemTable.getInstance().getWeapons();
        Map<Integer, L2Armor> armorsMap = ItemTable.getInstance().getArmor();
        Map<Integer, L2EtcItem> etcItemsMap = ItemTable.getInstance().getEtcItems();

        Set<Integer> masterworkIds = RecipeData.getInstance().getAllMasterworkItemIds();

        List<L2Weapon> nonMasterworkWeapons = weaponsMap.values().stream().filter(weapon -> !masterworkIds.contains(weapon.getId())).collect(Collectors.toList());
        List<L2Armor> nonMasterworkAllArmors = armorsMap.values().stream().filter(armor -> !masterworkIds.contains(armor.getId())).collect(Collectors.toList());

        List<L2Armor> nonMasterworkArmors = nonMasterworkAllArmors.stream().filter(armor -> L2Item.TYPE2_ACCESSORY != armor.getType2()).collect(Collectors.toList());
        List<L2Armor> nonMasterworkJewels = nonMasterworkAllArmors.stream().filter(armor -> L2Item.TYPE2_ACCESSORY == armor.getType2()).collect(Collectors.toList());


        Set<String> weaponNames = nonMasterworkWeapons.stream().map(L2Weapon::getName).collect(Collectors.toSet());
        Set<String> armorNames = nonMasterworkAllArmors.stream().map(L2Armor::getName).collect(Collectors.toSet());
        Set<String> weaponsAndArmorNames = new HashSet<>(weaponNames.size() + armorNames.size());
        weaponsAndArmorNames.addAll(weaponNames);
        weaponsAndArmorNames.addAll(armorNames);

        List<L2EtcItem> allMaterials = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.MATERIAL == etcItem.getItemType()).collect(Collectors.toList());

        List<L2EtcItem> weaponAndArmorParts = allMaterials.stream().filter(material -> weaponsAndArmorNames.contains(removeLastWord(material.getName()))).collect(Collectors.toList());
        List<L2EtcItem> craftMaterials = allMaterials.stream().filter(material -> !weaponsAndArmorNames.contains(removeLastWord(material.getName()))).collect(Collectors.toList());

        List<L2EtcItem> recipes = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.RECIPE == etcItem.getItemType()).collect(Collectors.toList());
        List<L2EtcItem> weaponEnchants = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.SCRL_ENCHANT_WP == etcItem.getItemType()).collect(Collectors.toList());
        List<L2EtcItem> armorEnchants = etcItemsMap.values().stream().filter(etcItem -> EtcItemType.SCRL_ENCHANT_AM == etcItem.getItemType()).collect(Collectors.toList());


    }

    public static String removeLastWord(String text) {
        return removeLastWord(text, " ");
    }

    public static String removeLastWord(String text, String wordSeparator) {
        int lastWhitespace = text.lastIndexOf(wordSeparator);
        if (lastWhitespace == -1) {
            return text;
        }

        return text.substring(0, lastWhitespace + 1);
    }


}
