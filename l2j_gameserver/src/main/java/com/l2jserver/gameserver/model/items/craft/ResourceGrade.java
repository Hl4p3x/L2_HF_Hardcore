package com.l2jserver.gameserver.model.items.craft;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.l2jserver.common.util.StringUtil;

public enum ResourceGrade {

    LOW, MID, HIGH, HIGHEST, BASE_CATEGORY_MATERIALS("Base Craft" ), UNSET, UNDROPPABLE;

    private final String customName;

    ResourceGrade() {
        this.customName = StringUtil.capitalize(name());
    }

    ResourceGrade(String customName) {
        this.customName = customName;
    }

    @JsonCreator
    public static ResourceGrade fromString(String text) {
        return valueOf(text.toUpperCase());
    }

    @JsonValue
    public String asString() {
        return name().toLowerCase();
    }

    public String toString() {
        return customName;
    }

}
