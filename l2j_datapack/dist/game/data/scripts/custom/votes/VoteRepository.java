package custom.votes;

import com.google.common.base.Functions;
import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import org.jdbi.v3.core.Jdbi;

import java.util.Map;
import java.util.stream.Collectors;

public class VoteRepository {

    private static final String CREATE_TABLE_IF_NOT_EXIST = "CREATE TABLE votes (id NOT NULL AUTOINCREMENT, character_id NOT NULL, source_type NOT_NULL VARCHAR(64), timestamp NOT NULL date)";
    private static final String SELECT_LATEST_VOTES_BY_SOURCE = "SELECT source_type, timestamp FROM votes GROUP BY source_type ORDER BY source_type, timestamp HAVING character_id=:character_id";

    private Jdbi jdbi;

    public VoteRepository() {
        this.jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
    }

    public boolean createTableIfNotExist() {
        return jdbi.withHandle(h -> h.execute(CREATE_TABLE_IF_NOT_EXIST)) > 0;
    }

    public Map<String, VoteEntry> findAllLatestVotesBySource(int playerObjectId) {
        return jdbi.withHandle(h -> h.createQuery(SELECT_LATEST_VOTES_BY_SOURCE)
                .bind("character_id", playerObjectId)
                .mapTo(VoteEntry.class)
                .stream()
                .collect(Collectors.toMap(VoteEntry::getSourceType, Functions.identity())));
    }
}
