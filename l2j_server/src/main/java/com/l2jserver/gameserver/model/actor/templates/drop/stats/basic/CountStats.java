package com.l2jserver.gameserver.model.actor.templates.drop.stats.basic;

import com.l2jserver.gameserver.model.actor.templates.drop.Range;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class CountStats {

    private Map<GradeInfo, Range> countByGradeInfo;

    public CountStats() {
    }

    public CountStats(Map<GradeInfo, Range> countByGradeInfo) {
        this.countByGradeInfo = countByGradeInfo;
    }

    public Map<GradeInfo, Range> getCountByGradeInfo() {
        return countByGradeInfo;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CountStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(countByGradeInfo))
                .toString();
    }

}
