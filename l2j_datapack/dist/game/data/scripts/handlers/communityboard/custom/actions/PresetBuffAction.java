package handlers.communityboard.custom.actions;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.FakeCast;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.*;
import handlers.communityboard.custom.bufflists.Buffs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class PresetBuffAction implements BoardAction {

    private final static Logger LOG = LoggerFactory.getLogger(PresetBuffAction.class);

    private final Buffs buffs;

    public PresetBuffAction(Buffs buffs) {
        this.buffs = buffs;
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.getArgs().size() != 1) {
            LOG.warn("{} is trying to use buff preset with invalid args number {}", player, args.getArgs().size());
            return ProcessResult.failure(Strings.of(player).get("invalid_preset_buff_request"));
        }

        TargetHolder targetHolder = TargetHelper.parseTarget(player, args.getArgs().get(0));

        ProcessResult checkResult = BuffCondition.checkCondition(player);
        if (checkResult.isFailure()) {
            return checkResult;
        }

        ProcessResult paymentResult = PaymentHelper.payForService(player, Config.COMMUNITY_DEFAULT_PRESET_PRICE);
        if (paymentResult.isFailure()) {
            return paymentResult;
        }

        int delay = GameTimeController.TICKS_PER_SECOND * GameTimeController.MILLIS_IN_TICK * 2;

        if (targetHolder.isSummonTarget()) {
            List<FakeCast> fakeCasts = buffs.getBuffs().stream().map(buff -> new FakeCast(buff, delay, () -> buff.getSkill().applyEffects(targetHolder.getMaster(), targetHolder.getSummon()))).collect(Collectors.toList());
            targetHolder.getMaster().fakeCastSeries(fakeCasts, targetHolder.getSummon());
        } else {
            List<FakeCast> fakeCasts = buffs.getBuffs().stream().map(buff -> new FakeCast(buff, delay, () -> buff.getSkill().applyEffects(targetHolder.getMaster(), targetHolder.getMaster()))).collect(Collectors.toList());
            targetHolder.getMaster().fakeCastSeries(fakeCasts, targetHolder.getMaster());
        }

        return ProcessResult.success();
    }

}
