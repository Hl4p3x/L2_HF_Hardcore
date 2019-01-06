package com.l2jserver.gameserver.model.itemcontainer;

import java.util.stream.Stream;

public enum WarehouseType {

    NONE, PRIVATE, CLAN;

    public static WarehouseType of(String text) {
        return Stream.of(values()).filter(item -> item.name().toLowerCase().equals(text.toLowerCase())).findFirst().orElse(NONE);
    }

}
