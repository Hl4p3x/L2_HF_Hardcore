package com.l2jserver.gameserver.model.items.craft;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.l2jserver.util.StringUtil;

public enum ResourceGrade {

    LOW, MID, HIGH, HIGHEST, UNSET;

    @JsonCreator
    public static ResourceGrade fromString(String text) {
        return valueOf(text.toUpperCase());
    }

    @JsonValue
    public String asString() {
        return name().toLowerCase();
    }

    public String toString() {
        return StringUtil.capitalize(name());
    }

}
