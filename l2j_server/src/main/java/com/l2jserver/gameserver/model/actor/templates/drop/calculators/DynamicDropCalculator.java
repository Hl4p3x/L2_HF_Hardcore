package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.datatables.categorized.*;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropTable;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
        // Fake manage, for drop ignore
        managedItemIds.addAll(Arrays.asList(
                17,   // Wooden Arrow
                1341, // Bone Arrow
                1342, // Steel Arrow
                1343, // Silver Arrow
                1344, // Mithril Arrow
                1345, // Shining Arrow
                5268, // Recipe: Greater Soulshot (D) Compressed Package (100%)
                5269, // Recipe: Greater Soulshot (C) Compressed Package(100%)
                5270, // Recipe: Greater Soulshot (B) Compressed Package(100%)
                5271, // Recipe: Greater Soulshot (A) Compressed Package(100%)
                5272, // Recipe: Greater Soulshot (S) Compressed Package(100%)
                5273, // Recipe: Greater Spiritshot (D) Compressed Package(100%)
                5274, // Recipe: Greater Spiritshot (C) Compressed Package(100%)
                5275, // Recipe: Greater Spiritshot (B) Compressed Package(100%)
                5276, // Recipe: Greater Spiritshot (A) Compressed Package(100%)
                5277, // Recipe: Greater Spiritshot (S) Compressed Package(100%)
                5278, // Recipe: Greater Blessed Spiritshot (D) Compressed Package(100%)
                5279, // Recipe: Greater Blessed Spiritshot (C) Compressed Package(100%)
                5280, // Recipe: Greater Blessed Spiritshot (B) Compressed Package(100%)
                5281, // Recipe: Greater Blessed Spiritshot (A) Compressed Package(100%)
                5282  // Recipe: Greater Blessed Spiritshot (S) Compressed Package(100%)
        ));
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
