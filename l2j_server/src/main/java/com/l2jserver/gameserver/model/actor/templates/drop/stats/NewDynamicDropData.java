package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.NewEquipmentDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.NewAllScrollsDropData;

import java.util.Objects;
import java.util.StringJoiner;

public class NewDynamicDropData {

    private NewEquipmentDropData equipment;
    private NewEquipmentDropData parts;
    private NewEquipmentDropData recipes;
    private ResourceDropData resources;
    private NewAllScrollsDropData scrolls;

    public NewDynamicDropData() {
    }

    public NewDynamicDropData(NewEquipmentDropData equipment, NewEquipmentDropData parts, NewEquipmentDropData recipes, ResourceDropData resources, NewAllScrollsDropData scrolls) {
        this.equipment = equipment;
        this.parts = parts;
        this.recipes = recipes;
        this.resources = resources;
        this.scrolls = scrolls;
    }

    public NewEquipmentDropData getEquipment() {
        return equipment;
    }

    public NewEquipmentDropData getParts() {
        return parts;
    }

    public NewEquipmentDropData getRecipes() {
        return recipes;
    }

    public ResourceDropData getResources() {
        return resources;
    }

    public NewAllScrollsDropData getScrolls() {
        return scrolls;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NewDynamicDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(equipment))
                .add(Objects.toString(parts))
                .add(Objects.toString(recipes))
                .add(Objects.toString(resources))
                .add(Objects.toString(scrolls))
                .toString();
    }

}
