package handlers.communityboard.custom;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class PaymentHelper {

    public static ProcessResult payForService(L2PcInstance player, long price) {
        if (player.getClassId().level() <= 2) {
            // Free
            return ProcessResult.success();
        }

        boolean result = player.reduceAdena("Service", price, null, true);
        if (result) {
            return ProcessResult.success();
        } else {
            return ProcessResult.failure("Not enough Adena");
        }
    }

}
