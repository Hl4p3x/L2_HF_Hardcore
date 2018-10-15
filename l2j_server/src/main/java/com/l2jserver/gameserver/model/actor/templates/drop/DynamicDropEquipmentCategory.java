package com.l2jserver.gameserver.model.actor.templates.drop;

import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropEquipmentCategory {

    private DynamicDropCategory weapons;
    private DynamicDropCategory armor;
    private DynamicDropCategory jewels;

    public DynamicDropEquipmentCategory() {
    }

    public DynamicDropEquipmentCategory(DynamicDropCategory weapons, DynamicDropCategory armor, DynamicDropCategory jewels) {
        this.weapons = weapons;
        this.armor = armor;
        this.jewels = jewels;
    }

    public DynamicDropCategory getWeapons() {
        return weapons;
    }

    public DynamicDropCategory getArmor() {
        return armor;
    }

    public DynamicDropCategory getJewels() {
        return jewels;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DynamicDropEquipmentCategory.class.getSimpleName() + "[", "]")
                .add(Objects.toString(weapons))
                .add(Objects.toString(armor))
                .add(Objects.toString(jewels))
                .toString();
    }

}
