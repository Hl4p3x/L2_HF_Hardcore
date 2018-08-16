package handlers.communityboard.custom;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class BuffCondition {

    public static ProcessResult checkCondition(L2PcInstance player) {
        if (player.isAlikeDead()) {
            return ProcessResult.failure("You cannot do this while being dead");
        }

        if (player.isInOlympiadMode()) {
            return ProcessResult.failure("You cannot do this while being in Olympiad");
        }

        if (player.isPvpFlagged()) {
            return ProcessResult.failure("You cannot do this while in PvP mode");
        }

        if (player.isFlying()) {
            return ProcessResult.failure("You cannot do this while flying");
        }

        if (player.isInCombat()) {
            return ProcessResult.failure("You cannot do this in combat");
        }

        return ProcessResult.success();
    }

}
