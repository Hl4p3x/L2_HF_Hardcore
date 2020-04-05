package com.l2jserver.gameserver.data.sql.impl;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class SkillHolderMapper implements RowMapper<SkillHolder> {

    @Override
    public SkillHolder map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new SkillHolder(rs.getInt("skill_id"), rs.getInt("skill_level"));
    }

}
