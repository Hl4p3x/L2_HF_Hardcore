package com.l2jserver.gameserver.transfer.bulk;

import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.model.items.type.ItemType;
import java.util.stream.Stream;

public enum BulkItemType {

    RESOURCES(EtcItemType.MATERIAL), PARTS(EtcItemType.MATERIAL), RECIPES(EtcItemType.RECIPE), NONE(EtcItemType.NONE);

    private ItemType itemType;

    BulkItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public static BulkItemType of(String itemType) {
        return Stream.of(values()).filter(item -> item.name().toLowerCase().equals(itemType.toLowerCase())).findFirst().orElse(NONE);
    }

    public ItemType getItemType() {
        return itemType;
    }

}
