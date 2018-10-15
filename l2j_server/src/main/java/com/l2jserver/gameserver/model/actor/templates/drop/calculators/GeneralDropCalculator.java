package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropGradeData;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.ArrayList;
import java.util.List;

public class GeneralDropCalculator {

    private final EquipmentDropCalculator equipmentDropCalculator = new EquipmentDropCalculator();
    private final ResourcesDropCalculator resourcesDropCalculator = new ResourcesDropCalculator();
    private final ScrollsDropCalculator scrollsDropCalculator = new ScrollsDropCalculator();

    public List<ItemHolder> calculate(DynamicDropGradeData dynamicDropGradeData) {
        List<ItemHolder> drop = new ArrayList<>();
        drop.addAll(equipmentDropCalculator.calculate(dynamicDropGradeData));
        drop.addAll(resourcesDropCalculator.calculate(dynamicDropGradeData.getResources()));
        drop.addAll(scrollsDropCalculator.calculate(dynamicDropGradeData));
        return drop;
    }

}
