package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

public enum ScrollGrade {

    UNSET, D, C, B, A, S;

    public static ScrollGrade fromString(String text) {
        return valueOf(text.toUpperCase());
    }

}
