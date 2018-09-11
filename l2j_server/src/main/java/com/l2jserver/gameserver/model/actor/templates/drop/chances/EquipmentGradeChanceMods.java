package com.l2jserver.gameserver.model.actor.templates.drop.chances;

public enum EquipmentGradeChanceMods {

    NG(12), D(9), C(7), B(5), A(3), S(1), S_DYNO(0.8), S_MORAI(0.5), S_VESP(0.2);

    private final double mod;

    EquipmentGradeChanceMods(double mod) {
        this.mod = mod;
    }

    public double getMod() {
        return mod;
    }

}
