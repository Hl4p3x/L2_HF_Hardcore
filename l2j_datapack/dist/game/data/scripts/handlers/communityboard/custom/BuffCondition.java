package handlers.communityboard.custom;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.localization.Strings;

public class BuffCondition {

    public static ProcessResult checkCondition(L2PcInstance player) {
        if (player.isAlikeDead()) {
            return ProcessResult.failure(Strings.of(player).get("you_cannot_do_this_while_being_dead"));
        }

        if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegisteredInComp(player)) {
            return ProcessResult.failure(Strings.of(player).get("you_cannot_do_this_while_being_in_olympiad"));
        }

        if (player.isAttackingDisabled() || player.isCastingNow() || player.isCastingSimultaneouslyNow()) {
            return ProcessResult.failure(Strings.of(player).get("you_cannot_do_this_because_you_are_busy"));
        }

        if (player.isPvpFlagged()) {
            return ProcessResult.failure(Strings.of(player).get("you_cannot_do_this_while_in_pvp_mode"));
        }

        if (player.isFlying()) {
            return ProcessResult.failure(Strings.of(player).get("you_cannot_do_this_while_flying"));
        }

        if (player.isInCombat()) {
            return ProcessResult.failure(Strings.of(player).get("you_cannot_do_this_in_combat"));
        }

        return ProcessResult.success();
    }

}
