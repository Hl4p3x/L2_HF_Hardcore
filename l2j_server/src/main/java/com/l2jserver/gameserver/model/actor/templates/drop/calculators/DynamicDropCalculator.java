package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.datatables.categorized.*;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropTable;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicDropCalculator {

    private Set<Integer> managedItemIds = new HashSet<>();
    private GeneralDropCalculator generalDropCalculator = new GeneralDropCalculator();

    public DynamicDropCalculator() {
        load();
    }

    private void load() {
        managedItemIds.addAll(GradedItemsDropDataTable.getInstance().getGradedItemsIds());
        managedItemIds.addAll(ItemPartsDropDataTable.getInstance().getAllItemPartsIds());
        managedItemIds.addAll(ItemRecipesDropDataTable.getInstance().getRecipeIds());
        managedItemIds.addAll(CraftResourcesDropDataTable.getInstance().getResourceIds());
        managedItemIds.addAll(ScrollDropDataTable.getInstance().getScrollIds());
    }

    // Add Dynasty Essence, Attribute Stones, Dual Craft Stamp, SA Stones

    public List<ItemHolder> calculate(L2Character victim) {
        if (victim.isRaid()) {
            return generalDropCalculator.calculate(victim, DynamicDropTable.getInstance().getAllDynamicDropData().getRaid());
        } else {
            return generalDropCalculator.calculate(victim, DynamicDropTable.getInstance().getAllDynamicDropData().getMobs());
        }
    }

    public Set<Integer> getAllDynamicItemsIds() {
        return managedItemIds;
    }

    public static DynamicDropCalculator getInstance() {
        return DynamicDropCalculator.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final DynamicDropCalculator INSTANCE = new DynamicDropCalculator();
    }

}
