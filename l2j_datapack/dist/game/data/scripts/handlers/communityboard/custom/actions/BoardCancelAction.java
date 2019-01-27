package handlers.communityboard.custom.actions;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoardCancelAction implements BoardAction {

    private final static Logger LOG = LoggerFactory.getLogger(BoardCancelAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.getArgs().size() != 1) {
            LOG.warn("{} is trying to use cancel with invalid args number {}", player, args.getArgs().size());
            return ProcessResult.failure(Strings.of(player).get("invalid_request"));
        }

        String targetString = args.getArgs().get(0);
        TargetHolder targetHolder = TargetHelper.parseTarget(player, targetString);


        ProcessResult checkResult = BuffCondition.checkCondition(player);
        if (checkResult.isFailure()) {
            return checkResult;
        }

        ProcessResult paymentResult = PaymentHelper.payForService(player, Config.COMMUNITY_CANCEL_PRICE);
        if (paymentResult.isFailure()) {
            return paymentResult;
        }

        int delay = GameTimeController.TICKS_PER_SECOND * GameTimeController.MILLIS_IN_TICK;
        SkillHolder cancellation = new SkillHolder(1056, 1);

        if (targetHolder.isSummonTarget()) {
            targetHolder.getMaster().fakeCast(cancellation, delay, () -> cancelBuffs(targetHolder.getSummon()), targetHolder.getSummon())
            ;
        } else {
            targetHolder.getMaster().fakeCast(cancellation, delay, () -> cancelBuffs(targetHolder.getMaster()), targetHolder.getMaster());
        }

        return ProcessResult.success();
    }

    private void cancelBuffs(L2Character target) {
        target.getEffectList().stopAllBuffs(true, false);
        target.getEffectList().stopAllDances(true);
    }

}
