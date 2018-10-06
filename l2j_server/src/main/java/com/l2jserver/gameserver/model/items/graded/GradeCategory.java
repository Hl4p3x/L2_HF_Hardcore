package com.l2jserver.gameserver.model.items.graded;

public enum GradeCategory {

    LOW, MID, TOP, ALL, UNSET;

    public static GradeCategory fromString(String text) {
        return valueOf(text.toUpperCase());
    }

}
