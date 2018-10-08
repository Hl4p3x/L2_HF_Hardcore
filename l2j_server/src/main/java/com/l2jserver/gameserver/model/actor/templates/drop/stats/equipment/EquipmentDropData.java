package com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.deserializer.GradeInfoKeyDeserializer;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class EquipmentDropData {

    @JsonDeserialize(keyUsing = GradeInfoKeyDeserializer.class)
    private Map<GradeInfo, ChanceCountPair> weapon;

    @JsonDeserialize(keyUsing = GradeInfoKeyDeserializer.class)
    private Map<GradeInfo, ChanceCountPair> armor;

    @JsonDeserialize(keyUsing = GradeInfoKeyDeserializer.class)
    private Map<GradeInfo, ChanceCountPair> jewels;

    public EquipmentDropData() {
    }

    public EquipmentDropData(Map<GradeInfo, ChanceCountPair> weapon, Map<GradeInfo, ChanceCountPair> armor, Map<GradeInfo, ChanceCountPair> jewels) {
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
        return new StringJoiner(", ", EquipmentDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(weapon))
                .add(Objects.toString(armor))
                .add(Objects.toString(jewels))
                .toString();
    }

}
