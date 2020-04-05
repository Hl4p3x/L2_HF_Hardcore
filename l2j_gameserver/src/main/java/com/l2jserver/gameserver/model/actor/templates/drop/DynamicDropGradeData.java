package com.l2jserver.gameserver.model.actor.templates.drop;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class DynamicDropGradeData {

    private DynamicDropEquipmentCategory equipment;
    private DynamicDropEquipmentCategory parts;
    private DynamicDropEquipmentCategory recipes;

    private List<DynamicDropResourcesCategory> resources;
    private DynamicDropScrollCategory weaponScrolls;
    private DynamicDropScrollCategory armorScrolls;
    private DynamicDropScrollCategory miscScrolls;

    public DynamicDropGradeData() {
    }

    public DynamicDropGradeData(DynamicDropEquipmentCategory equipment, DynamicDropEquipmentCategory parts, DynamicDropEquipmentCategory recipes, List<DynamicDropResourcesCategory> resources, DynamicDropScrollCategory weaponScrolls, DynamicDropScrollCategory armorScrolls, DynamicDropScrollCategory miscScrolls) {
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

    public List<DynamicDropResourcesCategory> getResources() {
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

    public DynamicDropGradeData applyChanceMod(double chanceMod) {
        return new DynamicDropGradeData(equipment.applyChanceMod(chanceMod),
                parts.applyChanceMod(chanceMod),
                recipes.applyChanceMod(chanceMod),
                resources.stream().map(resource -> resource.applyChanceMod(chanceMod)).collect(Collectors.toList()),
                weaponScrolls.applyChanceMod(chanceMod),
                armorScrolls.applyChanceMod(chanceMod),
                miscScrolls.applyChanceMod(chanceMod)
        );
    }

}
