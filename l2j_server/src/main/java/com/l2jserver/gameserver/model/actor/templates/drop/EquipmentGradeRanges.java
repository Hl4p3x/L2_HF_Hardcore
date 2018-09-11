package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.datatables.categorized.EquipmentCategories;

public enum EquipmentGradeRanges {

    LOW_NG(1, 9, EquipmentCategories.LOW_NG), //
    MID_NG(10, 16, EquipmentCategories.MID_NG), //
    TOP_NG(17, 20, EquipmentCategories.TOP_NG), //
    //
    LOW_D(21, 30, EquipmentCategories.LOW_D), //
    MID_D(31, 36, EquipmentCategories.MID_D), //
    TOP_D(37, 40, EquipmentCategories.TOP_D), //
    //
    LOW_C(41, 46, EquipmentCategories.LOW_C), //
    MID_C(47, 49, EquipmentCategories.MID_C), //
    TOP_C(50, 52, EquipmentCategories.TOP_C), //
    //
    LOW_B(53, 58, EquipmentCategories.LOW_B), //
    TOP_B(59, 61, EquipmentCategories.TOP_B), //
    //
    LOW_A(62, 68, EquipmentCategories.LOW_A), //
    MID_A(62, 68, EquipmentCategories.MID_A), //
    TOP_A(69, 75, EquipmentCategories.TOP_A), //
    //
    S(76, 79, EquipmentCategories.S), //
    S_DYNO(80, 81, EquipmentCategories.LOW_S80), //
    S_MORA(82, 83, EquipmentCategories.TOP_S80), //
    S_VESP(84, 84, EquipmentCategories.LOW_S84);

    private int lowLevel;
    private int highLevel;
    private EquipmentCategories equipmentCategories;

    EquipmentGradeRanges(int lowLevel, int highLevel, EquipmentCategories equipmentCategories) {
        this.lowLevel = lowLevel;
        this.highLevel = highLevel;
        this.equipmentCategories = equipmentCategories;
    }

    public int getLowLevel() {
        return lowLevel;
    }

    public int getHighLevel() {
        return highLevel;
    }

    public EquipmentCategories getEquipmentCategories() {
        return equipmentCategories;
    }

}
