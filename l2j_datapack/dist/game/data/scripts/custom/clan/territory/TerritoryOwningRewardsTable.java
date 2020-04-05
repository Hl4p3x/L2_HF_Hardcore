package custom.clan.territory;

import com.l2jserver.common.util.YamlMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerritoryOwningRewardsTable {

    private static final Logger LOG = LoggerFactory.getLogger(TerritoryOwningRewardsTable.class);

    private static final String REWARDS_FILE_PATH = "./territory_owning_rewards.yml";

    private List<TerritoryOwningReward> allTerritoryOwningRewards;

    public void load() {
        try {
            TerritoryOwningRewardData territoryOwningRewards = YamlMapper.getInstance().readValue(getClass().getResourceAsStream(REWARDS_FILE_PATH), TerritoryOwningRewardData.class);

            allTerritoryOwningRewards = new ArrayList<>();
            allTerritoryOwningRewards.addAll(territoryOwningRewards.getCastles());
            allTerritoryOwningRewards.addAll(territoryOwningRewards.getFortresses());

            LOG.info("{} {} reward entries loaded!", TerritoryOwningRewardsManager.LOG_TAG, allTerritoryOwningRewards.size());
        } catch (IOException e) {
            String message = "Could not read rewards file from '" + REWARDS_FILE_PATH + "', please check that file exists and is a valid YML";
            LOG.error("{}" + message + " error: {}", TerritoryOwningRewardsManager.LOG_TAG, e.getMessage());
            throw new IllegalStateException(message);
        }
    }

    public Optional<TerritoryOwningReward> findResidenceReward(int residenceId) {
        return allTerritoryOwningRewards.stream().filter(territoryOwningReward -> territoryOwningReward.getResidenceId() == residenceId).findFirst();
    }

}
