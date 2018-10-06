package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.deserializer.ScrollGradeKeyDeserializer;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ScrollCountStats {

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<ScrollGrade, Double> countByScrollGrade;

    public ScrollCountStats() {
    }

    public ScrollCountStats(Map<ScrollGrade, Double> countByScrollGrade) {
        this.countByScrollGrade = countByScrollGrade;
    }

    public Map<ScrollGrade, Double> getCountByScrollGrade() {
        return countByScrollGrade;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScrollCountStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(countByScrollGrade))
                .toString();
    }

}
