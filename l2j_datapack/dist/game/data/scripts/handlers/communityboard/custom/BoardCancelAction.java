package handlers.communityboard.custom;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BoardCancelAction implements BoardAction {

    private final static Logger LOG = LoggerFactory.getLogger(BoardCancelAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.getArgs().size() != 1) {
            LOG.warn("{} is trying to use cancel with invalid args number {}", player, args.getArgs().size());
            return ProcessResult.failure("Invalid restore request");
        }

        Optional<L2Character> targetOption = TargetHelper.parseTarget(player, args.getArgs().get(0));
        if (!targetOption.isPresent()) {
            LOG.warn("Player {} tried to use cancel on an invalid target {}", player, args.getArgs().get(0));
            return ProcessResult.failure("Invalid target " + args.getArgs().get(0));
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
        target.stopAndDisable();
        MagicSkillUse msk = new MagicSkillUse(target, cancellationId, 1, delay, 0);
        Broadcast.toSelfAndKnownPlayersInRadius(target, msk, 900);

        player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(() -> {
            target.getEffectList().stopAllBuffs(true, false);
            target.getEffectList().stopAllDances(true);
            target.startAndEnable();
        }, delay));

        return ProcessResult.success();
    }

}
