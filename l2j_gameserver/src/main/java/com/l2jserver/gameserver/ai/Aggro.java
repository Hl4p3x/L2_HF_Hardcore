package com.l2jserver.gameserver.ai;

import com.l2jserver.common.util.Rnd;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2GuardInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import java.util.Collection;

public class Aggro {

    private L2AttackableAI attackableAi;
    private L2Attackable npc;

    public Aggro(L2AttackableAI attackableAi, L2Attackable npc) {
        this.attackableAi = attackableAi;
        this.npc = npc;
    }

    private void addMostHate(L2Character mostHate, L2Character obj) {
        if (mostHate != null && !mostHate.isDead()) {
            long hateDiff = npc.getHating(mostHate) - npc.getHating(obj);
            npc.addDamageHate(obj, 0, hateDiff + 2000);
        } else {
            npc.addDamageHate(obj, 0, 2000);
        }
    }

    public void aggroReconsider() {
        L2Character mostHate = npc.getMostHated();
        if (npc.getHateList() != null) {

            int rand = Rnd.get(npc.getHateList().size());
            int count = 0;
            for (L2Character obj : npc.getHateList()) {
                if (count < rand) {
                    count++;
                    continue;
                }

                if ((obj == null) || !GeoData.getInstance().canSeeTarget(npc, obj) || obj.isDead() || (obj == attackableAi.getAttackTarget()) || (obj == npc)) {
                    continue;
                }

                try {
                    npc.setTarget(attackableAi.getAttackTarget());
                } catch (NullPointerException e) {
                    continue;
                }
                addMostHate(mostHate, obj);
                npc.setTarget(obj);
                attackableAi.setAttackTarget(obj);
                return;
            }
        }

        if (npc instanceof L2GuardInstance) {
            return;
        }

        Collection<L2Object> objs = npc.getKnownList().getKnownObjects().values();
        for (L2Object target : objs) {
            L2Character obj = null;
            if (target instanceof L2Character) {
                obj = (L2Character) target;
            } else {
                continue;
            }

            if (!GeoData.getInstance().canSeeTarget(npc, obj) || obj.isDead() || (obj != mostHate) || (obj == npc)) {
                continue;
            }
            if (obj instanceof L2PcInstance) {
                addMostHate(mostHate, obj);
                npc.setTarget(obj);
                attackableAi.setAttackTarget(obj);
            } else if (obj instanceof L2Attackable) {
                if (npc.isChaos()) {
                    if (((L2Attackable) obj).isInMyClan(npc)) {
                        continue;
                    }

                    addMostHate(mostHate, obj);
                    npc.setTarget(obj);
                    attackableAi.setAttackTarget(obj);
                }
            } else if (obj instanceof L2Summon) {
                addMostHate(mostHate, obj);
                npc.setTarget(obj);
                attackableAi.setAttackTarget(obj);
            }
        }
    }

}
