package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.ItemGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropData;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GeneralDropCalculator {

    private final EquipmentDropCalculator equipmentDropCalculator = new EquipmentDropCalculator();
    private final ResourcesDropCalculator resourcesDropCalculator = new ResourcesDropCalculator();
    private final ScrollsDropCalculator scrollsDropCalculator = new ScrollsDropCalculator();

    public List<ItemHolder> calculate(L2Character victim, DynamicDropData dynamicDropData) {
        Optional<ItemGradeRange> range = ItemGradeRange.byLevel(victim.getLevel());
        if (!range.isPresent()) {
            return Lists.newArrayList();
        }

        GradeInfo gradeInfo = range.get().getGradeInfo();
        List<ItemHolder> drop = new ArrayList<>();
        drop.addAll(equipmentDropCalculator.calculate(gradeInfo, dynamicDropData));
        drop.addAll(equipmentDropCalculator.calculate(gradeInfo, dynamicDropData));
        drop.addAll(equipmentDropCalculator.calculate(gradeInfo, dynamicDropData));
        drop.addAll(resourcesDropCalculator.calculate(victim, dynamicDropData));
        drop.addAll(scrollsDropCalculator.calculate(victim, dynamicDropData));

        return drop;
    }

}
