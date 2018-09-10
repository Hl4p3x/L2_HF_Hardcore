package com.l2jserver.gameserver.model.actor.templates.drop.chances;

public enum BasePartsDropChances {

    WEAPON(30), ARMOR(40), JEWELS(50);

    private final double chance;

    BasePartsDropChances(double chance) {
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

}
