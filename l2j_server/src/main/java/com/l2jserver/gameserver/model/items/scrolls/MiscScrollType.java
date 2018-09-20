package com.l2jserver.gameserver.model.items.scrolls;

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

}
