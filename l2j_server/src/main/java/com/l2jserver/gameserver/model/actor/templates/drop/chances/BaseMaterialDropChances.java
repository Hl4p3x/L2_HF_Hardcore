package com.l2jserver.gameserver.model.actor.templates.drop.chances;

public enum BaseMaterialDropChances {

    LOW(90), MID(60), TOP(30), COMPLETE(15);

    private final double chance;

    BaseMaterialDropChances(double chance) {
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

}
