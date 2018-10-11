package com.l2jserver.util.misc;

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
import java.util.stream.Stream;

public class CategorizedItems {

    private final List<L2Weapon> nonMasterworkWeapons;
    private final List<L2Armor> nonMasterworkArmors;
    private final List<L2Armor> nonMasterworkJewels;

    private final List<ItemPart> weaponParts;
    private final List<ItemPart> armorParts;
    private final List<ItemPart> jewelParts;


    private final List<L2EtcItem> craftResources;

    private final List<L2EtcItem> recipes;
    private final List<L2EtcItem> weaponEnchantScrolls;
    private final List<L2EtcItem> armorEnchantScrolls;
    private final List<L2Item> resurrectionAndEscapeScrolls;

    private final List<L2Item> allItems;
    private final List<L2Item> allEquipment;
    private final Set<Integer> allIds;

    public CategorizedItems(List<L2Weapon> nonMasterworkWeapons,
                            List<L2Armor> nonMasterworkArmors,
                            List<L2Armor> nonMasterworkJewels,
                            List<ItemPart> weaponParts,
                            List<ItemPart> armorParts,
                            List<ItemPart> jewelParts,
                            List<L2EtcItem> craftResources, List<L2EtcItem> recipes,
                            List<L2EtcItem> weaponEnchantScrolls, List<L2EtcItem> armorEnchantScrolls) {
        this.nonMasterworkWeapons = nonMasterworkWeapons;
        this.nonMasterworkArmors = nonMasterworkArmors;
        this.nonMasterworkJewels = nonMasterworkJewels;
        this.weaponParts = weaponParts;
        this.armorParts = armorParts;
        this.jewelParts = jewelParts;
        this.craftResources = craftResources;
        this.recipes = recipes;
        this.weaponEnchantScrolls = weaponEnchantScrolls;
        this.armorEnchantScrolls = armorEnchantScrolls;

        this.resurrectionAndEscapeScrolls = Stream.of(
                736, // Scroll of Escape
                1830, // Scroll of Escape: Castle
                1829, // Scroll of Escape: Clan Hall
                1538, // Blessed Scroll of Escape
                5858, // Blessed Scroll of Escape: Clan Hall
                5859, // Blessed Scroll of Escape: Castle
                737, // Scroll of Resurrection
                6387, // Blessed Scroll of Resurrection for Pets
                3936// Blessed Scroll of Resurrection
        ).map(id -> ItemTable.getInstance().getTemplate(id)).collect(Collectors.toList());

        allItems = new ArrayList<>();
        allItems.addAll(nonMasterworkWeapons);
        allItems.addAll(nonMasterworkArmors);
        allItems.addAll(nonMasterworkJewels);
        allItems.addAll(weaponParts.stream().map(part -> ItemTable.getInstance().getTemplate(part.getPartId())).collect(Collectors.toList()));
        allItems.addAll(armorParts.stream().map(part -> ItemTable.getInstance().getTemplate(part.getPartId())).collect(Collectors.toList()));
        allItems.addAll(jewelParts.stream().map(part -> ItemTable.getInstance().getTemplate(part.getPartId())).collect(Collectors.toList()));
        allItems.addAll(craftResources);
        allItems.addAll(recipes);
        allItems.addAll(weaponEnchantScrolls);
        allItems.addAll(armorEnchantScrolls);
        allItems.addAll(resurrectionAndEscapeScrolls);

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

    public List<ItemPart> getWeaponParts() {
        return weaponParts;
    }

    public List<ItemPart> getArmorParts() {
        return armorParts;
    }

    public List<ItemPart> getJewelParts() {
        return jewelParts;
    }

    public List<L2EtcItem> getCraftResources() {
        return craftResources;
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

    public List<L2Item> getResurrectionAndEscapeScrolls() {
        return resurrectionAndEscapeScrolls;
    }

}
