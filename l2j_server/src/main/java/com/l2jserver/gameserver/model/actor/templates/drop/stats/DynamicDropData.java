package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.EquipmentDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.AllScrollsDropData;

import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropData {

    private EquipmentDropData equipment;
    private EquipmentDropData parts;
    private EquipmentDropData recipes;
    private ResourceDropData resources;
    private AllScrollsDropData scrolls;

    public DynamicDropData() {
    }

    public DynamicDropData(EquipmentDropData equipment, EquipmentDropData parts, EquipmentDropData recipes, ResourceDropData resources, AllScrollsDropData scrolls) {
        this.equipment = equipment;
        this.parts = parts;
        this.recipes = recipes;
        this.resources = resources;
        this.scrolls = scrolls;
    }

    public EquipmentDropData getEquipment() {
        return equipment;
    }

    public EquipmentDropData getParts() {
        return parts;
    }

    public EquipmentDropData getRecipes() {
        return recipes;
    }

    public ResourceDropData getResources() {
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
