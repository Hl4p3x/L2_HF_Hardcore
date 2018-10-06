package com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountStats;

import java.util.Objects;
import java.util.StringJoiner;

public class WeaponArmorJewelStats {

    private ChanceCountStats weapon;
    private ChanceCountStats armor;
    private ChanceCountStats jewels;

    public WeaponArmorJewelStats() {
    }

    public WeaponArmorJewelStats(ChanceCountStats weapon, ChanceCountStats armor, ChanceCountStats jewels) {
        this.weapon = weapon;
        this.armor = armor;
        this.jewels = jewels;
    }

    public ChanceCountStats getWeapon() {
        return weapon;
    }

    public ChanceCountStats getArmor() {
        return armor;
    }

    public ChanceCountStats getJewels() {
        return jewels;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WeaponArmorJewelStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(weapon))
                .add(Objects.toString(armor))
                .add(Objects.toString(jewels))
                .toString();
    }

}
