package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropCategory;
import com.l2jserver.gameserver.model.actor.templates.drop.HasDropCategory;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.ArrayList;
import java.util.List;

public class CustomDropCalculator {

    public <T extends HasDropCategory> List<ItemHolder> calculate(List<T> customDropEntries) {
        List<ItemHolder> drop = new ArrayList<>();
        for (T customDropEntry : customDropEntries) {
            drop.addAll(DynamicDropHelper.calculateCategoryDrop(customDropEntry.getDrop()));
        }
        return drop;
    }

    public List<ItemHolder> calculateCategories(List<DynamicDropCategory> categories) {
        List<ItemHolder> drop = new ArrayList<>();
        for (DynamicDropCategory category : categories) {
            drop.addAll(DynamicDropHelper.calculateCategoryDrop(category));
        }
        return drop;
    }

}
