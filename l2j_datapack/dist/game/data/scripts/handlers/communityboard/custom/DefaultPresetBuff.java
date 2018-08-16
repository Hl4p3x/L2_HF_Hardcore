package handlers.communityboard.custom;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultPresetBuff implements BoardAction {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultPresetBuff.class);

    private final String name;
    private final List<Skill> buffs;

    public DefaultPresetBuff(String name) {
        this.name = name;
        this.buffs = new ArrayList<>();
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.getArgs().size() != 2) {
            LOG.warn("{} is trying to use buff preset with invalid args number {}", player, args.getArgs().size());
            return ProcessResult.failure("Invalid restore request");
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

        L2Character target = targetOption.get();

        Runnable stopAndDisableTask = target::stopAndDisable;

/*        player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(() -> {
            MagicSkillUse msk = new MagicSkillUse(target, cancellationId, 1, delay, 0);
            Broadcast.toSelfAndKnownPlayersInRadius(target, msk, 900);
            target.startAndEnable();
        }, delay));

        buffs.map(buff -> {

        });*/

        Runnable startAndEnableTask = target::startAndEnable;

        return ProcessResult.success();
    }

}
