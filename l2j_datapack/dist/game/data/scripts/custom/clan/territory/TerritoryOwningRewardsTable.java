package custom.clan.territory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.l2jserver.util.ObjectMapperYamlSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TerritoryOwningRewardsTable {

    private static final Logger LOG = LoggerFactory.getLogger(TerritoryOwningRewardsTable.class);

    private static final String REWARDS_FILE_PATH = "./territory_owning_rewards.yml";

    public List<TerritoryOwningReward> territoryOwningRewards = Collections.emptyList();

    public void load() {
        try {
            territoryOwningRewards = ObjectMapperYamlSingleton.getInstance().readValue(getClass().getResourceAsStream(REWARDS_FILE_PATH), new TypeReference<List<TerritoryOwningReward>>() {
            });
            LOG.info("[{}] {} reward entries loaded!", TerritoryOwningRewardsManager.LOG_TAG, territoryOwningRewards.size());
        } catch (IOException e) {
            String message = "Could not read rewards file from '" + REWARDS_FILE_PATH + "', please check that file exists and is a valid YML";
            LOG.error("[{}]" + message + " error: {}", TerritoryOwningRewardsManager.LOG_TAG, e.getMessage());
            throw new IllegalStateException(message);
        }
    }

    public Optional<TerritoryOwningReward> findResidenceReward(int residenceId) {
        return territoryOwningRewards.stream().filter(territoryOwningReward -> territoryOwningReward.getResidenceId() == residenceId).findFirst();
    }

}
