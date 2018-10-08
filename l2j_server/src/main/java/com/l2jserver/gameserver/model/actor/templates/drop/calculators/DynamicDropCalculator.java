package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.datatables.categorized.CraftResourcesData;
import com.l2jserver.gameserver.datatables.categorized.GradedItemsData;
import com.l2jserver.gameserver.datatables.categorized.ItemPartsData;
import com.l2jserver.gameserver.datatables.categorized.ItemRecipesData;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropTable;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicDropCalculator {

    private Set<Integer> managedItemIds = new HashSet<>();
    private BasicDropCalculator basicDropCalculator = new BasicDropCalculator();

    public DynamicDropCalculator() {
        load();
    }

    private void load() {
        managedItemIds.addAll(GradedItemsData.getInstance().getGradedItemsIds());
        managedItemIds.addAll(ItemPartsData.getInstance().getItemPartsIds());
        managedItemIds.addAll(ItemRecipesData.getInstance().getRecipeIds());
        managedItemIds.addAll(CraftResourcesData.getInstance().getResourceIds());
        // Add Scrolls
    }

    // Add Dynasty Essence, Attribute Stones, Dual Craft Stamp, SA Stones

    public List<ItemHolder> calculate(L2Character victim) {
        if (victim.isRaid()) {
            return basicDropCalculator.calculate(victim, DynamicDropTable.getInstance().getAllDynamicDropData().getRaid());
        } else {
            return basicDropCalculator.calculate(victim, DynamicDropTable.getInstance().getAllDynamicDropData().getMobs());
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
