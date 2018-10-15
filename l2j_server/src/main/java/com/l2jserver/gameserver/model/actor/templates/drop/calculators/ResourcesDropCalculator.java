package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropCategory;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.ArrayList;
import java.util.List;

public class ResourcesDropCalculator {

    public List<ItemHolder> calculate(List<DynamicDropCategory> resourceDropCategories) {
        List<ItemHolder> drop = new ArrayList<>();
        for (DynamicDropCategory resourceDropCategory : resourceDropCategories) {
            drop.addAll(DynamicDropHelper.calculateCategoryDrop(resourceDropCategory));
        }
        return drop;
    }

}
