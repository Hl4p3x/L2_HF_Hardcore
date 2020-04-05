package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropResourcesCategory;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import java.util.ArrayList;
import java.util.List;

public class ResourcesDropCalculator {

    public List<ItemHolder> calculate(List<DynamicDropResourcesCategory> resourceDropCategories) {
        List<ItemHolder> drop = new ArrayList<>();
        for (DynamicDropResourcesCategory resourceDropCategory : resourceDropCategories) {
            drop.addAll(DynamicDropHelper.calculateCategoryDrop(resourceDropCategory.getDynamicDropCategory()));
        }
        return drop;
    }

}
