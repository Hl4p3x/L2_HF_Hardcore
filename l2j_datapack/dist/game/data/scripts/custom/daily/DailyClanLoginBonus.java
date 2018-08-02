package custom.daily;

import ai.npc.AbstractNpcAI;
import com.l2jserver.Config;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.instancemanager.MailManager;
import com.l2jserver.gameserver.loginbonus.LoginBonusDao;
import com.l2jserver.gameserver.loginbonus.LoginBonusRecord;
import com.l2jserver.gameserver.loginbonus.LoginBonusType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Message;
import com.l2jserver.gameserver.model.itemcontainer.Mail;
import com.l2jserver.util.Rnd;
import com.l2jserver.util.RndCollection;
import custom.ChancedReward;
import custom.Reward;
import custom.SetReward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DailyClanLoginBonus extends AbstractNpcAI {

    private static final Logger LOG = LoggerFactory.getLogger(DailyClanLoginBonus.class);

    private final LoginBonusDao loginBonusDao;

    private static final long MINUTE_IN_MS = 60000;
    private static final long HOUR_IN_MS = MINUTE_IN_MS * 60;
    private static final long DAY_IN_MS = MINUTE_IN_MS / 2; //HOUR_IN_MS * 24;
    private static final long TWO_DAYS_IN_MS = DAY_IN_MS * 2;

    private static final Map<Integer, List<Reward>> STATIC_REWARDS_MAP = createStaticDailyRewards();

    private static Map<Integer, List<Reward>> createStaticDailyRewards() {
        Map<Integer, List<Reward>> results = new HashMap<>();
        results.put(1, Arrays.asList(
                new Reward(1061, 50), // Greater healing potion
                new Reward(734, 10), // Haste potion
                new Reward(735, 10), // Alacrity potion
                new Reward(6035, 10) // Magic Haste potion
        ));

        results.put(2, Arrays.asList(
                new Reward(1061, 100), // Greater healing potion
                new Reward(734, 5), // Greater Haste Potion
                new Reward(1375, 5), //  Greater Swift Attack Potion
                new Reward(6036, 5) // Greater Magic Haste potion
        ));

        results.put(3, Arrays.asList(
                new Reward(1466, 700), // Soulshot A Grade
                new Reward(3951, 700) // Blessed Spiritshot A Grade
        ));

        results.put(4, Arrays.asList(
                new Reward(1467, 500), // Soulshot S Grade
                new Reward(3952, 500) // Blessed Spiritshot S Grade
        ));

        return results;
    }

    private static final Map<Integer, List<ChancedReward>> CHANCED_REWARDS_MAP = createChancedDailyRewards();

    private static Map<Integer, List<ChancedReward>> createChancedDailyRewards() {
        Map<Integer, List<ChancedReward>> results = new HashMap<>();

        results.put(2, Arrays.asList(
                new ChancedReward(new Reward(947), 15), // Scroll: Enchant Weapon (B-Grade)
                new ChancedReward(new Reward(948, 2), 30) // Scroll: Enchant Armor (B-Grade)
        ));

        results.put(3, Arrays.asList(
                new ChancedReward(new Reward(729), 12.5), // Scroll: Enchant Weapon (A-Grade)
                new ChancedReward(new Reward(730, 2), 25) // Scroll: Enchant Armor (A-Grade)
        ));

        results.put(4, Arrays.asList(
                new ChancedReward(new Reward(959), 10), // Scroll: Enchant Weapon (S-Grade)
                new ChancedReward(new Reward(960, 2), 20) // Scroll: Enchant Armor (S-Grade)
        ));

        return results;
    }

    private static final List<Reward> ACCESSORY_REWARDS = Arrays.asList(
            new Reward(10176), // White Half Mask
            new Reward(10177), // Black Half Mask
            new Reward(10240), // Bird Nest
            new Reward(7837), // Sayha's White Mask
            new Reward(7839), // Gran Kain's Black Mask
            new Reward(8558) // Eva's Mark (Event)
    );

    private static final List<SetReward> SET_REWARDS = Arrays.asList(
            new SetReward(357), // Zubei's Breastplate
            new SetReward(2376), // Avadon Breastplate
            new SetReward(2384), // Zubei's Leather Shirt
            new SetReward(2390), // Avadon Leather Armor
            new SetReward(2397), // Tunic of Zubei
            new SetReward(2406), // Avadon Robe
            new SetReward(358), // Blue Wolf Breastplate
            new SetReward(2381), // Doom Plate Armor
            new SetReward(2391), // Blue Wolf Leather Armor
            new SetReward(2392), // Leather Armor of Doom
            new SetReward(2398), // Blue Wolf Tunic
            new SetReward(2399), // Tunic of Doom

            new SetReward(365), // Dark Crystal Breastplate
            new SetReward(2382), // Tallum Plate Armor
            new SetReward(2385), // Dark Crystal Leather Armor
            new SetReward(2393), // Tallum Leather Armor
            new SetReward(2400), // Tallum Tunic
            new SetReward(2407), // Dark Crystal Robe
            new SetReward(374), // Armor of Nightmare
            new SetReward(2383), // Majestic Plate Armor
            new SetReward(2394), // Leather Armor of Nightmare
            new SetReward(2395), // Majestic Leather Armor
            new SetReward(2408), // Robe of Nightmare
            new SetReward(2409), // Majestic Robe

            new SetReward(6373), // Imperial Crusader Breastplate
            new SetReward(6379), // Draconic Leather Armor
            new SetReward(6383), // Major Arcana Robe

            new SetReward(9416), // Dynasty Breast Plate
            new SetReward(9425), // Dynasty Leather Armor
            new SetReward(9432) // Dynasty Tunic
    );

    private DailyClanLoginBonus() {
        super(DailyClanLoginBonus.class.getSimpleName(), "custom/daily");
        loginBonusDao = DAOFactory.getInstance().getLoginBonusDao();
        setOnEnterWorld(Config.DAILY_CLAN_LOGIN_BONUS);
        if (Config.DAILY_CLAN_LOGIN_BONUS) {
            LOG.info("[Daily Clan Member ogin Bonuses] loaded!");
        }
    }

    @Override
    public String onEnterWorld(L2PcInstance player) {
        if (player.getClan() == null) {
            return super.onEnterWorld(player);
        }

        List<LoginBonusRecord> loginBonusRecord = loginBonusDao.findPlayerBonusRecords(player.getObjectId(), LoginBonusType.CLAN);

        List<LoginBonusRecord> sortedPlayerBonuses = loginBonusRecord.stream().sorted(Comparator.comparingLong(LoginBonusRecord::getLastBonusTimeInMs).reversed()).collect(Collectors.toList());
        Optional<LoginBonusRecord> headBonus = sortedPlayerBonuses.stream().findFirst();

        if (headBonus.isPresent()) {
            long timeSinceLastBonus = System.currentTimeMillis() - headBonus.get().getLastBonusTimeInMs();
            if (timeSinceLastBonus > TWO_DAYS_IN_MS) {
                LOG.debug("Resetting Login bonuses for {}, too much time passed since last login {}", player, timeSinceLastBonus);
                loginBonusDao.deleteLoginBonusRecords(player.getObjectId(), LoginBonusType.CLAN);
            }

            if (timeSinceLastBonus > DAY_IN_MS) {
                int consecutiveLogins = countPreviousConsecutiveLoginDays(sortedPlayerBonuses);
                if (consecutiveLogins > 4) {
                    LOG.debug("Resetting Login bonuses for {} after hitting {} days in a row", player, consecutiveLogins);
                    loginBonusDao.deleteLoginBonusRecords(player.getObjectId(), LoginBonusType.CLAN);
                } else {
                    handleReward(player, consecutiveLogins);
                }
            }
        } else {
            // First login bonus
            handleReward(player, 0);
        }

        return super.onEnterWorld(player);
    }

    private int countPreviousConsecutiveLoginDays(List<LoginBonusRecord> sortedPreviousBonuses) {
        Iterator<LoginBonusRecord> iterator = sortedPreviousBonuses.iterator();
        if (!iterator.hasNext()) {
            return 0;
        }

        int counter = 0;
        LoginBonusRecord current = iterator.next();
        while (iterator.hasNext()) {
            LoginBonusRecord next = iterator.next();
            long distance = Math.abs(current.getLastBonusTimeInMs() - next.getLastBonusTimeInMs());
            boolean check = DAY_IN_MS < distance && distance < TWO_DAYS_IN_MS;
            LOG.debug("Distance check {} < {} < {} is {}", DAY_IN_MS, distance, TWO_DAYS_IN_MS, check);
            if (!check) {
                break;
            }
            counter++;
            current = next;
        }

        return counter;
    }

    private boolean handleReward(L2PcInstance player, int consecutiveLogins) {
        boolean updated = loginBonusDao.createLoginBonusRecordNow(player.getObjectId(), LoginBonusType.CLAN);
        if (updated) {
            rewardPlayer(player, consecutiveLogins);
        } else {
            LOG.warn("Could not create login bonus reward for player {}", player);
        }
        return updated;
    }

    private void rewardPlayer(L2PcInstance player, int consecutiveLogins) {
        LOG.debug("Trying to reward {} times {}", player, consecutiveLogins);

        Message rewardMessage = new Message(player.getObjectId(), "Clan Daily Login Bonus", "Congratulations with your reward!", Message.SendBySystem.PLAYER);
        Mail attachments = rewardMessage.createAttachments();

        List<Reward> staticRewards = STATIC_REWARDS_MAP.getOrDefault(consecutiveLogins, Collections.emptyList());
        staticRewards.forEach(reward -> attachments.addItem("ClanLoginBonus", reward.getItemId(), reward.getAmount(), null, null));

        List<ChancedReward> chancedRewards = CHANCED_REWARDS_MAP.getOrDefault(consecutiveLogins, Collections.emptyList());
        chancedRewards.forEach(chancedReward -> {
            if ((Rnd.get(100) + Rnd.get()) < chancedReward.getChance()) {
                attachments.addItem(
                        "ClanLoginBonus",
                        chancedReward.getReward().getItemId(),
                        chancedReward.getReward().getAmount(),
                        null, null
                );
            }
        });

        if (consecutiveLogins == 3 && (Rnd.get(100) + Rnd.get()) < 20) {
            Reward reward = RndCollection.random(ACCESSORY_REWARDS);
            attachments.addItem("ClanLoginBonus", reward.getItemId(), reward.getAmount(), null, null);
        }

        if (consecutiveLogins == 4 && (Rnd.get(100) + Rnd.get()) < 10) {
            Reward reward = RndCollection.random(SET_REWARDS.stream().flatMap(set -> set.getSetRewards().stream()).collect(Collectors.toList()));
            attachments.addItem("ClanLoginBonus", reward.getItemId(), reward.getAmount(), null, null);
        }

        if (attachments.getSize() > 0) {
            MailManager.getInstance().sendMessage(rewardMessage);
            LOG.debug("Player {} earned daily login bonus {}", player, attachments);
        } else {
            LOG.debug("Player {} could not earn login bonus, no attachments specified", player);
        }
    }

    public static void main(String[] args) {
        new DailyClanLoginBonus();
    }

}
