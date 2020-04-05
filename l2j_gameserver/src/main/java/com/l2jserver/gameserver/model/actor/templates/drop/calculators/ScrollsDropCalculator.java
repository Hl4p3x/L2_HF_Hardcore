package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropGradeData;
import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropScrollCategory;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import java.util.ArrayList;
import java.util.List;

public class ScrollsDropCalculator {

    public List<ItemHolder> calculate(DynamicDropGradeData dynamicDropGradeData) {
        List<ItemHolder> drop = new ArrayList<>();
        drop.addAll(calculateScrollDropCategory(dynamicDropGradeData.getWeaponScrolls()));
        drop.addAll(calculateScrollDropCategory(dynamicDropGradeData.getArmorScrolls()));
        drop.addAll(DynamicDropHelper.calculateCategoryDrop(dynamicDropGradeData.getMiscScrolls().getBlessed()));
        return drop;
    }

    private List<ItemHolder> calculateScrollDropCategory(DynamicDropScrollCategory dynamicDropScrollCategory) {
        List<ItemHolder> drop = new ArrayList<>();
        drop.addAll(DynamicDropHelper.calculateCategoryDrop(dynamicDropScrollCategory.getNormal()));
        drop.addAll(DynamicDropHelper.calculateCategoryDrop(dynamicDropScrollCategory.getBlessed()));
        return drop;
    }

}
