package com.l2jserver.gameserver.model.actor.templates.drop;

public enum EquipmentGradeRanges {

    LOW_NG(1, 9), NG(10, 16), TOP_NG(17, 20), //
    LOW_D(21, 30), D(31, 36), TOP_D(37, 40), //
    LOW_C(41, 46), C(47, 49), TOP_C(50, 52), //
    LOW_B(53, 58), TOP_B(59, 61), //
    LOW_A(62, 68), TOP_A(69, 75), //
    S(76, 79), S_DYNO(80, 81), //
    S_MORA(82, 83), S_VESP(84, 84);

    private int lowLevel;
    private int highLevel;

    EquipmentGradeRanges(int lowLevel, int highLevel) {
        this.lowLevel = lowLevel;
        this.highLevel = highLevel;
    }

    public int getLowLevel() {
        return lowLevel;
    }

    public int getHighLevel() {
        return highLevel;
    }

}
