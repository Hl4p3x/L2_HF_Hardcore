package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.ItemGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.WeaponArmorJewelStats;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;

import java.util.List;
import java.util.Optional;

public class BasicDropCalculator {

    public List<ItemHolder> calculate(L2Character victim, DynamicDropStats dynamicDropStats) {
        Optional<ItemGradeRange> range = ItemGradeRange.byLevel(victim.getLevel());
        if (!range.isPresent()) {
            return Lists.newArrayList();
        }

        GradeInfo gradeInfo = range.get().getGradeInfo();

        return null;
    }

    private List<ItemHolder> calculateEquipmentDrop(GradeInfo gradeInfo, WeaponArmorJewelStats equipmentDropStats) {
        return null;
    }

}
