package handlers.communityboard.custom;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class CharacterBlockHelper {

    public static void block(L2Character target) {
        // target.stopAndDisable();
        target.setIsCastingNow(true);
        if (target instanceof L2Summon) {
            L2Summon summon = (L2Summon) target;
            L2PcInstance owner = summon.getOwner();
            owner.stopAndDisable();
            owner.setIsCastingNow(true);
        }
    }

    public static void unblock(L2Character target) {
        target.setIsCastingNow(false);
        // target.startAndEnable();
        if (target instanceof L2Summon) {
            L2Summon summon = (L2Summon) target;
            L2PcInstance owner = summon.getOwner();
            owner.setIsCastingNow(false);
            owner.startAndEnable();
            summon.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }

}
