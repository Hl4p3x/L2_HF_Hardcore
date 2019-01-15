package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.datatables.categorized.*;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropTable;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicDropCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicDropCalculator.class);

    private Set<Integer> managedItemIds = new HashSet<>();
    private GeneralDropCalculator generalDropCalculator = new GeneralDropCalculator();
    private CustomDropCalculator customDropCalculator = new CustomDropCalculator();

    public DynamicDropCalculator() {
        load();
    }

    public void reload() {
        GradedItemsDropDataTable.getInstance().load();
        ItemPartsDropDataTable.getInstance().load();
        ItemRecipesDropDataTable.getInstance().load();
        CraftResourcesDropDataTable.getInstance().load();
        ScrollDropDataTable.getInstance().load();

        load();
    }

    public void load() {
        managedItemIds = new HashSet<>();
        managedItemIds.addAll(GradedItemsDropDataTable.getInstance().getGradedItemsIds());
        managedItemIds.addAll(ItemPartsDropDataTable.getInstance().getAllItemPartsIds());
        managedItemIds.addAll(ItemRecipesDropDataTable.getInstance().getRecipeIds());
        managedItemIds.addAll(CraftResourcesDropDataTable.getInstance().getResourceIds());
        managedItemIds.addAll(ScrollDropDataTable.getInstance().getScrollIds());
        managedItemIds.addAll(DynamicDropTable.getInstance().getCustomDropIds());
        LOG.info("Dynamic Drop Calculator initialized");
    }

    public List<ItemHolder> calculate(L2Character victim) {
        List<ItemHolder> drop = new ArrayList<>(generalDropCalculator.calculate(DynamicDropTable.getInstance().getDynamicNpcDropData(victim)));
        drop.addAll(customDropCalculator.calculate(DynamicDropTable.getInstance().getCustomDrop(victim)));
        return drop;
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
