package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.l2jserver.gameserver.model.actor.templates.drop.Range;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.deserializer.ScrollGradeKeyDeserializer;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ScrollChanceCountStats {

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<ScrollGrade, Double> chance;


    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<ScrollGrade, Range> count;

    public ScrollChanceCountStats() {
    }

    public ScrollChanceCountStats(Map<ScrollGrade, Double> chance, Map<ScrollGrade, Range> count) {
        this.chance = chance;
        this.count = count;
    }

    public Map<ScrollGrade, Double> getChance() {
        return chance;
    }

    public Map<ScrollGrade, Range> getCount() {
        return count;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScrollChanceCountStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(chance))
                .add(Objects.toString(count))
                .toString();
    }

}
