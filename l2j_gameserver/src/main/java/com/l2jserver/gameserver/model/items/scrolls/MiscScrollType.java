package com.l2jserver.gameserver.model.items.scrolls;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MiscScrollType {

    UNKNOWN, SOE, RESURRECT;

    public static MiscScrollType fromName(String name) {
        if (name.contains("Resurrection")) {
            return RESURRECT;
        } else if (name.contains("Scroll of Escape")) {
            return SOE;
        } else {
            return UNKNOWN;
        }
    }

    @JsonCreator
    public static MiscScrollType fromString(String text) {
        return valueOf(text.toUpperCase());
    }

    @JsonValue
    public String asString() {
        return name().toLowerCase();
    }

}
