package com.l2jserver.gameserver.data.sql.impl;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommunityBuffListMapper implements RowMapper<CommunityBuffList> {

    @Override
    public CommunityBuffList map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new CommunityBuffList(
                rs.getInt("list_id"),
                rs.getInt("owner_id"),
                rs.getString("list_name"));
    }

}
