package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import java.util.Objects;
import java.util.StringJoiner;

public class AllScrollsStats {

    private ScrollsStats weapon;
    private ScrollsStats armor;

    public AllScrollsStats() {
    }

    public AllScrollsStats(ScrollsStats weapon, ScrollsStats armor) {
        this.weapon = weapon;
        this.armor = armor;
    }

    public ScrollsStats getWeapon() {
        return weapon;
    }

    public ScrollsStats getArmor() {
        return armor;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AllScrollsStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(weapon))
                .add(Objects.toString(armor))
                .toString();
    }

}
