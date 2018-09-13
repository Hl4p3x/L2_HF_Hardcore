package com.l2jserver.gameserver.datatables.categorized;

import java.util.HashSet;
import java.util.Set;

public enum EquipmentCategory {

    NONE, //
    LOW_NG, MID_NG, TOP_NG, //
    LOW_D, MID_D, TOP_D, //
    LOW_C, MID_C, TOP_C, //
    LOW_B, TOP_B, //
    LOW_A, MID_A, TOP_A, //
    S, //
    LOW_S80, TOP_S80, //
    LOW_S84, MID_S84, TOP_S84;

    public static Set<EquipmentCategory> parse(String raw) {
        Set<EquipmentCategory> result = new HashSet<>();
        String[] categories = raw.split(";");
        for (String category : categories) {
            result.add(valueOf(category));
        }
        return result;
    }

}
