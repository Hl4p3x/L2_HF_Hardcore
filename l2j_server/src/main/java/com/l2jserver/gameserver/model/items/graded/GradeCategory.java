package com.l2jserver.gameserver.model.items.graded;

import com.l2jserver.util.StringUtil;

public enum GradeCategory {

    LOW, MID, TOP, ALL, UNSET;

    public static GradeCategory fromString(String text) {
        return valueOf(text.toUpperCase());
    }

    @Override
    public String toString() {
        return StringUtil.capitalize(this.name());
    }

}
