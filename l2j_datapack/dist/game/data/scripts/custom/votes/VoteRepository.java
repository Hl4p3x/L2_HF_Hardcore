package custom.votes;

import com.google.common.base.Functions;
import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import org.jdbi.v3.core.Jdbi;

import java.util.Map;
import java.util.stream.Collectors;

public class VoteRepository {

    private static final String CREATE_TABLE_IF_NOT_EXIST = "CREATE TABLE IF NOT EXISTS votes (id INT NOT NULL AUTO_INCREMENT, character_id INT NOT NULL, source_code VARCHAR(64) NOT NULL, vote_timestamp timestamp NOT NULL, PRIMARY KEY (id), UNIQUE(character_id, source_code, vote_timestamp))";
    private static final String SELECT_LATEST_VOTES_BY_SOURCE = "SELECT source_code, MAX(vote_timestamp) as vote_timestamp, character_id FROM votes GROUP BY source_code, character_id HAVING character_id=:character_id";
    private static final String INSERT_VOTE_ENTRY = "INSERT INTO votes (character_id, source_code, vote_timestamp) VALUES (:character_id, :source_code, :timestamp)";

    private Jdbi jdbi;

    public VoteRepository() {
        this.jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
        jdbi.registerRowMapper(new VoteEntryJdbiMapper());
    }

    public boolean createTableIfNotExist() {
        return jdbi.withHandle(h -> h.execute(CREATE_TABLE_IF_NOT_EXIST)) > 0;
    }

    public Map<String, VoteEntry> findAllLatestVotesBySource(int playerObjectId) {
        return jdbi.withHandle(h -> h.createQuery(SELECT_LATEST_VOTES_BY_SOURCE)
                .bind("character_id", playerObjectId)
                .mapTo(VoteEntry.class)
                .stream()
                .collect(Collectors.toMap(VoteEntry::getSourceCode, Functions.identity())));
    }

    public boolean saveVoteHistory(int playerObjectId, VoteEntry entry) {
        return jdbi.withHandle(h -> h.createUpdate(INSERT_VOTE_ENTRY)
                .bind("character_id", playerObjectId)
                .bind("source_code", entry.getSourceCode())
                .bind("timestamp", entry.getTimestamp()).execute()) > 0;
    }

}
