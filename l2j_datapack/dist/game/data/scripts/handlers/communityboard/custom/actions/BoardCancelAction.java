package handlers.communityboard.custom.actions;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.util.Broadcast;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BoardCancelAction implements BoardAction {

    private final static Logger LOG = LoggerFactory.getLogger(BoardCancelAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.getArgs().size() != 1) {
            LOG.warn("{} is trying to use cancel with invalid args number {}", player, args.getArgs().size());
            return ProcessResult.failure(Strings.of(player).get("invalid_request"));
        }

        String targetString = args.getArgs().get(0);
        Optional<L2Character> targetOption = TargetHelper.parseTarget(player, targetString);
        if (targetOption.isEmpty()) {
            LOG.warn("Player {} tried to use cancel on an invalid target {}", player, targetString);
            return ProcessResult.failure(Strings.of(player).get("invalid_target_n").replace("$n", targetString));
        }

        ProcessResult checkResult = BuffCondition.checkCondition(player);
        if (checkResult.isFailure()) {
            return checkResult;
        }

        ProcessResult paymentResult = PaymentHelper.payForService(player, Config.COMMUNITY_CANCEL_PRICE);
        if (paymentResult.isFailure()) {
            return paymentResult;
        }

        int delay = GameTimeController.TICKS_PER_SECOND * GameTimeController.MILLIS_IN_TICK;
        int cancellationId = 1056;

        L2Character target = targetOption.get();
        CharacterBlockHelper.block(target);

        MagicSkillUse msk = new MagicSkillUse(target, cancellationId, 1, delay, 0);
        Broadcast.toSelfAndKnownPlayersInRadius(target, msk, 900);

        player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(() -> {
            target.getEffectList().stopAllBuffs(true, false);
            target.getEffectList().stopAllDances(true);
            CharacterBlockHelper.unblock(target);
        }, delay));

        return ProcessResult.success();
    }

}
