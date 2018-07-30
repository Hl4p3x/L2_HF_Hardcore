package com.l2jserver.gameserver.model.actor.instance.helpers;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.ItemList;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DieDropHelper {

    private final Logger LOG = LoggerFactory.getLogger(DieDropHelper.class);

    private L2PcInstance pcInstance;

    public DieDropHelper(L2PcInstance pcInstance) {
        this.pcInstance = pcInstance;
    }

    public L2ItemInstance crystallizeDrop(L2ItemInstance itemDrop) {
        // Remove the actual item from inventory
        L2ItemInstance removedItem = pcInstance.getInventory()
                .destroyItem("Crystallize", itemDrop.getObjectId(), itemDrop.getCount(), pcInstance, null);

        InventoryUpdate crystallizedItemDropUpdate = new InventoryUpdate();
        crystallizedItemDropUpdate.addRemovedItem(removedItem);
        pcInstance.sendPacket(crystallizedItemDropUpdate);

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CRYSTALLIZED);
        sm.addItemName(removedItem);
        pcInstance.sendPacket(sm);

        // Replace with crystals
        int crystalId = itemDrop.getItem().getCrystalItemId();
        int crystalAmount = itemDrop.getCrystalCount();
        L2ItemInstance crystallizedDrop = pcInstance.getInventory().addItem("Crystallize", crystalId, crystalAmount, pcInstance, pcInstance);

        L2World.getInstance().removeObject(removedItem);
        LOG.debug("Crystallized drop {} into {} crystals", itemDrop, crystalAmount);
        return crystallizedDrop;
    }

    public List<L2ItemInstance> filterNonDroppableItemsAndSliceByLimit(List<L2ItemInstance> items, long limit) {
        return items.stream().filter(item -> !nonDropableCheck(item)).limit(limit).collect(Collectors.toList());
    }

    public boolean nonDropableCheck(L2ItemInstance itemDrop) {
        return itemDrop.isShadowItem() || // Dont drop Shadow Items
                itemDrop.isTimeLimitedItem() || // Dont drop Time Limited Items
                !itemDrop.isDropable() || (itemDrop.getId() == Inventory.ADENA_ID) || // Adena
                (itemDrop.getItem().getType2() == L2Item.TYPE2_QUEST) || // Quest Items
                (pcInstance.hasSummon() && (pcInstance.getSummon().getControlObjectId() == itemDrop.getId())) || // Control Item of active pet
                (Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_ITEMS, itemDrop.getId()) >= 0) || // Item listed in the non droppable item list
                (Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_PET_ITEMS, itemDrop.getId()) >= 0);
    }

    public void handleDrop(List<L2ItemInstance> itemDrops, double itemDropChance, L2Character killer) {
        int dropCount = 0;

        for (L2ItemInstance itemDrop : itemDrops) {
            if (nonDropableCheck(itemDrop)) {
                LOG.debug("Handle drop got an undroppable item {}, skipping", itemDrop);
                continue;
            }

            double roll = Rnd.get(100) + Rnd.nextDouble();
            if (roll < itemDropChance) {
                LOG.debug("Handling drop for {} rolled {} against {}", itemDrop, roll, itemDropChance);
                if (itemDrop.isEquipped()) {
                    LOG.debug("Unequip for {} in slot {}", itemDrop, itemDrop.getLocationSlot());
                    pcInstance.getInventory().unEquipItemInSlot(itemDrop.getLocationSlot());
                }

                if (Config.ALT_PLAYER_DROP_CAN_BE_CRYSTALLIZED &&
                        itemDrop.getCrystalCount() > 0 &&
                        Rnd.get(100) < Config.ALT_PLAYER_DROP_CRYSTALLIZATION_CHANCE) {
                    itemDrop = crystallizeDrop(itemDrop);
                }
                pcInstance.dropItem("DieDrop", itemDrop, killer, true);

                LOG.debug("{} dropped id = {}, count = {}", pcInstance, itemDrop, itemDrop.getCount());
                dropCount++;
            }
        }

        if (dropCount > 0) {
            pcInstance.sendPacket(new ItemList(pcInstance, false));
            pcInstance.broadcastUserInfo();
        }
    }

}
