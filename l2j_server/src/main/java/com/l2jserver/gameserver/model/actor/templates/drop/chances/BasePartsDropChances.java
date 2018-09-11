package com.l2jserver.gameserver.model.actor.templates.drop.chances;

public enum BasePartsDropChances {

    WEAPON(60), ARMOR(70), JEWELS(75);

    private final double chance;

    BasePartsDropChances(double chance) {
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

}
