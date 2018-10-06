package com.l2jserver.gameserver.model.actor.templates.drop.stats.basic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.l2jserver.gameserver.model.actor.templates.drop.Range;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.deserializer.GradeInfoKeyDeserializer;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ChanceCountStats {

    @JsonDeserialize(keyUsing = GradeInfoKeyDeserializer.class)
    private Map<GradeInfo, Range> count;

    @JsonDeserialize(keyUsing = GradeInfoKeyDeserializer.class)
    private Map<GradeInfo, Double> chance;

    public ChanceCountStats() {
    }

    public ChanceCountStats(Map<GradeInfo, Range> count, Map<GradeInfo, Double> chance) {
        this.count = count;
        this.chance = chance;
    }

    public Map<GradeInfo, Range> getCounts() {
        return count;
    }

    public Map<GradeInfo, Double> getChances() {
        return chance;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChanceCountStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(count))
                .add(Objects.toString(chance))
                .toString();
    }

}
