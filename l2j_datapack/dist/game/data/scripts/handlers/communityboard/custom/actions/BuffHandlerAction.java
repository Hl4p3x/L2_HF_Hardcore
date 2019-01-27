package handlers.communityboard.custom.actions;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.*;
import handlers.communityboard.custom.bufflists.BuffList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;


public class BuffHandlerAction implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(BuffHandlerAction.class);

    private final Map<String, BuffList> buffMap;

    public BuffHandlerAction(Map<String, BuffList> buffMap) {
        this.buffMap = buffMap;
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.getArgs().size() != 3) {
            LOG.warn("Player {} sent and incorrect buff request", player);
            return ProcessResult.failure(Strings.of(player).get("invalid_buff_request"));
        }

        ProcessResult checkResult = BuffCondition.checkCondition(player);
        if (checkResult.isFailure()) {
            return checkResult;
        }

        String buffCategory = args.getArgs().get(0);
        Integer skillId = Integer.parseInt(args.getArgs().get(1));

        String targetString = args.getArgs().get(2);

        TargetHolder targetHolder = TargetHelper.parseTarget(player, targetString);

        BuffList categoryBuffs = buffMap.get(buffCategory);
        Optional<SkillHolder> buffHolder = categoryBuffs.findBySkillId(skillId);
        if (buffHolder.isEmpty()) {
            return ProcessResult.failure(Strings.of(player).get("this_buff_is_not_available"));
        }

        ProcessResult paymentResult = PaymentHelper.payForService(player, Config.COMMUNITY_SINGLE_BUFF_PRICE);
        if (paymentResult.isFailure()) {
            return paymentResult;
        }

        int delay = GameTimeController.TICKS_PER_SECOND * GameTimeController.MILLIS_IN_TICK;
        if (targetHolder.isSummonTarget()) {
            targetHolder.getMaster().fakeCast(buffHolder.get(), delay, () -> buffHolder.get().getSkill().applyEffects(targetHolder.getMaster(), targetHolder.getSummon()), targetHolder.getSummon());
        } else {
            targetHolder.getMaster().fakeCast(buffHolder.get(), delay, () -> buffHolder.get().getSkill().applyEffects(targetHolder.getMaster(), targetHolder.getMaster()), targetHolder.getMaster());
        }

        return ProcessResult.success();
    }

}
