package custom.clan.territory;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class TerritoryOwningRewardData {

    private List<TerritoryOwningReward> castles;
    private List<TerritoryOwningReward> fortresses;

    public TerritoryOwningRewardData() {
    }

    public TerritoryOwningRewardData(List<TerritoryOwningReward> castles, List<TerritoryOwningReward> fortresses) {
        this.castles = castles;
        this.fortresses = fortresses;
    }

    public List<TerritoryOwningReward> getCastles() {
        return castles;
    }

    public List<TerritoryOwningReward> getFortresses() {
        return fortresses;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TerritoryOwningRewardData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(castles))
                .add(Objects.toString(fortresses))
                .toString();
    }

}
