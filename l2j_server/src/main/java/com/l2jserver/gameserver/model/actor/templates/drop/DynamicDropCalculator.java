package com.l2jserver.gameserver.model.actor.templates.drop;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.datatables.categorized.GradedItemsData;
import com.l2jserver.gameserver.datatables.categorized.ItemPartsData;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.chances.ItemGradeChanceMods;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.gameserver.model.items.parts.ItemPart;
import com.l2jserver.util.Rnd;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicDropCalculator {

    public List<ItemHolder> calculate(L2Character victim, L2Character killer) {
        Optional<ItemGradeRange> range = ItemGradeRange.byLevel(victim.getLevel());
        if (!range.isPresent()) {
            return Lists.newArrayList();
        }

        GradeInfo gradeInfo = range.get().getGradeInfo();
        Collection<GradedItem> possibleDropItems = GradedItemsData.getInstance().getGradedItemsMap().get(gradeInfo);
        Set<Integer> possibleDropItemIds = possibleDropItems.stream().map(GradedItem::getItemId).collect(Collectors.toSet());

        List<ItemHolder> partHolders = ItemPartsData.getInstance().getItemParts().stream().filter(part -> possibleDropItemIds.contains(part.getItemId()) && Rnd.getDouble(100) <= 70).map(itemPart -> new ItemHolder(itemPart.getPartId(), Rnd.get(2) + 1)).collect(Collectors.toList());
        List<ItemHolder> itemHolders = possibleDropItems.stream()
                .filter(gradedItem -> {
                    return Rnd.getDouble(100) <= Math.max(ItemGradeChanceMods.valueOf(gradedItem.getGradeInfo().getGrade().name()).getMod() * 10, 100);
                })
                .map(gradedItem -> new ItemHolder(gradedItem.getItemId(), 1))
                .collect(Collectors.toList());

        List<ItemHolder> allDrop = new ArrayList<>();
        allDrop.addAll(partHolders);
        allDrop.addAll(itemHolders);
        return allDrop;
    }

    public Set<Integer> getAllDynamicItemsIds() {
        return Stream.concat(
                GradedItemsData.getInstance().getGradedItems().stream().map(GradedItem::getItemId),
                ItemPartsData.getInstance().getItemParts().stream().map(ItemPart::getPartId)).collect(Collectors.toSet()
        );
    }

    public static DynamicDropCalculator getInstance() {
        return DynamicDropCalculator.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final DynamicDropCalculator INSTANCE = new DynamicDropCalculator();
    }

}
