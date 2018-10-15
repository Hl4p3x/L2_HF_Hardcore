package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.datatables.categorized.*;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropTable;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicDropCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicDropCalculator.class);

    private Set<Integer> managedItemIds = new HashSet<>();
    private GeneralDropCalculator generalDropCalculator = new GeneralDropCalculator();

    public DynamicDropCalculator() {
        load();
    }

    public void load() {
        GradedItemsDropDataTable.getInstance().load();
        ItemPartsDropDataTable.getInstance().load();
        ItemRecipesDropDataTable.getInstance().load();
        CraftResourcesDropDataTable.getInstance().load();
        ScrollDropDataTable.getInstance().load();

        managedItemIds = new HashSet<>();
        managedItemIds.addAll(GradedItemsDropDataTable.getInstance().getGradedItemsIds());
        managedItemIds.addAll(ItemPartsDropDataTable.getInstance().getAllItemPartsIds());
        managedItemIds.addAll(ItemRecipesDropDataTable.getInstance().getRecipeIds());
        managedItemIds.addAll(CraftResourcesDropDataTable.getInstance().getResourceIds());
        managedItemIds.addAll(ScrollDropDataTable.getInstance().getScrollIds());
        LOG.info("Dynamic Drop Calculator initialized");
    }

    // Add Dynasty Essence, Attribute Stones, Dual Craft Stamp, SA Stones

    public List<ItemHolder> calculate(L2Character victim) {
        if (victim.isRaid()) {
            return generalDropCalculator.calculate(DynamicDropTable.getInstance().getDynamicRaidDropData(victim.getLevel()));
        } else {
            return generalDropCalculator.calculate(DynamicDropTable.getInstance().getDynamicMobDropData(victim.getLevel()));
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
