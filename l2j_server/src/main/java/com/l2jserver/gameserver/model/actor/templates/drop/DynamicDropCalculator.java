package com.l2jserver.gameserver.model.actor.templates.drop;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.datatables.categorized.GradedEquipmentDataTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.chances.EquipmentGradeChanceMods;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.util.Rnd;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicDropCalculator {

    public List<ItemHolder> calculate(L2Character victim, L2Character killer) {
        Optional<EquipmentGradeRanges> range = EquipmentGradeRanges.byLevel(victim.getLevel());
        if (!range.isPresent()) {
            return Lists.newArrayList();
        }

        GradeInfo gradeInfo = range.get().getGradeInfo();
        Collection<GradedItem> possibleDropItems = GradedEquipmentDataTable.getInstance().getGradedItemsMap().get(gradeInfo);

        return possibleDropItems.stream()
                .filter(gradedItem -> {
                    return Rnd.getDouble(100) <= Math.max(EquipmentGradeChanceMods.valueOf(gradedItem.getGradeInfo().getGrade().name()).getMod() * 10, 100);
                })
                .map(gradedItem -> new ItemHolder(gradedItem.getItemId(), 1))
                .collect(Collectors.toList());
    }

    public Set<Integer> getAllDynamicItemsIds() {
        return GradedEquipmentDataTable.getInstance().getGradedItems().stream().map(GradedItem::getItemId).collect(Collectors.toSet());
    }

    public static DynamicDropCalculator getInstance() {
        return DynamicDropCalculator.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final DynamicDropCalculator INSTANCE = new DynamicDropCalculator();
    }

}
