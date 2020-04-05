package com.l2jserver.gameserver.transfer.bulk.store;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.ItemContainer;
import com.l2jserver.gameserver.model.itemcontainer.WarehouseType;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.transfer.bulk.BulkItemType;
import com.l2jserver.gameserver.transfer.bulk.BulkItemTypeFilter;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.localization.Strings;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.l2jserver.gameserver.model.itemcontainer.Inventory.ADENA_ID;

public class BulkStoreService {

    private static final Logger LOG = LoggerFactory.getLogger(BulkStoreService.class);

    private static final Integer GOLEM_TRADER_ID = 13128;

    public void storeAllByType(BulkItemType type, WarehouseType warehouseType, L2PcInstance player) {
        if (player == null) {
            return;
        }

        List<L2ItemInstance> allItems = BulkItemTypeFilter.getInstance().filter(type, player.getInventory().getAllItemsByType(type.getItemType()));

        if (!player.getClient().getFloodProtectors().getTransaction().tryPerformAction("deposit")) {
            player.sendMessage("You are depositing items too fast.");
            return;
        }

        final ItemContainer warehouse;
        if (warehouseType.equals(WarehouseType.PRIVATE)) {
            warehouse = player.getWarehouse();
        } else if (warehouseType.equals(WarehouseType.CLAN) && player.getClan() != null) {
            warehouse = player.getClan().getWarehouse();
        } else {
            warehouse = null;
        }

        if (warehouse == null) {
            return;
        }

        final boolean isPrivate = warehouseType.equals(WarehouseType.PRIVATE);

        final L2Npc manager = player.getLastFolkNPC();
        if (((manager == null) || (!manager.isWarehouse() && manager.getId() != GOLEM_TRADER_ID) || !manager.canInteract(player)) && !player.isGM()) {
            return;
        }

        if (!isPrivate && !player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return;
        }

        if (player.getActiveEnchantItemId() != L2PcInstance.ID_NONE) {
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to use enchant Exploit!", Config.DEFAULT_PUNISH);
            return;
        }

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getKarma() > 0)) {
            return;
        }

        // Freight price from config or normal price per item slot (30)
        final long fee = allItems.size() * 30;
        long currentAdena = player.getAdena();
        int slots = 0;

        for (L2ItemInstance i : allItems) {
            L2ItemInstance item = player.checkItemManipulation(i.getObjectId(), i.getCount(), "deposit");
            if (item == null) {
                LOG.warn("Error depositing a warehouse object for char " + player.getName() + " (validity check)");
                return;
            }

            // Calculate needed adena and slots
            if (item.getId() == ADENA_ID) {
                currentAdena -= i.getCount();
            }
            if (!item.isStackable()) {
                slots += i.getCount();
            } else if (warehouse.getItemByItemId(item.getId()) == null) {
                slots++;
            }
        }

        // Item Max Limit Check
        if (!warehouse.validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        // Check if enough adena and charge the fee
        if ((currentAdena < fee) || !player.reduceAdena(warehouse.getName(), fee, manager, false)) {
            player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
            return;
        }

        // get current tradelist if any
        if (player.getActiveTradeList() != null) {
            return;
        }

        // Proceed to the transfer
        InventoryUpdate playerIU = new InventoryUpdate();
        for (L2ItemInstance i : allItems) {
            // Check validity of requested item
            L2ItemInstance oldItem = player.checkItemManipulation(i.getObjectId(), i.getCount(), "deposit");
            if (oldItem == null) {
                LOG.warn("Error depositing a warehouse object for char " + player.getName() + " (olditem == null)");
                return;
            }

            if (!oldItem.isDepositable(isPrivate) || !oldItem.isAvailable(player, true, isPrivate)) {
                continue;
            }

            final L2ItemInstance newItem = player.getInventory().transferItem(warehouse.getName(), i.getObjectId(), i.getCount(), warehouse, player, manager);
            if (newItem == null) {
                LOG.warn("Error depositing a warehouse object for char " + player.getName() + " (newitem == null)");
                continue;
            }

            if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
                playerIU.addModifiedItem(oldItem);
            } else {
                playerIU.addRemovedItem(oldItem);
            }
        }

        // Send updated item list to the player
        player.sendPacket(playerIU);

        // Update current load status on player
        StatusUpdate su = new StatusUpdate(player);
        su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
        player.sendPacket(su);
        player.sendMessage(Strings.of(player).get("successfully_deposited_n_items").replace("$n", String.valueOf(allItems.size())));
    }

    public static BulkStoreService getInstance() {
        return BulkStoreService.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final BulkStoreService _instance = new BulkStoreService();
    }

}
