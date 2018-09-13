package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.datatables.categorized.EquipmentCategory;

public enum EquipmentGradeRanges {

    LOW_NG(1, 9, EquipmentCategory.LOW_NG), //
    MID_NG(10, 16, EquipmentCategory.MID_NG), //
    TOP_NG(17, 20, EquipmentCategory.TOP_NG), //
    //
    LOW_D(21, 30, EquipmentCategory.LOW_D), //
    MID_D(31, 36, EquipmentCategory.MID_D), //
    TOP_D(37, 40, EquipmentCategory.TOP_D), //
    //
    LOW_C(41, 46, EquipmentCategory.LOW_C), //
    MID_C(47, 49, EquipmentCategory.MID_C), //
    TOP_C(50, 52, EquipmentCategory.TOP_C), //
    //
    LOW_B(53, 58, EquipmentCategory.LOW_B), //
    TOP_B(59, 61, EquipmentCategory.TOP_B), //
    //
    LOW_A(62, 68, EquipmentCategory.LOW_A), //
    MID_A(62, 68, EquipmentCategory.MID_A), //
    TOP_A(69, 75, EquipmentCategory.TOP_A), //
    //
    S(76, 79, EquipmentCategory.S), //
    S_DYNO(80, 81, EquipmentCategory.LOW_S80), //
    S_MORA(82, 83, EquipmentCategory.TOP_S80), //
    S_VESP(84, 84, EquipmentCategory.LOW_S84);

    private int lowLevel;
    private int highLevel;
    private EquipmentCategory equipmentCategory;

    EquipmentGradeRanges(int lowLevel, int highLevel, EquipmentCategory equipmentCategory) {
        this.lowLevel = lowLevel;
        this.highLevel = highLevel;
        this.equipmentCategory = equipmentCategory;
    }

    public int getLowLevel() {
        return lowLevel;
    }

    public int getHighLevel() {
        return highLevel;
    }

    public EquipmentCategory getEquipmentCategory() {
        return equipmentCategory;
    }

}
