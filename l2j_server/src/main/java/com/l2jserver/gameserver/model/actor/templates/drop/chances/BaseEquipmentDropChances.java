package com.l2jserver.gameserver.model.actor.templates.drop.chances;

public enum BaseEquipmentDropChances {

    HELMET(3), CHEST(2.5), LEGS(2.5), //
    FULL_ARMOR(2), GLOVES(4), BOOTS(4), //
    SHIELD(5), WEAPONS(1), //
    NECKLACE(4), EARRING(5), RING(6);

    private final double chance;

    BaseEquipmentDropChances(double chance) {
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

}
