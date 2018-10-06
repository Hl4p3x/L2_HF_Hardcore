package com.l2jserver.gameserver.model.items.craft;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ResourceGrade {

    LOW, MID, HIGH, HIGHEST, UNSET;

    @JsonCreator
    public static ResourceGrade fromString(String text) {
        return valueOf(text.toUpperCase());
    }

}
