package handlers.communityboard.custom;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.util.Broadcast;

public class BoardHealAction implements BoardAction {

    @Override
    public ProcessResult process(L2PcInstance player, String[] args) {
        ProcessResult checkResult = BuffCondition.checkCondition(player);
        if (checkResult.isFailure()) {
            return checkResult;
        }

        ProcessResult paymentResult = PaymentHelper.payForService(player, Config.COMMUNITY_HEAL_PRICE);
        if (paymentResult.isFailure()) {
            return paymentResult;
        }

        int delay = GameTimeController.TICKS_PER_SECOND * GameTimeController.MILLIS_IN_TICK;
        int greaterHealId = 1217;
        MagicSkillUse msk = new MagicSkillUse(player, greaterHealId, 1, delay, 0);
        Broadcast.toSelfAndKnownPlayersInRadius(player, msk, 900);

        player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(() -> {
            player.setCurrentHp(player.getMaxRecoverableHp());
            player.setCurrentMp(player.getMaxRecoverableMp());
            player.setCurrentCp(player.getMaxRecoverableCp());
        }, delay));

        return ProcessResult.success();
    }

}
