package com.l2jserver.gameserver.model.actor.templates.drop.chances;

public enum ItemGradeChanceMods {

    NG(12), D(9), C(7), B(5), A(3), S(1), S80(0.8), S84(0.5);

    private final double mod;

    ItemGradeChanceMods(double mod) {
        this.mod = mod;
    }

    public double getMod() {
        return mod;
    }

}
