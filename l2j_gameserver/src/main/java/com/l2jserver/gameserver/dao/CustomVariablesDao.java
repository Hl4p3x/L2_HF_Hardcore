package com.l2jserver.gameserver.dao;

import com.l2jserver.common.pool.impl.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomVariablesDao {

    private static final Logger LOG = LoggerFactory.getLogger(CustomVariablesDao.class);

    private static final String SELECT_VARIABLE = "SELECT value FROM custom_variables WHERE var = ?";
    private static final String CREATE_VARIABLE = "INSERT_INTO custom_variables (var, value) VALUES (?, ?)";
    private static final String UPDATE_VARIABLE = "UPDATE custom_variables SET value=? WHERE var = ?";

    public Optional<String> findVariable(String variableName) {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement selectVariable = con.prepareStatement(SELECT_VARIABLE);
        ) {
            selectVariable.setString(1, variableName);
            try (ResultSet resultSet = selectVariable.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getString("value"));
                } else {
                    return Optional.empty();
                }
            } catch (SQLException e) {
                LOG.error("Could not query for a variable {}", variableName, e);
                throw new IllegalStateException(e);
            }
        } catch (Exception e) {
            LOG.error("Could not prepare variable query", e);
            throw new IllegalStateException(e);
        }
    }

    public boolean createVariable(String variableName, String value) {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement selectVariable = con.prepareStatement(CREATE_VARIABLE);
        ) {
            selectVariable.setString(1, variableName);
            selectVariable.setString(2, value);
            int updatedRows = selectVariable.executeUpdate();
            return updatedRows > 0;
        } catch (Exception e) {
            LOG.error("Could not create variable {} with value {}", variableName, value, e);
            throw new IllegalStateException(e);
        }
    }

    public boolean updateVariable(String variableName, String value) {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement selectVariable = con.prepareStatement(UPDATE_VARIABLE);
        ) {
            selectVariable.setString(1, value);
            selectVariable.setString(2, variableName);
            int updatedRows = selectVariable.executeUpdate();
            return updatedRows > 0;
        } catch (Exception e) {
            LOG.error("Could not update variable {} with value {}", variableName, value, e);
            throw new IllegalStateException(e);
        }
    }

}
