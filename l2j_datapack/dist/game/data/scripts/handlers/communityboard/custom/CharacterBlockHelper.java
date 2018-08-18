package handlers.communityboard.custom;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class CharacterBlockHelper {

    public static void block(L2Character target) {
        target.setIsCastingNow(true);
        target.stopAndDisable();
        if (target instanceof L2Summon) {
            L2Summon summon = (L2Summon) target;
            L2PcInstance owner = summon.getOwner();
            owner.setIsCastingNow(true);
            owner.stopAndDisable();
        }
    }

    public static void unblock(L2Character target) {
        target.startAndEnable();
        target.setIsCastingNow(false);
        if (target instanceof L2Summon) {
            L2Summon summon = (L2Summon) target;
            L2PcInstance owner = summon.getOwner();
            owner.startAndEnable();
            owner.setIsCastingNow(false);
        }
    }

}
