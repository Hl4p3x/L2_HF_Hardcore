package com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.GradeInfoHelper;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.deserializer.GradeInfoKeyDeserializer;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class EquipmentDropStats {

    @JsonDeserialize(keyUsing = GradeInfoKeyDeserializer.class)
    private Map<GradeInfo, DropStats> weapon;

    @JsonDeserialize(keyUsing = GradeInfoKeyDeserializer.class)
    private Map<GradeInfo, DropStats> armor;

    @JsonDeserialize(keyUsing = GradeInfoKeyDeserializer.class)
    private Map<GradeInfo, DropStats> jewels;

    public EquipmentDropStats() {
    }

    public EquipmentDropStats(Map<GradeInfo, DropStats> weapon, Map<GradeInfo, DropStats> armor, Map<GradeInfo, DropStats> jewels) {
        this.weapon = weapon;
        this.armor = armor;
        this.jewels = jewels;
    }

    public Optional<DropStats> getWeapon(GradeInfo gradeInfo) {
        return GradeInfoHelper.findByGradeInfo(gradeInfo, weapon);
    }

    public Optional<DropStats> getArmor(GradeInfo gradeInfo) {
        return GradeInfoHelper.findByGradeInfo(gradeInfo, armor);
    }

    public Optional<DropStats> getJewels(GradeInfo gradeInfo) {
        return GradeInfoHelper.findByGradeInfo(gradeInfo, jewels);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EquipmentDropStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(weapon))
                .add(Objects.toString(armor))
                .add(Objects.toString(jewels))
                .toString();
    }

}
