package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ScrollChanceStats {

    private Map<ScrollGrade, Double> chanceByScrollGrade;

    public ScrollChanceStats() {
    }

    public ScrollChanceStats(Map<ScrollGrade, Double> chanceByScrollGrade) {
        this.chanceByScrollGrade = chanceByScrollGrade;
    }

    public Map<ScrollGrade, Double> getChanceByScrollGrade() {
        return chanceByScrollGrade;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScrollChanceStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(chanceByScrollGrade))
                .toString();
    }

}
