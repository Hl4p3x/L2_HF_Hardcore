package custom.clan.territory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class TerritoryOwningReward {

    @JsonProperty("residence_id")
    private int residenceId;
    private List<ItemHolder> rewards;

    public TerritoryOwningReward() {
    }

    public TerritoryOwningReward(int residenceId, List<ItemHolder> rewards) {
        this.residenceId = residenceId;
        this.rewards = rewards;
    }

    public int getResidenceId() {
        return residenceId;
    }

    public List<ItemHolder> getRewards() {
        return rewards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerritoryOwningReward that = (TerritoryOwningReward) o;
        return residenceId == that.residenceId &&
                Objects.equals(rewards, that.rewards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(residenceId, rewards);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TerritoryOwningReward.class.getSimpleName() + "[", "]")
                .add(Objects.toString(residenceId))
                .add(Objects.toString(rewards))
                .toString();
    }

}
