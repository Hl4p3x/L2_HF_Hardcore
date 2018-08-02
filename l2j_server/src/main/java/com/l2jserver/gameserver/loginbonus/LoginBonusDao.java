package com.l2jserver.gameserver.loginbonus;

import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginBonusDao {

    private static final Logger LOG = LoggerFactory.getLogger(LoginBonusDao.class);

    public List<LoginBonusRecord> findPlayerBonusRecords(int playerId, LoginBonusType bonusType) {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement findBonusStatement = con.prepareStatement("SELECT * FROM login_bonuses WHERE bonus_owner=? AND bonus_type=?")) {
            findBonusStatement.setInt(1, playerId);
            findBonusStatement.setString(2, bonusType.toString());
            List<LoginBonusRecord> results = new ArrayList<>();
            try (ResultSet resultSet = findBonusStatement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(convert(resultSet));
                }
            }
            return results;
        } catch(Exception e){
            LOG.error("Could not find query bonus type for {} of type {}", playerId, bonusType, e);
            throw new IllegalStateException(e);
        }
    }

    public boolean deleteLoginBonusRecords(int playerId, LoginBonusType bonusType) {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement findBonusStatement = con.prepareStatement("DELETE FROM login_bonuses WHERE bonus_owner=? AND bonus_type=?")) {
            findBonusStatement.setInt(1, playerId);
            findBonusStatement.setString(2, bonusType.toString());
            int updatedRows = findBonusStatement.executeUpdate();
            return updatedRows >= 1;
        } catch(Exception e){
            LOG.error("Could not update bonus type for {} of type {}", playerId, bonusType, e);
            throw new IllegalStateException(e);
        }
    }

    public boolean createLoginBonusRecordNow(int playerId, LoginBonusType bonusType) {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement findBonusStatement = con.prepareStatement("INSERT INTO login_bonuses (bonus_owner, bonus_type, last_bonus_time) VALUES (?, ?, ?)")) {
            findBonusStatement.setInt(1, playerId);
            findBonusStatement.setString(2, bonusType.toString());
            findBonusStatement.setLong(3, System.currentTimeMillis());
            int updatedRows = findBonusStatement.executeUpdate();
            return updatedRows >= 1;
        } catch(Exception e){
            LOG.error("Could not create bonus type for {} of type {}", playerId, bonusType, e);
            throw new IllegalStateException(e);
        }
    }

    public LoginBonusRecord convert(ResultSet resultSet) {
        try {
            return new LoginBonusRecord(
                    resultSet.getInt("bonus_owner"),
                    LoginBonusType.valueOf(resultSet.getString("bonus_type")),
                    resultSet.getLong("last_bonus_time"));
        } catch (SQLException e) {
            LOG.error("Could not convert result set to login bonus", e);
            throw new IllegalStateException(e);
        }
    }

}
