package com.l2jserver.gameserver.model.actor.templates.drop.stats.basic;

import com.l2jserver.gameserver.model.items.graded.GradeInfo;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ChanceStats {

    private Map<GradeInfo, Double> chanceByGradeInfo;

    public ChanceStats() {
    }

    public ChanceStats(Map<GradeInfo, Double> chanceByGradeInfo) {
        this.chanceByGradeInfo = chanceByGradeInfo;
    }

    public Map<GradeInfo, Double> getChanceByGradeInfo() {
        return chanceByGradeInfo;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChanceStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(chanceByGradeInfo))
                .toString();
    }

}
