package com.l2jserver.gameserver.datatables.categorized;

import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.items.L2Armor;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.parts.ItemPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CategorizedItems {

    private final List<L2Weapon> nonMasterworkWeapons;
    private final List<L2Armor> nonMasterworkArmors;
    private final List<L2Armor> nonMasterworkJewels;

    private final List<ItemPart> weaponAndArmorParts;
    private final List<L2EtcItem> craftMaterials;

    private final List<L2EtcItem> recipes;
    private final List<L2EtcItem> weaponEnchantScrolls;
    private final List<L2EtcItem> armorEnchantScrolls;

    private final List<L2Item> allItems;
    private final List<L2Item> allEquipment;
    private final Set<Integer> allIds;

    public CategorizedItems(List<L2Weapon> nonMasterworkWeapons, List<L2Armor> nonMasterworkArmors,
                            List<L2Armor> nonMasterworkJewels, List<ItemPart> weaponAndArmorParts,
                            List<L2EtcItem> craftMaterials, List<L2EtcItem> recipes,
                            List<L2EtcItem> weaponEnchantScrolls, List<L2EtcItem> armorEnchantScrolls) {
        this.nonMasterworkWeapons = nonMasterworkWeapons;
        this.nonMasterworkArmors = nonMasterworkArmors;
        this.nonMasterworkJewels = nonMasterworkJewels;
        this.weaponAndArmorParts = weaponAndArmorParts;
        this.craftMaterials = craftMaterials;
        this.recipes = recipes;
        this.weaponEnchantScrolls = weaponEnchantScrolls;
        this.armorEnchantScrolls = armorEnchantScrolls;

        allItems = new ArrayList<>();
        allItems.addAll(nonMasterworkWeapons);
        allItems.addAll(nonMasterworkArmors);
        allItems.addAll(nonMasterworkJewels);
        allItems.addAll(weaponAndArmorParts.stream().map(part -> ItemTable.getInstance().getTemplate(part.getPartId())).collect(Collectors.toList()));
        allItems.addAll(craftMaterials);
        allItems.addAll(recipes);
        allItems.addAll(weaponEnchantScrolls);
        allItems.addAll(armorEnchantScrolls);

        allEquipment = new ArrayList<>();
        allEquipment.addAll(nonMasterworkWeapons);
        allEquipment.addAll(nonMasterworkArmors);
        allEquipment.addAll(nonMasterworkJewels);

        allIds = allItems.stream().map(L2Item::getId).collect(Collectors.toSet());
    }

    public List<L2Weapon> getNonMasterworkWeapons() {
        return nonMasterworkWeapons;
    }

    public List<L2Armor> getNonMasterworkArmors() {
        return nonMasterworkArmors;
    }

    public List<L2Armor> getNonMasterworkJewels() {
        return nonMasterworkJewels;
    }

    public List<ItemPart> getWeaponAndArmorParts() {
        return weaponAndArmorParts;
    }

    public List<L2EtcItem> getCraftMaterials() {
        return craftMaterials;
    }

    public List<L2EtcItem> getRecipes() {
        return recipes;
    }

    public List<L2EtcItem> getWeaponEnchantScrolls() {
        return weaponEnchantScrolls;
    }

    public List<L2EtcItem> getArmorEnchantScrolls() {
        return armorEnchantScrolls;
    }

    public List<L2Item> getAllItems() {
        return allItems;
    }

    public Set<Integer> getAllIds() {
        return allIds;
    }

    public List<L2Item> getAllEquipment() {
        return allEquipment;
    }

}
