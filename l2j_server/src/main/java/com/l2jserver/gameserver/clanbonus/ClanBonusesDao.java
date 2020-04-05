package com.l2jserver.gameserver.clanbonus;

import com.l2jserver.common.database.pool.impl.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClanBonusesDao {

    private static final Logger LOG = LoggerFactory.getLogger(ClanBonusesDao.class);

    private static final String SELECT_CLAN_BONUSES = "SELECT * FROM clan_bonuses WHERE clan_id = ? AND bonus_type = ?";
    private static final String BONUS_COUNT = "SELECT count(*) as total_bonuses FROM clan_bonuses WHERE bonus_type = ?";
    private static final String INSERT_CLAN_BONUS = "INSERT INTO clan_bonuses (clan_id, bonus_type, bonus_time) VALUES (?, ?, ?)";

    public List<ClanBonus> findClanBonuses(int clanId, String bonusType) {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement selectVariable = con.prepareStatement(SELECT_CLAN_BONUSES)
        ) {
            selectVariable.setInt(1, clanId);
            selectVariable.setString(2, bonusType);
            try (ResultSet resultSet = selectVariable.executeQuery()) {
                List<ClanBonus> bonuses = new ArrayList<>();
                while (resultSet.next()) {
                    bonuses.add(
                            new ClanBonus(
                                resultSet.getInt("clan_id"),
                                resultSet.getString("bonus_type"),
                                resultSet.getLong("bonus_time")
                            )
                    );
                }
                return bonuses;
            } catch (SQLException e) {
                LOG.error("Could not query clan bonuses for {} by type {}", clanId, bonusType, e);
                throw new IllegalStateException(e);
            }
        } catch (Exception e) {
            LOG.error("Could not prepare clan bonus query", e);
            throw new IllegalStateException(e);
        }
    }

    public int bonusCount(String bonusType) {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement bonusCount = con.prepareStatement(BONUS_COUNT)
        ) {
            bonusCount.setString(1, bonusType);
            try (ResultSet resultSet = bonusCount.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total_bonuses");
                } else {
                    throw new IllegalStateException("Could not retrieve total clan bonuses count for type " + bonusType);
                }
            } catch (SQLException e) {
                LOG.error("Could not query total clan bonuses count for type {}", bonusType, e);
                throw new IllegalStateException(e);
            }
        } catch (Exception e) {
            LOG.error("Could not prepare bonus count query", e);
            throw new IllegalStateException(e);
        }
    }

    public boolean createClanBonusRecord(int clanId, String bonusType) {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement selectVariable = con.prepareStatement(INSERT_CLAN_BONUS);
        ) {
            selectVariable.setInt(1, clanId);
            selectVariable.setString(2, bonusType);
            selectVariable.setLong(3, System.currentTimeMillis());
            int updatedRows = selectVariable.executeUpdate();
            return updatedRows > 0;
        } catch (Exception e) {
            LOG.error("Could not create clan bonus record for {} with type {}", clanId, bonusType, e);
            throw new IllegalStateException(e);
        }
    }

}
