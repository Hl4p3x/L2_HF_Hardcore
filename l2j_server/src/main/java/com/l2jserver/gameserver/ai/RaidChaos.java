package com.l2jserver.gameserver.ai;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jserver.util.Rnd;

public class RaidChaos {

    private int _chaosTime = 0;

    private final L2Attackable npc;
    private final Aggro aggro;

    public RaidChaos(L2AttackableAI attackableAi, L2Attackable npc) {
        this.npc = npc;
        this.aggro = new Aggro(attackableAi, npc);
    }

    private boolean handleRaidBossChaos() {
        if (!((L2MonsterInstance) npc).hasMinions()) {
            if (_chaosTime > Config.RAID_CHAOS_TIME) {
                if (Rnd.get(100) <= (100 - ((npc.getCurrentHp() * Config.RAID_CHAOS_SOLO_BOSS_TRESHOLD) / npc.getMaxHp()))) {
                    aggro.aggroReconsider();
                    _chaosTime = 0;
                    return true;
                }
            }
        } else {
            if (_chaosTime > Config.RAID_CHAOS_TIME) {
                if (Rnd.get(100) <= (100 - ((npc.getCurrentHp() * Config.RAID_CHAOS_GROUP_BOSS_TRESHOLD) / npc.getMaxHp()))) {
                    aggro.aggroReconsider();
                    _chaosTime = 0;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleRaidMinionChaos() {
        if (_chaosTime > Config.MINION_CHAOS_TIME) {
            if (Rnd.get(100) <= (100 - ((npc.getCurrentHp() * Config.RAID_CHAOS_MINION_THRESHOLD) / npc.getMaxHp()))) {
                aggro.aggroReconsider();
                _chaosTime = 0;
                return true;
            }
        }
        return false;
    }

    private boolean handleGrandBossChaos() {
        if (_chaosTime > Config.GRAND_CHAOS_TIME) {
            double chaosRate = 100 - ((npc.getCurrentHp() * Config.RAID_CHAOS_GRAND_BOSS_THRESHOLD) / npc.getMaxHp());
            if (((chaosRate <= 10) && (Rnd.get(100) <= 10)) || ((chaosRate > 10) && (Rnd.get(100) <= chaosRate))) {
                aggro.aggroReconsider();
                _chaosTime = 0;
                return true;
            }
        }
        return false;
    }

    public boolean handleRaidChaos() {
        _chaosTime++;
        if (npc instanceof L2RaidBossInstance) {
            return handleRaidBossChaos();
        } else if (npc instanceof L2GrandBossInstance) {
            return handleGrandBossChaos();
        } else {
            return handleRaidMinionChaos();
        }
    }

}
