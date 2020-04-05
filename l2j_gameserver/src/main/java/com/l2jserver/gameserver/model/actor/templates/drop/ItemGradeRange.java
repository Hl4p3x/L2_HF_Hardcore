package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.model.items.graded.Grade;
import com.l2jserver.gameserver.model.items.graded.GradeCategory;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import java.util.Optional;
import java.util.stream.Stream;

public enum ItemGradeRange {

    LOW_NG(1, 9, new GradeInfo(Grade.NG, GradeCategory.LOW)), //
    MID_NG(10, 16, new GradeInfo(Grade.NG, GradeCategory.MID)), //
    TOP_NG(17, 20, new GradeInfo(Grade.NG, GradeCategory.TOP)), //
    //
    LOW_D(21, 30, new GradeInfo(Grade.D, GradeCategory.LOW)), //
    MID_D(31, 36, new GradeInfo(Grade.D, GradeCategory.MID)), //
    TOP_D(37, 40, new GradeInfo(Grade.D, GradeCategory.TOP)), //
    //
    LOW_C(41, 46, new GradeInfo(Grade.C, GradeCategory.LOW)), //
    MID_C(47, 49, new GradeInfo(Grade.C, GradeCategory.MID)), //
    TOP_C(50, 52, new GradeInfo(Grade.C, GradeCategory.TOP)), //
    //
    LOW_B(53, 58, new GradeInfo(Grade.B, GradeCategory.LOW)), //
    TOP_B(59, 61, new GradeInfo(Grade.B, GradeCategory.TOP)), //
    //
    LOW_A(62, 68, new GradeInfo(Grade.A, GradeCategory.LOW)), //
    MID_A(69, 71, new GradeInfo(Grade.A, GradeCategory.MID)), //
    TOP_A(72, 75, new GradeInfo(Grade.A, GradeCategory.TOP)), //
    //
    S(76, 79, new GradeInfo(Grade.S, GradeCategory.ALL)), //
    S_DYNO(80, 82, new GradeInfo(Grade.S80, GradeCategory.LOW)), //
    S_MORA(83, 84, new GradeInfo(Grade.S80, GradeCategory.TOP)), //
    S_VESP(85, 85, new GradeInfo(Grade.S84, GradeCategory.LOW));

    private int lowLevel;
    private int highLevel;
    private GradeInfo gradeInfo;

    ItemGradeRange(int lowLevel, int highLevel, GradeInfo gradeInfo) {
        this.lowLevel = lowLevel;
        this.highLevel = highLevel;
        this.gradeInfo = gradeInfo;
    }

    public int getLowLevel() {
        return lowLevel;
    }

    public int getHighLevel() {
        return highLevel;
    }

    public GradeInfo getGradeInfo() {
        return gradeInfo;
    }

    public static Optional<ItemGradeRange> byLevel(int level) {
        return Stream.of(values()).filter(item -> item.getLowLevel() <= level && level <= item.getHighLevel()).findFirst();
    }

}
