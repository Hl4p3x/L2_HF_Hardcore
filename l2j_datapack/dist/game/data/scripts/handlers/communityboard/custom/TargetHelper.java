package handlers.communityboard.custom;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.Optional;

public class TargetHelper {

    public static TargetHolder parseTarget(L2PcInstance player, String targetArg) {
        if (targetArg == null) {
            return TargetHolder.of(player);
        }

        Optional<Target> targetOption = Target.parse(targetArg);
        if (targetOption.isEmpty()) {
            return TargetHolder.of(player);
        }

        Target targetValue = targetOption.get();
        if (targetValue.equals(Target.SUMMON)) {
            L2Character summon = player.getSummon();
            if (summon != null) {
                return TargetHolder.of(player, summon);
            }
        }

        return TargetHolder.of(player);
    }

}
