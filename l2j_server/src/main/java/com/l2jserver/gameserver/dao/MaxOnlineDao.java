package com.l2jserver.gameserver.dao;

import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaxOnlineDao {

    private static final Logger LOG = LoggerFactory.getLogger(MaxOnlineDao.class);

    private static final String SELECT_GREATEST_ONLINE = "SELECT GREATEST((SELECT count(*) FROM characters WHERE online > 0), (SELECT value FROM global_variables WHERE var = 'MaxOnline')) as MAX_ONLINE from DUAL";

    private static final String UPDATE_MAX_ONLINE = "UPDATE global_variable SET value=? WHERE var = 'MaxOnline'";

    public boolean updateMaxOnline() {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
            PreparedStatement selectGreatestOnline = con.prepareStatement(SELECT_GREATEST_ONLINE);
            PreparedStatement updateMaxOnline = con.prepareStatement(UPDATE_MAX_ONLINE);
            ResultSet resultSet = selectGreatestOnline.executeQuery()
        ) {
            if (resultSet.next()) {
                int maxOnline = resultSet.getInt("MAX_ONLINE");
                updateMaxOnline.setInt(1, maxOnline);
                updateMaxOnline.execute();
            } else {
                LOG.warn("Could not query max online value");
            }

        } catch (Exception e) {
            LOG.error("Could not update max online", e);
            return false;
        }
        return true;
    }

}
