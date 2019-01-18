package com.l2jserver.gameserver.transfer.bulk.shop;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.transfer.bulk.BulkBlacklist;
import com.l2jserver.gameserver.util.Util;

import java.util.List;
import java.util.stream.Collectors;

import static com.l2jserver.gameserver.model.actor.L2Npc.INTERACTION_DISTANCE;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

public class BulkSellService {

    public long sellAllByTypePrice(EtcItemType type, L2PcInstance player) {
        List<L2ItemInstance> allItems = player.getInventory().getAllSellableItemsByType(type).stream().filter(item -> !BulkBlacklist.IDS.contains(item.getId())).collect(Collectors.toList());

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

    public int sellAllByTypeCount(EtcItemType type, L2PcInstance player) {
        return player.getInventory().getAllSellableItemsByType(type).size();
    }

    public void sellAllByType(EtcItemType type, L2PcInstance player) {
        List<L2ItemInstance> allItems = player.getInventory().getAllSellableItemsByType(type);

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

            if (Config.ALLOW_REFUND) {
                player.getInventory().transferItem("Sell", i.getObjectId(), i.getCount(), player.getRefund(), player, target);
            } else {
                player.getInventory().destroyItem("Sell", i.getObjectId(), i.getCount(), player, target);
            }
        }
        player.addAdena("Sell", totalPrice, target, true);

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
