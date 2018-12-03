package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.model.actor.templates.drop.custom.CustomDropEntry;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.ArrayList;
import java.util.List;

public class CustomDropCalculator {

    public List<ItemHolder> calculate(List<CustomDropEntry> customDropEntries) {
        List<ItemHolder> drop = new ArrayList<>();
        for (CustomDropEntry customDropEntry : customDropEntries) {
            drop.addAll(DynamicDropHelper.calculateCategoryDrop(customDropEntry.getDrop()));
        }
        return drop;
    }

}
