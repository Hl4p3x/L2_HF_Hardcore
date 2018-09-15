package com.l2jserver.gameserver.model.items.parts;

import com.l2jserver.gameserver.datatables.ItemTable;

public class ItemPart {

    private int itemId;
    private String itemName;
    private int partId;
    private String partName;

    public ItemPart() {
    }

    public ItemPart(int itemId, int partId) {
        this(
                itemId,
                ItemTable.getInstance().getTemplate(itemId).getName(),
                partId,
                ItemTable.getInstance().getTemplate(partId).getName()
        );
    }

    public ItemPart(int itemId, String itemName, int partId, String partName) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.partId = partId;
        this.partName = partName;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPartId() {
        return partId;
    }

    public String getPartName() {
        return partName;
    }

    @Override
    public String toString() {
        return String.format("ItemPart[%s, %s]", itemId, partId);
    }

}
