package com.l2jserver.gameserver.model.actor.templates.drop.chances;

public enum BaseRecipesDropChances {

    WEAPONS(5), ARMOR(10), JEWELS(15);

    private final double chance;

    BaseRecipesDropChances(double chance) {
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

}
