package custom.votes;

import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteEntryJdbiMapper implements RowMapper<VoteEntry> {

    @Override
    public VoteEntry map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new VoteEntry(rs.getString("source_code"), CharNameTable.getInstance().getNameById(rs.getInt("character_id")), rs.getTimestamp("vote_timestamp").toLocalDateTime());
    }

}
