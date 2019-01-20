package com.l2jserver.gameserver.transfer.bulk;

import com.l2jserver.gameserver.datatables.categorized.CraftResourcesDropDataTable;
import com.l2jserver.gameserver.datatables.categorized.ItemPartsDropDataTable;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BulkItemTypeFilter {

    public List<L2ItemInstance> filter(BulkItemType type, List<L2ItemInstance> allItems) {
        Stream<L2ItemInstance> stream = allItems.stream().filter(item -> !BulkBlacklist.IDS.contains(item.getId()));

        if (type == BulkItemType.PARTS) {
            Set<Integer> partIds = ItemPartsDropDataTable.getInstance().getAllItemPartsIds();
            return stream.filter(item -> partIds.contains(item.getItemId())).collect(Collectors.toList());
        } else if (type == BulkItemType.RESOURCES) {
            Set<Integer> resourceIds = CraftResourcesDropDataTable.getInstance().getResourceIds();
            return stream.filter(item -> resourceIds.contains(item.getItemId())).collect(Collectors.toList());
        } else {
            return stream.collect(Collectors.toList());
        }
    }

    public static BulkItemTypeFilter getInstance() {
        return BulkItemTypeFilter.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final BulkItemTypeFilter _instance = new BulkItemTypeFilter();
    }

}
