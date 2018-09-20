package com.l2jserver.gameserver.model.items.graded;

import com.l2jserver.gameserver.model.items.type.CrystalType;

public enum Grade {

    UNSET, NG, D, C, B, A, S, S80, S84;

    public static Grade fromString(String text) {
        if (text == null || text.isEmpty()) {
            return UNSET;
        }

        return valueOf(text.substring(0, 1));
    }

    public static Grade fromCrystalType(CrystalType crystalType) {
        if (crystalType.equals(CrystalType.NONE)) {
            return NG;
        }

        try {
            return valueOf(crystalType.name());
        } catch (RuntimeException e) {
            return UNSET;
        }
    }

}
