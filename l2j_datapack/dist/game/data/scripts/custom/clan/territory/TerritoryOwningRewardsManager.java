package custom.clan.territory;

import ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.instancemanager.MailManager;
import com.l2jserver.gameserver.model.entity.Message;
import com.l2jserver.gameserver.model.entity.interfaces.Residence;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.localization.Language;
import com.l2jserver.localization.Strings;
import com.l2jserver.localization.StringsTable;
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
        LOG.info("{} Reward distribution!", LOG_TAG);

        List<Residence> castles = onlyOwnedByClan(CastleManager.getInstance().getCastles());
        List<Residence> forts = onlyOwnedByClan(FortManager.getInstance().getForts());

        distributeRewardsByResidences(castles, GameTimeController.FULL_WEEK_IN_MILLIS);
        distributeRewardsByResidences(forts, GameTimeController.FULL_DAY_IN_MILLIS);
    }

    public long calculateMultiplier(Residence residence, long rewardMultiplierTimeDistance) {
        Instant lastOwnershipChangeTime = Instant.ofEpochMilli(residence.getLastOwnershipChangeTime());
        long multiplier = Math.max(Duration.between(lastOwnershipChangeTime, Instant.now()).toMillis() / rewardMultiplierTimeDistance, 3);
        if (multiplier < 1) {
            return 1L;
        } else {
            return multiplier;
        }
    }

    public void distributeRewardsByResidences(List<Residence> residences, long rewardMultiplierTimeDistance) {
        for (Residence residence : residences) {
            Optional<TerritoryOwningReward> territoryOwningRewardOptional = territoryOwningRewardsTable.findResidenceReward(residence.getResidenceId());
            if (territoryOwningRewardOptional.isEmpty()) {
                LOG.warn("Residence {} [{}] has no Territory Owning Reward data", residence.getName(), residence.getResidenceId());
                continue;
            }

            List<ItemHolder> rewards = territoryOwningRewardOptional.get().getRewards();

            long countMultiplier = calculateMultiplier(residence, rewardMultiplierTimeDistance);
            rewards.forEach(
                    reward -> residence.getOwnerClan().getWarehouse().addItem("Territory Owning Reward", reward.getId(), reward.getCount() * countMultiplier, null, null)
            );

            String rewardsList = rewards.stream().map(itemInstance -> ItemTable.getInstance().getTemplate(itemInstance.getId()).getName() + " [" + itemInstance.getCount() + "]").collect(Collectors.joining(", \n"));

            int leaderId = residence.getOwnerClan().getLeader().getObjectId();
            Language leaderLanguage = DAOFactory.getInstance().getPlayerDAO().loadLanguageByCharacterId(leaderId);

            StringsTable stringsTable = Strings.lang(leaderLanguage);

            Message rewardMessage = new Message(leaderId,
                    stringsTable.get("territory_owning_reward") + '!',
                    stringsTable.get("territory_owning_reward_message") + ":\n" + rewardsList,
                    Message.SendBySystem.NONE);
            MailManager.getInstance().sendMessage(rewardMessage);
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
