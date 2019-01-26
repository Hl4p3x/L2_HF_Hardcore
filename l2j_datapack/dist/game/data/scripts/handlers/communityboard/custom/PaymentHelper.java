package handlers.communityboard.custom;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.localization.Strings;

public class PaymentHelper {

    public static ProcessResult payForService(L2PcInstance player, long price) {
        if (checkFreeService(player)) {
            return ProcessResult.success();
        }

        boolean result = player.reduceAdena("Service", price, null, true);
        if (result) {
            return ProcessResult.success();
        } else {
            return ProcessResult.failure(Strings.of(player).get("not_enough_adena"));
        }
    }

    public static boolean checkFreeService(L2PcInstance player) {
        return player.getClassId().level() < Config.FREE_COMMUNITY_TILL_CLASS_LEVEL;
    }

}
