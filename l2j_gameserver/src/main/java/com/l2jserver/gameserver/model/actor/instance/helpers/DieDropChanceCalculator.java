package com.l2jserver.gameserver.model.actor.instance.helpers;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.ZoneId;

public class DieDropChanceCalculator {

    private final boolean isKarmaDeath;
    private final boolean isKillerNpc;
    private final boolean isSiegeKill;
    private final boolean isWarKill;

    public DieDropChanceCalculator(L2PcInstance pcInstance, L2Character killer) {
        this.isKarmaDeath = (pcInstance.getKarma() > 0) && (pcInstance.getPkKills() >= Config.KARMA_PK_LIMIT);
        this.isKillerNpc = (killer instanceof L2Npc);
        this.isSiegeKill = pcInstance.isInsideZone(ZoneId.SIEGE);
        this.isWarKill = pcInstance.getClan() != null && killer.getActingPlayer() != null && pcInstance.getClan().isAtWarWith(killer.getActingPlayer().getClanId());
    }

    public int calculateWeaponDropCount() {
        if (isKarmaDeath) {
            return Config.PK_WEAPON_DROP_COUNT;
        } else {
            return Config.PLAYER_WEAPON_DROP_COUNT;
        }
    }

    public int calculateArmorDropCount() {
        if (isKarmaDeath) {
            return Config.PK_ARMOR_DROP_COUNT;
        } else {
            return Config.PLAYER_ARMOR_DROP_COUNT;
        }
    }

    public int calculateBagDropCount() {
        if (isKarmaDeath) {
            return Config.PK_BAG_DROP_COUNT;
        } else {
            return Config.PLAYER_BAG_DROP_COUNT;
        }
    }

    public double getDropChancePercentageMultiplier() {
        if (isSiegeKill) {
            return Config.PLAYER_SIEGE_DROP_MODIFIER;
        } else if (isWarKill) {
            return Config.PLAYER_WAR_DROP_MODIFIER;
        } else if (isKillerNpc) {
            return Config.PLAYER_NPC_DROP_MODIFIER;
        } else {
            return Config.PLAYER_PVP_DROP_MODIFIER;
        }
    }

    public double calculateWeaponDropChance() {
        if (isKarmaDeath) {
            return Config.PK_WEAPON_DROP_CHANCE;
        } else {
            return Config.PLAYER_WEAPON_DROP_CHANCE * getDropChancePercentageMultiplier();
        }
    }

    public double calculateArmorDropChance() {
        if (isKarmaDeath) {
            return Config.PK_ARMOR_DROP_CHANCE;
        } else {
            return Config.PLAYER_ARMOR_DROP_CHANCE * getDropChancePercentageMultiplier();
        }
    }

    public double calculateBagDropChance() {
        if (isKarmaDeath) {
            return Config.PK_BAG_DROP_CHANCE;
        } else {
            return Config.PLAYER_BAG_DROP_CHANCE * getDropChancePercentageMultiplier();
        }
    }

}
