package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import java.util.Objects;
import java.util.StringJoiner;

public class NewAllScrollsDropData {

    private ScrollDropData weapon;
    private ScrollDropData armor;
    private MiscScrollStats misc;

    public NewAllScrollsDropData() {
    }

    public NewAllScrollsDropData(ScrollDropData weapon, ScrollDropData armor, MiscScrollStats misc) {
        this.weapon = weapon;
        this.armor = armor;
        this.misc = misc;
    }

    public ScrollDropData getWeapon() {
        return weapon;
    }

    public ScrollDropData getArmor() {
        return armor;
    }

    public MiscScrollStats getMisc() {
        return misc;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NewAllScrollsDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(weapon))
                .add(Objects.toString(armor))
                .add(Objects.toString(misc))
                .toString();
    }

}
