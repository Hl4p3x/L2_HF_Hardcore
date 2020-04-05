package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;
import java.util.Optional;
import java.util.stream.Stream;

public enum ScrollGradeRange {

    D(20, 39, ScrollGrade.D),
    C(40, 51, ScrollGrade.C),
    B(52, 60, ScrollGrade.B),
    A(61, 75, ScrollGrade.A),
    S(76, 86, ScrollGrade.S);

    private int lowLevel;
    private int highLevel;
    private ScrollGrade scrollGrade;

    ScrollGradeRange(int lowLevel, int highLevel, ScrollGrade scrollGrade) {
        this.lowLevel = lowLevel;
        this.highLevel = highLevel;
        this.scrollGrade = scrollGrade;
    }

    public int getLowLevel() {
        return lowLevel;
    }

    public int getHighLevel() {
        return highLevel;
    }

    public ScrollGrade getScrollGrade() {
        return scrollGrade;
    }

    public static Optional<ScrollGrade> byLevel(int level) {
        return Stream.of(values()).filter(item -> item.getLowLevel() <= level && level <= item.getHighLevel()).map(ScrollGradeRange::getScrollGrade).findFirst();
    }

    public static ScrollGradeRange lowestGrade() {
        return D;
    }

}
