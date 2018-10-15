package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.deserializer.ScrollGradeKeyDeserializer;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class AllScrollsDropData {

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<ScrollGrade, ScrollDropStats> weapon;

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<ScrollGrade, ScrollDropStats> armor;

    private MiscScrollStats misc;

    public AllScrollsDropData() {
    }

    public AllScrollsDropData(Map<ScrollGrade, ScrollDropStats> weapon, Map<ScrollGrade, ScrollDropStats> armor, MiscScrollStats misc) {
        this.weapon = weapon;
        this.armor = armor;
        this.misc = misc;
    }

    public Map<ScrollGrade, ScrollDropStats> getWeapon() {
        return weapon;
    }

    public Optional<ScrollDropStats> getWeaponByGrade(ScrollGrade scrollGrade) {
        return Optional.ofNullable(weapon.get(scrollGrade));
    }

    public Map<ScrollGrade, ScrollDropStats> getArmor() {
        return armor;
    }

    public Optional<ScrollDropStats> getArmorByGrade(ScrollGrade scrollGrade) {
        return Optional.ofNullable(armor.get(scrollGrade));
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
