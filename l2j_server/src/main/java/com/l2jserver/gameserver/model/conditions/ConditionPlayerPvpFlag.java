package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

public class ConditionPlayerPvpFlag extends Condition {

    private final boolean expected;

    public ConditionPlayerPvpFlag(boolean expected) {
        this.expected = expected;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        if (effector instanceof L2PcInstance) {
            L2PcInstance pcInstance = (L2PcInstance) effector;
            return pcInstance.isPvpFlagged() == expected;
        }

        return true;
    }

}
