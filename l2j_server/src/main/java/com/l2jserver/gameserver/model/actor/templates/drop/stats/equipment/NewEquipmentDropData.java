package com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.deserializer.ScrollGradeKeyDeserializer;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class NewEquipmentDropData {

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<GradeInfo, ChanceCountPair> weapon;

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<GradeInfo, ChanceCountPair> armor;

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<GradeInfo, ChanceCountPair> jewels;

    public NewEquipmentDropData() {
    }

    public NewEquipmentDropData(Map<GradeInfo, ChanceCountPair> weapon, Map<GradeInfo, ChanceCountPair> armor, Map<GradeInfo, ChanceCountPair> jewels) {
        this.weapon = weapon;
        this.armor = armor;
        this.jewels = jewels;
    }

    public Map<GradeInfo, ChanceCountPair> getWeapon() {
        return weapon;
    }

    public Map<GradeInfo, ChanceCountPair> getArmor() {
        return armor;
    }

    public Map<GradeInfo, ChanceCountPair> getJewels() {
        return jewels;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NewEquipmentDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(weapon))
                .add(Objects.toString(armor))
                .add(Objects.toString(jewels))
                .toString();
    }

}
