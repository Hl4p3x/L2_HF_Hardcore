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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DailyClanLoginBonus extends AbstractNpcAI {

    private static final Logger LOG = LoggerFactory.getLogger(DailyClanLoginBonus.class);

    private final LoginBonusDao loginBonusDao;

    private static final long MINUTE_IN_MS = 60000;
    private static final long HOUR_IN_MS = MINUTE_IN_MS * 60;
    private static final long DAY_IN_MS = HOUR_IN_MS * 24;
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
                new Reward(6036, 5), // Greater Magic Haste potion

                new Reward(1465, 1200), // Soulshot B Grade
                new Reward(3950, 1200) // Blessed Spiritshot B Grade
        ));

        results.put(3, Arrays.asList(
                new Reward(1466, 1000), // Soulshot A Grade
                new Reward(3951, 1000) // Blessed Spiritshot A Grade
        ));

        results.put(4, Arrays.asList(
                new Reward(1467, 800), // Soulshot S Grade
                new Reward(3952, 800) // Blessed Spiritshot S Grade
        ));

        return results;
    }

    private static final Map<Integer, List<ChancedReward>> CHANCED_REWARDS_MAP = createChancedDailyRewards();

    private static Map<Integer, List<ChancedReward>> createChancedDailyRewards() {
        Map<Integer, List<ChancedReward>> results = new HashMap<>();

        results.put(2, Arrays.asList(
                new ChancedReward(new Reward(947), 20), // Scroll: Enchant Weapon (B-Grade)
                new ChancedReward(new Reward(948, 2), 30) // Scroll: Enchant Armor (B-Grade)
        ));

        results.put(3, Arrays.asList(
                new ChancedReward(new Reward(729), 15), // Scroll: Enchant Weapon (A-Grade)
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

    private static final List<Reward> SET_REWARDS = Arrays.asList(
            new Reward(357), // Zubei's Breastplate
            new Reward(503), // Zubei's Helmet
            new Reward(5710), // Zubei's Gauntlets - Heavy Armor
            new Reward(383), // Zubei's Gaiters
            new Reward(5726), // Zubei's Boots - Heavy Armor

            new Reward(2376), // Avadon Breastplate
            new Reward(2415), // Avadon Circlet
            new Reward(5714), // Avadon Gloves - Heavy Armor
            new Reward(2379), // Avadon Gaiters
            new Reward(5730), // Avadon Boots - Heavy Armor
            new Reward(673), // Avadon Shield

            new Reward(2384), // Zubei's Leather Shirt
            new Reward(503), // Zubei's Helmet
            new Reward(5711), // Zubei's Gauntlets - Light Armor
            new Reward(2388), // Zubei's Leather Gaiters
            new Reward(5727), // Zubei's Boots - Light Armor

            new Reward(2390), // Avadon Leather Armor
            new Reward(2415), // Avadon Circlet
            new Reward(5715), // Avadon Gloves - Light Armor
            new Reward(5731), // Avadon Boots - Light Armor

            new Reward(2397), // Tunic of Zubei
            new Reward(503), // Zubei's Helmet
            new Reward(5712), // Zubei's Gauntlets - Robe
            new Reward(2402), // Stockings of Zubei
            new Reward(5728), // Zubei's Boots - Robe

            new Reward(2406), // Avadon Robe
            new Reward(2415), // Avadon Circlet
            new Reward(5716), // Avadon Gloves - Robe
            new Reward(5732), // Avadon Boots - Robe

            new Reward(358), // Blue Wolf Breastplate
            new Reward(2416), // Blue Wolf Helmet
            new Reward(5718), // Blue Wolf Gloves - Heavy Armor
            new Reward(2380), // Blue Wolf Gaiters
            new Reward(5734), // Blue Wolf Boots - Heavy Armor

            new Reward(2381), // Doom Plate Armor
            new Reward(2417), // Doom Helmet
            new Reward(5722), // Doom Gloves - Heavy Armor
            new Reward(5738), // Doom Boots - Heavy Armor
            new Reward(110), // Doom Shield

            new Reward(2391), // Blue Wolf Leather Armor
            new Reward(2416), // Blue Wolf Helmet
            new Reward(5719), // Blue Wolf Gloves - Light Armor
            new Reward(5735), // Blue Wolf Boots - Light Armor

            new Reward(2392), // Leather Armor of Doom
            new Reward(2417), // Doom Helmet
            new Reward(5723), // Doom Gloves - Light Armor
            new Reward(5739), // Doom Boots - Light Armor

            new Reward(2398), // Blue Wolf Tunic
            new Reward(2416), // Blue Wolf Helmet
            new Reward(5720), // Blue Wolf Gloves - Robe
            new Reward(2403), // Blue Wolf Stockings
            new Reward(5736), // Blue Wolf Boots - Robe

            new Reward(2399), // Tunic of Doom
            new Reward(2417), // Doom Helmet
            new Reward(5724), // Doom Gloves - Robe
            new Reward(2404), // Stockings of Doom
            new Reward(5740), // Doom Boots - Robe

            new Reward(365), // Dark Crystal Breastplate
            new Reward(512), // Dark Crystal Helmet
            new Reward(5765), // Dark Crystal Gloves - Heavy Armor
            new Reward(388), // Dark Crystal Gaiters
            new Reward(5777), // Dark Crystal Boots - Heavy Armor
            new Reward(641), // Dark Crystal Shield

            new Reward(2382), // Tallum Plate Armor
            new Reward(547), // Tallum Helm
            new Reward(5768), // Tallum Gloves - Heavy Armor
            new Reward(5780), // Tallum Boots - Heavy Armor

            new Reward(2385), // Dark Crystal Leather Armor
            new Reward(512), // Dark Crystal Helmet
            new Reward(5766), // Dark Crystal Gloves - Light Armor
            new Reward(2389), // Dark Crystal Leggings
            new Reward(5778), // Dark Crystal Boots - Light Armor

            new Reward(2393), // Tallum Leather Armor
            new Reward(547), // Tallum Helm
            new Reward(5769), // Tallum Gloves - Light Armor
            new Reward(5781), // Tallum Boots - Light Armor

            new Reward(2400), // Tallum Tunic
            new Reward(547), // Tallum Helm
            new Reward(5770), // Tallum Gloves - Robe
            new Reward(2405), // Tallum Stockings
            new Reward(5782), // Tallum Boots - Robe

            new Reward(2407), // Dark Crystal Robe
            new Reward(512), // Dark Crystal Helmet
            new Reward(5767), // Dark Crystal Gloves - Robe
            new Reward(5779), // Dark Crystal Boots - Robe

            new Reward(374), // Armor of Nightmare
            new Reward(2418), // Helm of Nightmare
            new Reward(5771), // Gauntlets of Nightmare - Heavy Armor
            new Reward(5783), // Boots of Nightmare - Heavy Armor
            new Reward(2498), // Shield of Nightmare

            new Reward(2383), // Majestic Plate Armor
            new Reward(2419), // Majestic Circlet
            new Reward(5774), // Majestic Gauntlets - Heavy Armor
            new Reward(5786), // Majestic Boots - Heavy Armor

            new Reward(2394), // Leather Armor of Nightmare
            new Reward(2418), // Helm of Nightmare
            new Reward(5772), // Gauntlets of Nightmare - Light Armor
            new Reward(5784), // Boots of Nightmare - Light Armor

            new Reward(2395), // Majestic Leather Armor
            new Reward(2419), // Majestic Circlet
            new Reward(5775), // Majestic Gauntlets - Light Armor
            new Reward(5787), // Majestic Boots - Light Armor

            new Reward(2408), // Robe of Nightmare
            new Reward(2418), // Helm of Nightmare
            new Reward(5773), // Gauntlets of Nightmare - Robe
            new Reward(5785), // Boots of Nightmare - Robe

            new Reward(2409), // Majestic Robe
            new Reward(2419), // Majestic Circlet
            new Reward(5776), // Majestic Gauntlets - Robe
            new Reward(5788), // Majestic Boots - Robe

            new Reward(6373), // Imperial Crusader Breastplate
            new Reward(6378), // Imperial Crusader Helmet
            new Reward(6375), // Imperial Crusader Gauntlets
            new Reward(6374), // Imperial Crusader Gaiters
            new Reward(6376), // Imperial Crusader Boots
            new Reward(6377), // Imperial Crusader Shield

            new Reward(6379), // Draconic Leather Armor
            new Reward(6382), // Draconic Leather Helmet
            new Reward(6380), // Draconic Leather Gloves
            new Reward(6381), // Draconic Leather Boots

            new Reward(6383), // Major Arcana Robe
            new Reward(6386), // Major Arcana Circlet
            new Reward(6384), // Major Arcana Gloves
            new Reward(6385), // Major Arcana Boots

            new Reward(9416), // Dynasty Breast Plate
            new Reward(9422), // Dynasty Helmet
            new Reward(9423), // Dynasty Gauntlet - Heavy Armor
            new Reward(9421), // Dynasty Gaiters
            new Reward(9424), // Dynasty Boots - Heavy Armor
            new Reward(9441), // Dynasty Shield

            new Reward(9425), // Dynasty Leather Armor
            new Reward(9429), // Dynasty Leather Helmet
            new Reward(9430), // Dynasty Leather Gloves - Light Armor
            new Reward(9428), // Dynasty Leather Leggings
            new Reward(9431), // Dynasty Leather Boots - Light Armor

            new Reward(9432), // Dynasty Tunic
            new Reward(9438), // Dynasty Circlet
            new Reward(9439), // Dynasty Gloves - Robe
            new Reward(9437), // Dynasty Stockings
            new Reward(9440) // Dynasty Shoes - Robe
    );

    private DailyClanLoginBonus() {
        super(DailyClanLoginBonus.class.getSimpleName(), "custom/daily");
        loginBonusDao = DAOFactory.getInstance().getLoginBonusDao();
        setOnEnterWorld(Config.DAILY_CLAN_LOGIN_BONUS);
        if (Config.DAILY_CLAN_LOGIN_BONUS) {
            LOG.info("[Daily Clan Member Login Bonuses] loaded!");
        }
    }

    @Override
    public String onEnterWorld(L2PcInstance player) {
        if (player.getClan() == null || player.getClan().getLevel() < 3) {
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
                // Registering this login
                handleReward(player, 0);
                return super.onEnterWorld(player);
            }

            if (timeSinceLastBonus > DAY_IN_MS) {
                int consecutiveLogins = sortedPlayerBonuses.size();
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
        LOG.debug("Trying to reward {} {} time", player, consecutiveLogins);

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

        if (consecutiveLogins == 3 && (Rnd.get(100) + Rnd.get()) < 15) {
            Reward reward = RndCollection.random(ACCESSORY_REWARDS);
            attachments.addItem("ClanLoginBonus", reward.getItemId(), reward.getAmount(), null, null);
        }

        if (consecutiveLogins == 4 && (Rnd.get(100) + Rnd.get()) < 10) {
            Reward reward = RndCollection.random(SET_REWARDS);
            attachments.addItem("ClanLoginBonus", reward.getItemId(), reward.getAmount(), null, null);
        }

        if (attachments.getSize() > 0) {
            MailManager.getInstance().sendMessage(rewardMessage);
            LOG.debug("Player {} earned daily login bonus {}", player, attachments.getItems());
        } else {
            LOG.debug("Player {} could not earn login bonus, no attachments specified", player);
        }
    }

    public static void main(String[] args) {
        new DailyClanLoginBonus();
    }

}
