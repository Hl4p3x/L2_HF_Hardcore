package custom.votes.config;

import com.l2jserver.gameserver.model.holders.ItemHolder;
import custom.votes.ShortNpc;
import custom.votes.VoteSource;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class VoteConfig {

    private ShortNpc npc;
    private List<ItemHolder> rewards;
    private List<VoteSource> sources;

    public ShortNpc getNpc() {
        return npc;
    }

    public List<ItemHolder> getRewards() {
        return rewards;
    }

    public List<VoteSource> getSources() {
        return sources;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VoteConfig.class.getSimpleName() + "[", "]")
                .add(Objects.toString(npc))
                .add(Objects.toString(rewards))
                .add(Objects.toString(sources))
                .toString();
    }

}
