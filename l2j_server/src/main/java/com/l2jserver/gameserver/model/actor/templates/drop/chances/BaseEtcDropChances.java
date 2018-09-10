package com.l2jserver.gameserver.model.actor.templates.drop.chances;

public enum BaseEtcDropChances {

    WEAPON_ENCHANT(10), ARMOR_ENCHANT(30), //
    ATTRIBUTE(20), SCROLLS(50);

    private final double chance;

    BaseEtcDropChances(double chance) {
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

}
