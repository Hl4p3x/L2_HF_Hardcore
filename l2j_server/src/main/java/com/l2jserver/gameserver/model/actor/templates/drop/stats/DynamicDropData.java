package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.EquipmentDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.AllScrollsDropData;

import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropData {

    private EquipmentDropStats equipment;
    private EquipmentDropStats parts;
    private EquipmentDropStats recipes;
    private ResourceDropStats resources;
    private AllScrollsDropData scrolls;

    public DynamicDropData() {
    }

    public DynamicDropData(EquipmentDropStats equipment, EquipmentDropStats parts, EquipmentDropStats recipes, ResourceDropStats resources, AllScrollsDropData scrolls) {
        this.equipment = equipment;
        this.parts = parts;
        this.recipes = recipes;
        this.resources = resources;
        this.scrolls = scrolls;
    }

    public EquipmentDropStats getEquipment() {
        return equipment;
    }

    public EquipmentDropStats getParts() {
        return parts;
    }

    public EquipmentDropStats getRecipes() {
        return recipes;
    }

    public ResourceDropStats getResources() {
        return resources;
    }

    public AllScrollsDropData getScrolls() {
        return scrolls;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DynamicDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(equipment))
                .add(Objects.toString(parts))
                .add(Objects.toString(recipes))
                .add(Objects.toString(resources))
                .add(Objects.toString(scrolls))
                .toString();
    }

}
