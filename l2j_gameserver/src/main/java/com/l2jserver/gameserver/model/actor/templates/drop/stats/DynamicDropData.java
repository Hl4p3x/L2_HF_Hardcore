package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.actor.templates.drop.custom.CustomDropEntry;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.EquipmentDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.AllScrollsDropData;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropData {

    private EquipmentDropStats equipment;
    private EquipmentDropStats parts;
    private EquipmentDropStats recipes;
    private ResourceDropStats resources;
    private AllScrollsDropData scrolls;

    @JsonProperty("custom-drop")
    private List<CustomDropEntry> customDropEntries;

    public DynamicDropData() {
    }

    public DynamicDropData(EquipmentDropStats equipment, EquipmentDropStats parts, EquipmentDropStats recipes, ResourceDropStats resources, AllScrollsDropData scrolls, List<CustomDropEntry> customDropEntries) {
        this.equipment = equipment;
        this.parts = parts;
        this.recipes = recipes;
        this.resources = resources;
        this.scrolls = scrolls;
        this.customDropEntries = customDropEntries;
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

    public List<CustomDropEntry> getCustomDropEntries() {
        return customDropEntries;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DynamicDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(equipment))
                .add(Objects.toString(parts))
                .add(Objects.toString(recipes))
                .add(Objects.toString(resources))
                .add(Objects.toString(scrolls))
                .add(Objects.toString(customDropEntries))
                .toString();
    }

}
