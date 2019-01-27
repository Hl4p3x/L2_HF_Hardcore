package handlers.communityboard.custom.actions;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoardRestoreAction implements BoardAction {

    private final static Logger LOG = LoggerFactory.getLogger(BoardRestoreAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (!Config.COMMUNITY_RESTORE_ENABLED) {
            return ProcessResult.failure(Strings.of(player).get("restore_is_disabled"));
        }

        if (args.getArgs().size() != 1) {
            LOG.warn("{} is trying to use restore with invalid args number {}", player, args.getArgs().size());
            return ProcessResult.failure(Strings.of(player).get("invalid_restore_request"));
        }

        String targetString = args.getArgs().get(0);
        TargetHolder targetHolder = TargetHelper.parseTarget(player, targetString);

        ProcessResult checkResult = BuffCondition.checkCondition(player);
        if (checkResult.isFailure()) {
            return checkResult;
        }

        if (!player.isInsideZone(ZoneId.PEACE)) {
            ProcessResult.failure(Strings.of(player).get("can_only_restore_inside_peace_zone"));
        }

        ProcessResult paymentResult = PaymentHelper.payForService(player, Config.COMMUNITY_RESTORE_PRICE);
        if (paymentResult.isFailure()) {
            return paymentResult;
        }

        int delay = GameTimeController.TICKS_PER_SECOND * GameTimeController.MILLIS_IN_TICK;
        SkillHolder greaterHeal = new SkillHolder(1217, 1);

        if (targetHolder.isSummonTarget()) {
            targetHolder.getMaster().fakeCast(greaterHeal, delay, () -> setVitalsToMax(targetHolder.getSummon()), targetHolder.getSummon());
        } else {
            targetHolder.getMaster().fakeCast(greaterHeal, delay, () -> setVitalsToMax(targetHolder.getMaster()), targetHolder.getMaster());
        }

        return ProcessResult.success();
    }

    private void setVitalsToMax(L2Character target) {
        target.setCurrentCp(target.getMaxRecoverableCp());
        target.setCurrentHp(target.getMaxRecoverableHp());
        target.setCurrentMp(target.getMaxRecoverableMp());
    }

}
