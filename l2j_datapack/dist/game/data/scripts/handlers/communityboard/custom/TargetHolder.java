package handlers.communityboard.custom;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.Objects;
import java.util.StringJoiner;

public class TargetHolder {

    private L2Character master;
    private L2Character summon;

    public TargetHolder(L2Character master, L2Character summon) {
        this.master = master;
        this.summon = summon;
    }

    public static TargetHolder of(L2PcInstance player) {
        return new TargetHolder(player, null);
    }

    public static TargetHolder of(L2PcInstance player, L2Character summon) {
        return new TargetHolder(player, summon);
    }

    public L2Character getMaster() {
        return master;
    }

    public L2Character getSummon() {
        return summon;
    }

    public boolean isSummonTarget() {
        return summon != null;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TargetHolder.class.getSimpleName() + "[", "]")
                .add(Objects.toString(master))
                .add(Objects.toString(summon))
                .toString();
    }

}
