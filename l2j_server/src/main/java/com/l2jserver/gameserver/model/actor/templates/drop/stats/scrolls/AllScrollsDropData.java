package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.deserializer.ScrollGradeKeyDeserializer;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class AllScrollsDropData {

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<ScrollGrade, ScrollDropData> weapon;

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<ScrollGrade, ScrollDropData> armor;

    private MiscScrollStats misc;

    public AllScrollsDropData() {
    }

    public AllScrollsDropData(Map<ScrollGrade, ScrollDropData> weapon, Map<ScrollGrade, ScrollDropData> armor, MiscScrollStats misc) {
        this.weapon = weapon;
        this.armor = armor;
        this.misc = misc;
    }

    public Map<ScrollGrade, ScrollDropData> getWeapon() {
        return weapon;
    }

    public Map<ScrollGrade, ScrollDropData> getArmor() {
        return armor;
    }

    public MiscScrollStats getMisc() {
        return misc;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AllScrollsDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(weapon))
                .add(Objects.toString(armor))
                .add(Objects.toString(misc))
                .toString();
    }

}
