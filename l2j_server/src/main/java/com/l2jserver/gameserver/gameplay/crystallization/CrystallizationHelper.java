package com.l2jserver.gameserver.gameplay.crystallization;

import com.l2jserver.Config;
import com.l2jserver.gameserver.enums.PrivateStoreType;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.gameserver.model.skills.CommonSkill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrystallizationHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CrystallizationHelper.class);

    public static boolean validateCrystallizationCount(L2PcInstance activeChar, long count, int objectId) {
        if (count <= 0) {
            Util.handleIllegalPlayerAction(activeChar, "[RequestCrystallizeItem] count <= 0! ban! oid: " + objectId + " owner: " + activeChar.getName(), Config.DEFAULT_PUNISH);
            return false;
        }
        return true;
    }

    public static boolean canPlayerCrystallizeNow(L2PcInstance activeChar) {
        if ((activeChar.getPrivateStoreType() != PrivateStoreType.NONE) || activeChar.isInCrystallize()) {
            activeChar.sendPacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
            return false;
        }
        return true;
    }

    public static boolean canItemBeCrystallized(L2PcInstance activeChar, L2ItemInstance item) {
        if (item.isHeroItem()) {
            return false;
        }

        if (item.isShadowItem() || item.isTimeLimitedItem()) {
            return false;
        }

        if (!item.getItem().isCrystallizable() || (item.getItem().getCrystalCount() <= 0) || (item.getItem().getCrystalType() == CrystalType.NONE)) {
            LOG.warn(activeChar.getName() + " (" + activeChar.getObjectId() + ") tried to crystallize " + item.getItem().getId());
            return false;
        }

        if (!activeChar.getInventory().canManipulateWithItemId(item.getId())) {
            activeChar.sendMessage("You cannot use this item.");
            return false;
        }

        return true;
    }

    public static boolean hasCrystallizationSkills(L2PcInstance activeChar, int objectId) {
        int skillLevel = activeChar.getSkillLevel(CommonSkill.CRYSTALLIZE.getId());
        if (skillLevel <= 0) {
            activeChar.sendPacket(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            if ((activeChar.getRace() != Race.DWARF) && (activeChar.getClassId().ordinal() != 117) && (activeChar.getClassId().ordinal() != 55)) {
                LOG.info("Player " + activeChar.getClient() + " used crystallize with classid: " + activeChar.getClassId().ordinal());
            }
            return false;
        }

        L2ItemInstance itemToRemove = activeChar.getInventory().getItemByObjectId(objectId);
        // Check if the char can crystallize items and return if false;
        boolean canCrystallize = true;

        switch (itemToRemove.getItem().getItemGradeSPlus()) {
            case C: {
                if (skillLevel <= 1) {
                    canCrystallize = false;
                }
                break;
            }
            case B: {
                if (skillLevel <= 2) {
                    canCrystallize = false;
                }
                break;
            }
            case A: {
                if (skillLevel <= 3) {
                    canCrystallize = false;
                }
                break;
            }
            case S: {
                if (skillLevel <= 4) {
                    canCrystallize = false;
                }
                break;
            }
        }

        if (!canCrystallize) {
            activeChar.sendPacket(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
        }

        return canCrystallize;
    }

    public static long normalizeCrystallizeCount(L2PcInstance activeChar, long count, int objectId) {
        L2ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
        if (count > item.getCount()) {
            return activeChar.getInventory().getItemByObjectId(objectId).getCount();
        } else {
            return count;
        }
    }

    public static void crystallizeItem(L2PcInstance activeChar, long count, int objectId) {
        activeChar.setInCrystallize(true);

        L2ItemInstance itemToRemove = activeChar.getInventory().getItemByObjectId(objectId);

        SystemMessage sm;
        if (itemToRemove.isEquipped()) {
            L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getLocationSlot());
            InventoryUpdate iu = new InventoryUpdate();
            for (L2ItemInstance item : unequiped) {
                iu.addModifiedItem(item);
            }
            activeChar.sendPacket(iu);

            if (itemToRemove.getEnchantLevel() > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
                sm.addInt(itemToRemove.getEnchantLevel());
                sm.addItemName(itemToRemove);
            } else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED);
                sm.addItemName(itemToRemove);
            }
            activeChar.sendPacket(sm);
        }

        L2ItemInstance removedItem = activeChar.getInventory()
                .destroyItem("Crystallize", objectId, count, activeChar, null);

        InventoryUpdate iu = new InventoryUpdate();
        iu.addRemovedItem(removedItem);
        activeChar.sendPacket(iu);

        int crystalId = itemToRemove.getItem().getCrystalItemId();
        int crystalAmount = itemToRemove.getCrystalCount();
        L2ItemInstance createdItem = activeChar.getInventory()
                .addItem("Crystallize", crystalId, crystalAmount, activeChar, activeChar);

        sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CRYSTALLIZED);
        sm.addItemName(removedItem);
        activeChar.sendPacket(sm);

        sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
        sm.addItemName(createdItem);
        sm.addLong(crystalAmount);
        activeChar.sendPacket(sm);

        activeChar.broadcastUserInfo();

        L2World world = L2World.getInstance();
        world.removeObject(removedItem);

        activeChar.setInCrystallize(false);
    }

}
