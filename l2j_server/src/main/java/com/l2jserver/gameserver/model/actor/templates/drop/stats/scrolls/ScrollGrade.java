package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ScrollGrade {

    UNSET, D, C, B, A, S;

    @JsonCreator
    public static ScrollGrade fromString(String text) {
        return valueOf(text.toUpperCase());
    }

    @JsonValue
    public String asString() {
        return name().toLowerCase();
    }

}
