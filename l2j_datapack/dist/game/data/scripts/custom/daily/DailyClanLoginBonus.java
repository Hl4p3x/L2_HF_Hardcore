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
import com.l2jserver.util.RndCollection;
import custom.Reward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DailyClanLoginBonus extends AbstractNpcAI {

    private static final Logger LOG = LoggerFactory.getLogger(DailyClanLoginBonus.class);

    private static final long MINUTE_IN_MS = 60000;
    private static final long HOUR_IN_MS = MINUTE_IN_MS * 60;
    private static final long DAY_IN_MS = HOUR_IN_MS * 24;

    private static final List<Reward> REWARDS = Arrays.asList(
            new Reward(1463, 1600), // Soulshot D Grade
            new Reward(1464, 1200), // Soulshot C Grade
            new Reward(1465, 900), // Soulshot B Grade
            new Reward(1466, 700), // Soulshot A Grade
            new Reward(1467, 500), // Soulshot S Grade

            new Reward(3948, 1600), // Blessed Spiritshot D Grade
            new Reward(3949, 1200), // Blessed Spiritshot C Grade
            new Reward(3950, 900), // Blessed Spiritshot B Grade
            new Reward(3951, 700), // Blessed Spiritshot A Grade
            new Reward(3952, 500) // Blessed Spiritshot S Grade
    );

    private DailyClanLoginBonus() {
        super(DailyClanLoginBonus.class.getSimpleName(), "custom/daily");
        setOnEnterWorld(Config.DAILY_CLAN_LOGIN_BONUS);
        if (Config.DAILY_CLAN_LOGIN_BONUS) {
            LOG.info("Daily Clan member login bonuses loaded!");
        }
    }

    @Override
    public String onEnterWorld(L2PcInstance player) {
        if (player.getClan() == null) {
            return super.onEnterWorld(player);
        }

        LoginBonusDao loginBonusDao = DAOFactory.getInstance().getLoginBonusDao();
        Optional<LoginBonusRecord> loginBonusRecord = loginBonusDao.findPlayerBonusRecord(player.getObjectId(), LoginBonusType.CLAN);
        if (loginBonusRecord.isPresent()) {
            long timeSinceLastBonus = System.currentTimeMillis() - loginBonusRecord.get().getLastBonusTimeInMs();
            if (timeSinceLastBonus >= DAY_IN_MS) {
                rewardPlayerWithRandomItem(player);
                boolean updated = loginBonusDao.updateLoginBonusRecordTime(player.getObjectId(), LoginBonusType.CLAN);
                if (!updated) {
                    LOG.warn("Could not update login bonus reward for player {}", player);
                }
            }
        } else {
            rewardPlayerWithRandomItem(player);
            boolean updated = loginBonusDao.createLoginBonusRecordNow(player.getObjectId(), LoginBonusType.CLAN);
            if (!updated) {
                LOG.warn("Could not create login bonus reward for player {}", player);
            }
        }

        return super.onEnterWorld(player);
    }

    private void rewardPlayerWithRandomItem(L2PcInstance player) {
        Reward reward = RndCollection.random(REWARDS);
        Message rewardMessage = new Message(player.getObjectId(), "Clan Daily Login Bonus", "Congratulations with your reward!", Message.SendBySystem.PLAYER);
        Mail attachments = rewardMessage.createAttachments();
        attachments.addItem("ClanLoginBonus", reward.getItemId(), reward.getAmount(), null, null);
        MailManager.getInstance().sendMessage(rewardMessage);
        LOG.debug("Player {} earned daily login bonus {}", player, reward);
    }

    public static void main(String[] args) {
        new DailyClanLoginBonus();
    }

}
