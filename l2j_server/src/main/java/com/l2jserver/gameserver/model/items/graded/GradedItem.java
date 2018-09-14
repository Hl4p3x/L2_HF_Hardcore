package com.l2jserver.gameserver.model.items.graded;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GradedItem {

    private Integer itemId;
    private String itemName;
    private int itemPrice;
    @JsonIgnore
    private int itemSlot;
    private GradeInfo gradeInfo;

    public GradedItem() {
    }

    public GradedItem(Integer itemId, String itemName, int itemPrice, int itemSlot, GradeInfo gradeInfo) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemSlot = itemSlot;
        this.gradeInfo = gradeInfo;
    }

    public int getItemSlot() {
        return itemSlot;
    }

    public Integer getItemId() {
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
