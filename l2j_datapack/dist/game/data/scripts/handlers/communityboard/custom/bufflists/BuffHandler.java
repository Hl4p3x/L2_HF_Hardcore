package handlers.communityboard.custom.bufflists;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.util.Broadcast;
import handlers.communityboard.custom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;


public class BuffHandler implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(BuffHandler.class);

    private final Map<String, BuffList> buffMap;

    public BuffHandler(Map<String, BuffList> buffMap) {
        this.buffMap = buffMap;
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.getArgs().size() != 3) {
            LOG.warn("Player {} sent and incorrect buff request", player);
            return ProcessResult.failure("Incorrect buff request");
        }

        String buffCategory = args.getArgs().get(0);
        Integer skillId = Integer.parseInt(args.getArgs().get(1));

        String targetString = args.getArgs().get(2);

        Optional<L2Character> targetOption = TargetHelper.parseTarget(player, targetString);
        if (!targetOption.isPresent()) {
            LOG.warn("Player {} tried to use buff on an invalid target {}", player, targetString);
            return ProcessResult.failure("Invalid target " + targetString);
        }

        BuffList categoryBuffs = buffMap.get(buffCategory);
        Optional<SkillHolder> buffHolder = categoryBuffs.findBySkillId(skillId);
        if (!buffHolder.isPresent()) {
            return ProcessResult.failure("This buff is not available");
        }

        ProcessResult paymentResult = PaymentHelper.payForService(player, Config.COMMUNITY_SINGLE_BUFF_PRICE);
        if (paymentResult.isFailure()) {
            return paymentResult;
        }

        int delay = GameTimeController.TICKS_PER_SECOND * GameTimeController.MILLIS_IN_TICK;

        L2Character target = targetOption.get();
        target.stopAndDisable();
        MagicSkillUse msk = new MagicSkillUse(target, buffHolder.get().getSkillId(), buffHolder.get().getSkillLvl(), delay, 0);
        Broadcast.toSelfAndKnownPlayersInRadius(target, msk, 900);

        player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(() -> {
            buffHolder.get().getSkill().applyEffects(target, target);
            target.startAndEnable();
        }, delay));

        return ProcessResult.success();
    }

}
