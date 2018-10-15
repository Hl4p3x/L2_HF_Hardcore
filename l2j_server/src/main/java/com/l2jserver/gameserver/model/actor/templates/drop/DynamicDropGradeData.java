package com.l2jserver.gameserver.model.actor.templates.drop;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropGradeData {

    private DynamicDropEquipmentCategory equipment;
    private DynamicDropEquipmentCategory parts;
    private DynamicDropEquipmentCategory recipes;

    private List<DynamicDropCategory> resources;
    private DynamicDropScrollCategory weaponScrolls;
    private DynamicDropScrollCategory armorScrolls;
    private DynamicDropScrollCategory miscScrolls;

    public DynamicDropGradeData() {
    }

    public DynamicDropGradeData(DynamicDropEquipmentCategory equipment, DynamicDropEquipmentCategory parts, DynamicDropEquipmentCategory recipes, List<DynamicDropCategory> resources, DynamicDropScrollCategory weaponScrolls, DynamicDropScrollCategory armorScrolls, DynamicDropScrollCategory miscScrolls) {
        this.equipment = equipment;
        this.parts = parts;
        this.recipes = recipes;
        this.resources = resources;
        this.weaponScrolls = weaponScrolls;
        this.armorScrolls = armorScrolls;
        this.miscScrolls = miscScrolls;
    }

    public DynamicDropEquipmentCategory getEquipment() {
        return equipment;
    }

    public DynamicDropEquipmentCategory getParts() {
        return parts;
    }

    public DynamicDropEquipmentCategory getRecipes() {
        return recipes;
    }

    public List<DynamicDropCategory> getResources() {
        return resources;
    }

    public DynamicDropScrollCategory getWeaponScrolls() {
        return weaponScrolls;
    }

    public DynamicDropScrollCategory getArmorScrolls() {
        return armorScrolls;
    }

    public DynamicDropScrollCategory getMiscScrolls() {
        return miscScrolls;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DynamicDropGradeData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(equipment))
                .add(Objects.toString(parts))
                .add(Objects.toString(recipes))
                .add(Objects.toString(resources))
                .add(Objects.toString(weaponScrolls))
                .add(Objects.toString(armorScrolls))
                .add(Objects.toString(miscScrolls))
                .toString();
    }
}
