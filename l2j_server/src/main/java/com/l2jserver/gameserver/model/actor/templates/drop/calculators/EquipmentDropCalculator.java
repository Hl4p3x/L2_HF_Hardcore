package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropEquipmentCategory;
import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropGradeData;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.ArrayList;
import java.util.List;

public class EquipmentDropCalculator {

    public List<ItemHolder> calculate(DynamicDropGradeData dynamicDropGradeData) {
        List<ItemHolder> drop = new ArrayList<>();
        drop.addAll(calculateEquipmentCategoryDrop(dynamicDropGradeData.getEquipment()));
        drop.addAll(calculateEquipmentCategoryDrop(dynamicDropGradeData.getParts()));
        drop.addAll(calculateEquipmentCategoryDrop(dynamicDropGradeData.getRecipes()));
        return drop;
    }

    private List<ItemHolder> calculateEquipmentCategoryDrop(DynamicDropEquipmentCategory dynamicDropEquipmentCategory) {
        List<ItemHolder> drop = new ArrayList<>();
        drop.addAll(DynamicDropHelper.calculateCategoryDrop(dynamicDropEquipmentCategory.getWeapons()));
        drop.addAll(DynamicDropHelper.calculateCategoryDrop(dynamicDropEquipmentCategory.getArmor()));
        drop.addAll(DynamicDropHelper.calculateCategoryDrop(dynamicDropEquipmentCategory.getJewels()));
        return drop;
    }

}
