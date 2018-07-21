package custom.clan;

import ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.instancemanager.MailManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.entity.Message;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.player.clan.OnPlayerClanJoin;
import com.l2jserver.gameserver.model.events.impl.character.player.clan.OnPlayerClanLvlUp;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import custom.Reward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FirstClanBonus extends AbstractNpcAI {

    private static final Logger LOG = LoggerFactory.getLogger(FirstClanBonus.class);
    private static final String LOG_TAG = "[First Clans Bonus Manager]";

    private static final int REQUIRED_MEMBERS_COUNT = 8;
    private static final int REQUIRED_CLAN_LEVEL = 3;

    private static final int MAX_CLANS_TO_EARN_BONUSES = 10;

    private static final String CLAN_COUNTER_VARIABLE_NAME = "EARNED_BONUS_CLAN_COUNTER";

    private static final List<Reward> REWARDS = Arrays.asList(
            // Mithril Heavy Set
            new Reward(58, 1),
            new Reward(59, 1),
            new Reward(47, 1),
            new Reward(628, 1),

            // Brigantine Set
            new Reward(352, 1),
            new Reward(2378, 1),
            new Reward(2411, 1),
            new Reward(2493, 1),

            // Reinforced Leather Set
            new Reward(394, 1),
            new Reward(416, 1),
            new Reward(2422, 1),

            // Manticore Skin Set
            new Reward(395, 1),
            new Reward(417, 1),
            new Reward(2424, 1),

            // Knowledge Set
            new Reward(436, 1),
            new Reward(469, 1),
            new Reward(2447, 1),

            // Mithril Robe Set
            new Reward(437, 1),
            new Reward(470, 1),
            new Reward(2450, 1),

            new Reward(128, 1), // Knight's Sword
            new Reward(124, 1), // Two-Handed Sword
            new Reward(221, 1), // Assassin Knife
            new Reward(276, 1), // Elven Bow
            new Reward(277, 1), // Dark Elven Bow
            new Reward(259, 1), // Single-Edged Jamadhr
            new Reward(292, 1), // Pike
            new Reward(180, 1), // Mace of Judgment
            new Reward(186, 1), // Staff of Magic

            new Reward(1458, 2000), // Crystal (D-Grade)
            new Reward(1459, 1000), // Crystal (C-Grade)
            new Reward(1460, 500), // Crystal (B-Grade)

            new Reward(847, 6), // Red Crescent Earring
            new Reward(910, 3), // Necklace of Devotion
            new Reward(890, 6) // Ring of Devotion
    );

    private FirstClanBonus() {
        super(FirstClanBonus.class.getSimpleName(), "custom/clan");

        Optional<String> clansEarnedBonusCounter = DAOFactory.getInstance().getCustomVariablesDao().findVariable(CLAN_COUNTER_VARIABLE_NAME);
        if (!clansEarnedBonusCounter.isPresent()) {
            LOG.info("{} There was no clan bonuses variable, creating one", LOG_TAG);
            DAOFactory.getInstance().getCustomVariablesDao().createVariable(CLAN_COUNTER_VARIABLE_NAME, "0");
        }

        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_CLAN_JOIN, (Consumer<OnPlayerClanJoin>) this::onPlayerClanJoin, this));
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_CLAN_LVLUP, (Consumer<OnPlayerClanLvlUp>) this::onPlayerClanLevelUp, this));
        LOG.info("{} Loaded!", LOG_TAG);
    }

    private void onPlayerClanLevelUp(OnPlayerClanLvlUp event) {
        handleClanReward(event.getClan());
    }

    private void onPlayerClanJoin(OnPlayerClanJoin event) {
        handleClanReward(event.getClan());
    }

    private synchronized void handleClanReward(L2Clan clan) {
        if (clan == null) {
            LOG.warn("{} Skipping clan bonus check, clan was null", LOG_TAG);
            return;
        }

        //TODO check clan already received a reward
        if (clan.getMembersCount() >= REQUIRED_MEMBERS_COUNT && clan.getLevel() >= REQUIRED_CLAN_LEVEL) {
            Optional<String> clanBonusCounter = DAOFactory.getInstance().getCustomVariablesDao().findVariable(CLAN_COUNTER_VARIABLE_NAME);
            if (clanBonusCounter.isPresent()) {
                int clanCounter = parseCounter(clanBonusCounter.get());
                if (clanCounter <= MAX_CLANS_TO_EARN_BONUSES) {
                    DAOFactory.getInstance().getCustomVariablesDao().updateVariable(CLAN_COUNTER_VARIABLE_NAME, String.valueOf(clanCounter + 1));
                    REWARDS.forEach(reward ->
                            clan.getWarehouse().addItem("ClanBonus", reward.getItemId(), reward.getAmount(), null, null)
                    );

                    Message rewardMessage = new Message(clan.getLeaderId(),
                            "Clan Reward",
                            "Your clan is one of the top 10 clans on server! Check your clan warehouse for rewards!",
                            Message.SendBySystem.NONE);
                    MailManager.getInstance().sendMessage(rewardMessage);
                }
            } else {
                LOG.warn("{} Could not find clan bonuses counter", LOG_TAG);
            }
        }
    }

    private int parseCounter(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOG.warn("{} Invalid counter value, using max clans earned limit as counter to disable bonus awards", LOG_TAG);
            return MAX_CLANS_TO_EARN_BONUSES;
        }
    }

    public static void main(String[] args) {
        new FirstClanBonus();
    }

}
