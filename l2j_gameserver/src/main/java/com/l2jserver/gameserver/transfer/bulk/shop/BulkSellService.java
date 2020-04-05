package com.l2jserver.gameserver.transfer.bulk.shop;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.transfer.bulk.BulkItemType;
import com.l2jserver.gameserver.transfer.bulk.BulkItemTypeFilter;
import com.l2jserver.gameserver.util.Util;
import java.util.List;

import static com.l2jserver.gameserver.model.actor.L2Npc.INTERACTION_DISTANCE;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

public class BulkSellService {

    public long sellAllByTypePrice(BulkItemType type, L2PcInstance player) {
        List<L2ItemInstance> allItems = BulkItemTypeFilter.getInstance().filter(type, player.getInventory().getAllItemsByType(type.getItemType()));

        long totalPrice = 0;
        for (L2ItemInstance i : allItems) {
            L2ItemInstance item = player.checkItemManipulation(i.getObjectId(), i.getCount(), "sell");
            if (item == null || !item.isSellable()) {
                continue;
            }

            long price = item.getReferencePrice() / 2;
            totalPrice += price * i.getCount();

            if (((MAX_ADENA / i.getCount()) < price) || (totalPrice > MAX_ADENA)) {
                Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + MAX_ADENA + " adena worth of goods.", Config.DEFAULT_PUNISH);
                return 0L;
            }
        }

        return totalPrice;
    }

    public int sellAllByTypeCount(BulkItemType type, L2PcInstance player) {
        return BulkItemTypeFilter.getInstance().filter(type, player.getInventory().getAllSellableItemsByType(type.getItemType())).size();
    }

    public void sellAllByType(BulkItemType type, L2PcInstance player) {
        List<L2ItemInstance> allItems = BulkItemTypeFilter.getInstance().filter(type, player.getInventory().getAllSellableItemsByType(type.getItemType()));

        if (!player.getClient().getFloodProtectors().getTransaction().tryPerformAction("buy")) {
            player.sendMessage("You are buying too fast.");
            return;
        }

        L2Object target = player.getTarget();
        if (target == null ||
                player.isNotInsideRadius(target, INTERACTION_DISTANCE, true, false) ||
                player.isNotInSameInstanceAs(target)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!(target instanceof L2MerchantInstance)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        InventoryUpdate playerIU = new InventoryUpdate();
        long totalPrice = 0;
        for (L2ItemInstance i : allItems) {
            L2ItemInstance item = player.checkItemManipulation(i.getObjectId(), i.getCount(), "sell");
            if (item == null || !item.isSellable()) {
                continue;
            }

            long price = item.getReferencePrice() / 2;
            totalPrice += price * i.getCount();

            if (((MAX_ADENA / i.getCount()) < price) || (totalPrice > MAX_ADENA)) {
                Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + MAX_ADENA + " adena worth of goods.", Config.DEFAULT_PUNISH);
                return;
            }

            final L2ItemInstance newItem;
            if (Config.ALLOW_REFUND) {
                newItem = player.getInventory().transferItem("Sell", i.getObjectId(), i.getCount(), player.getRefund(), player, target);
            } else {
                newItem = player.getInventory().destroyItem("Sell", i.getObjectId(), i.getCount(), player, target);
            }

            if ((i.getCount() > 0) && (i != newItem)) {
                playerIU.addModifiedItem(i);
            } else {
                playerIU.addRemovedItem(i);
            }
        }
        player.addAdena("Sell", totalPrice, target, true);
        player.sendPacket(playerIU);

        StatusUpdate su = new StatusUpdate(player);
        su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
        player.sendPacket(su);
    }

    public static BulkSellService getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final BulkSellService _instance = new BulkSellService();
    }

}
