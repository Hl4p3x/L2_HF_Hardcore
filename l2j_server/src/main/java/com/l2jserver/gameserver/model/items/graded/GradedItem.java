package com.l2jserver.gameserver.model.items.graded;

public class GradedItem {

    private Integer itemId;
    private String itemName;
    private int itemPrice;
    private GradeInfo gradeInfo;

    public GradedItem(Integer itemId, String itemName, int itemPrice, GradeInfo gradeInfo) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.gradeInfo = gradeInfo;
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
