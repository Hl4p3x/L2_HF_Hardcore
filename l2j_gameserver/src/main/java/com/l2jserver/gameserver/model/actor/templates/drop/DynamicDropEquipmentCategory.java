package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropEquipmentCategory {

    private GradeInfo gradeInfo;
    private DynamicDropCategory weapons;
    private DynamicDropCategory armor;
    private DynamicDropCategory jewels;

    public DynamicDropEquipmentCategory() {
    }

    public DynamicDropEquipmentCategory(GradeInfo gradeInfo, DynamicDropCategory weapons, DynamicDropCategory armor, DynamicDropCategory jewels) {
        this.gradeInfo = gradeInfo;
        this.weapons = weapons;
        this.armor = armor;
        this.jewels = jewels;
    }

    public static DynamicDropEquipmentCategory empty() {
        return new DynamicDropEquipmentCategory(GradeInfo.unset(), DynamicDropCategory.empty(), DynamicDropCategory.empty(), DynamicDropCategory.empty());
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

    public GradeInfo getGradeInfo() {
        return gradeInfo;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DynamicDropEquipmentCategory.class.getSimpleName() + "[", "]")
                .add(Objects.toString(gradeInfo))
                .add(Objects.toString(weapons))
                .add(Objects.toString(armor))
                .add(Objects.toString(jewels))
                .toString();
    }

    public DynamicDropEquipmentCategory applyChanceMod(double chanceMod) {
        return new DynamicDropEquipmentCategory(gradeInfo, weapons.applyChanceMod(chanceMod), armor.applyChanceMod(chanceMod), jewels.applyChanceMod(chanceMod));
    }
}
