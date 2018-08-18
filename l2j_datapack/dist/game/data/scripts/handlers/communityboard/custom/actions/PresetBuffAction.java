package handlers.communityboard.custom.actions;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.util.Broadcast;
import handlers.communityboard.custom.*;
import handlers.communityboard.custom.bufflists.BuffList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PresetBuffAction implements BoardAction {

    private final static Logger LOG = LoggerFactory.getLogger(PresetBuffAction.class);

    private final String name;
    private final BuffList buffs;

    public PresetBuffAction(String name, BuffList buffs) {
        this.name = name;
        this.buffs = buffs;
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.getArgs().size() != 1) {
            LOG.warn("{} is trying to use buff preset with invalid args number {}", player, args.getArgs().size());
            return ProcessResult.failure("Invalid preset buff request");
        }

        Optional<L2Character> targetOption = TargetHelper.parseTarget(player, args.getArgs().get(0));
        if (!targetOption.isPresent()) {
            LOG.warn("Player {} tried to use buff on an invalid target {}", player, args.getArgs().get(0));
            return ProcessResult.failure("Invalid target " + args.getArgs().get(0));
        }

        ProcessResult checkResult = BuffCondition.checkCondition(player);
        if (checkResult.isFailure()) {
            return checkResult;
        }

        ProcessResult paymentResult = PaymentHelper.payForService(player, Config.COMMUNITY_DEFAULT_PRESET_PRICE);
        if (paymentResult.isFailure()) {
            return paymentResult;
        }

        int delay = GameTimeController.TICKS_PER_SECOND * GameTimeController.MILLIS_IN_TICK;

        final L2Character target = targetOption.get();
        target.stopAndDisable();

        int rollingCastDelay = 0;
        int rollingEffectDelay = delay;
        for (SkillHolder buff : buffs.getBuffs()) {
            ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                MagicSkillUse msk = new MagicSkillUse(target, buff.getSkillId(), buff.getSkillLvl(), delay, 0);
                Broadcast.toSelfAndKnownPlayersInRadius(target, msk, 900);
            }, rollingCastDelay);

            player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                buff.getSkill().applyEffects(target, target);
            }, rollingEffectDelay));

            rollingCastDelay += delay;
            rollingEffectDelay += delay;
        }

        ThreadPoolManager.getInstance().scheduleGeneral(target::startAndEnable, rollingEffectDelay);

        return ProcessResult.success();
    }

}
