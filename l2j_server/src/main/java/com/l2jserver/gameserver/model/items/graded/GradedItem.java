package com.l2jserver.gameserver.model.items.graded;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.l2jserver.gameserver.model.items.interfaces.HasItemId;

public class GradedItem implements HasItemId {

    private int itemId;
    private String itemName;
    private int itemPrice;
    @JsonIgnore
    private int itemSlot;
    private GradeInfo gradeInfo;

    public GradedItem() {
    }

    public GradedItem(int itemId, String itemName, int itemPrice, int itemSlot, GradeInfo gradeInfo) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemSlot = itemSlot;
        this.gradeInfo = gradeInfo;
    }


    public int getItemSlot() {
        return itemSlot;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public GradeInfo getGradeInfo() {
        return gradeInfo;
    }

    @Override
    public String toString() {
        return String.format("GradedItem[%s, %s, %s, %s]", itemId, itemName, itemPrice, gradeInfo);
    }

}
