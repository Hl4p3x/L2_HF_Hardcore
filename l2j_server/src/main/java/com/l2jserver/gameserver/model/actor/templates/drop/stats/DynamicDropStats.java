package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.WeaponArmorJewelStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourcesStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.AllScrollsStats;

import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropStats {

    private WeaponArmorJewelStats equipment;
    private WeaponArmorJewelStats parts;
    private WeaponArmorJewelStats recipes;
    private ResourcesStats resources;
    private AllScrollsStats scrolls;

    public DynamicDropStats() {
    }

    public DynamicDropStats(WeaponArmorJewelStats equipment, WeaponArmorJewelStats parts, WeaponArmorJewelStats recipes, ResourcesStats resources, AllScrollsStats scrolls) {
        this.equipment = equipment;
        this.parts = parts;
        this.recipes = recipes;
        this.resources = resources;
        this.scrolls = scrolls;
    }

    public WeaponArmorJewelStats getEquipment() {
        return equipment;
    }

    public WeaponArmorJewelStats getParts() {
        return parts;
    }

    public WeaponArmorJewelStats getRecipes() {
        return recipes;
    }

    public ResourcesStats getResources() {
        return resources;
    }

    public AllScrollsStats getScrolls() {
        return scrolls;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DynamicDropStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(equipment))
                .add(Objects.toString(parts))
                .add(Objects.toString(recipes))
                .add(Objects.toString(resources))
                .add(Objects.toString(scrolls))
                .toString();
    }

}
