package custom.clan.territory;

import ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.model.entity.interfaces.Residence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TerritoryOwningRewardsManager extends AbstractNpcAI {

    private static final Logger LOG = LoggerFactory.getLogger(TerritoryOwningRewardsManager.class);

    public static final String LOG_TAG = "[TerritoryOwningRewardsManager]";

    private static final LocalTime DISTRIBUTION_TIME = LocalTime.parse("08:00:00"); // 24H format

    private final TerritoryOwningRewardsTable territoryOwningRewardsTable = new TerritoryOwningRewardsTable();

    private ScheduledFuture<?> taskHandle;

    private TerritoryOwningRewardsManager() {
        super(TerritoryOwningRewardsManager.class.getSimpleName(), "custom/clan/territory");
        territoryOwningRewardsTable.load();
        scheduleRewardTask();
        LOG.info("{} Loaded!", LOG_TAG);
    }

    public void scheduleRewardTask() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime currentTime = currentDateTime.toLocalTime();

        LocalDateTime expectedExecutionDateTime;
        if (currentTime.isBefore(DISTRIBUTION_TIME)) {
            expectedExecutionDateTime = DISTRIBUTION_TIME.atDate(currentDateTime.toLocalDate());
        } else {
            expectedExecutionDateTime = DISTRIBUTION_TIME.atDate(currentDateTime.toLocalDate()).plusDays(1L);
        }

        long expectedTimeMillis = expectedExecutionDateTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
        long currentTimeMillis = currentDateTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
        final long delay = expectedTimeMillis - currentTimeMillis;

        taskHandle = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::distributeRewards, delay, GameTimeController.FULL_DAY_IN_MILLIS, TimeUnit.MILLISECONDS);

        LOG.info("{} Next reward distribution will be at {} [{} millis]", LOG_TAG, expectedExecutionDateTime, delay);
    }

    public static <T extends Residence> List<Residence> onlyOwnedByClan(List<T> residences) {
        return residences.stream().filter(residence -> residence.getOwnerClan() != null).collect(Collectors.toList());
    }

    public void distributeRewards() {
        LOG.info("{} Reward distribution started!", LOG_TAG);

        List<Residence> castles = onlyOwnedByClan(CastleManager.getInstance().getCastles());
        List<Residence> forts = onlyOwnedByClan(FortManager.getInstance().getForts());

        distributeRewardsByResidences(castles, GameTimeController.FULL_WEEK_IN_MILLIS);
        distributeRewardsByResidences(forts, GameTimeController.FULL_DAY_IN_MILLIS);
    }

    public long calculateMultiplier(Residence residence, long rewardMultiplierTimeDistance) {
        Instant lastOwnershipChangeTime = Instant.ofEpochMilli(residence.getLastOwnershipChangeTime());
        return Duration.between(lastOwnershipChangeTime, Instant.now()).toMillis() / rewardMultiplierTimeDistance;
    }

    public void distributeRewardsByResidences(List<Residence> residences, long rewardMultiplierTimeDistance) {
        for (Residence residence : residences) {
            Optional<TerritoryOwningReward> territoryOwningRewardOptional = territoryOwningRewardsTable.findResidenceReward(residence.getResidenceId());
            if (!territoryOwningRewardOptional.isPresent()) {
                LOG.warn("Residence {} [{}] has no Territory Owning Reward data", residence.getName(), residence.getResidenceId());
                continue;
            }

            long countMultiplier = calculateMultiplier(residence, rewardMultiplierTimeDistance);
            territoryOwningRewardOptional.get().getRewards().forEach(
                    reward -> residence.getOwnerClan().getWarehouse().addItem("Territory Owning Reward", reward.getId(), reward.getCount() * countMultiplier, null, null)
            );
        }
    }

    public static void main(String[] args) {
        new TerritoryOwningRewardsManager();
    }

    @Override
    public boolean reload() {
        boolean result = super.reload();
        if (taskHandle != null) {
            taskHandle.cancel(false);
        }
        territoryOwningRewardsTable.load();
        scheduleRewardTask();
        return result;
    }

    @Override
    public boolean unload() {
        if (taskHandle != null) {
            taskHandle.cancel(false);
        }
        return super.unload();
    }

}
