package custom.votes;

import com.l2jserver.common.Log;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import custom.votes.mappers.HopzoneEntryMapper;
import custom.votes.mappers.L2TopVoteEntryMapper;
import custom.votes.mappers.MmorpgTopRuEntryMapper;
import custom.votes.mappers.VoteEntryMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VoteChecker {

    private static final Log LOG = new Log("[VoteManager]", VoteChecker.class);

    private List<VoteSource> voteSources;
    private VoteRepository voteRepository;
    private Map<String, VoteEntryMapper> voteMappers = Stream.of(
            new L2TopVoteEntryMapper("l2top_ru"),
            new HopzoneEntryMapper("hopzone"),
            new MmorpgTopRuEntryMapper("mmo_top_ru")
    ).collect(Collectors.toMap(VoteEntryMapper::getSourceCode, Function.identity()));

    private VoteRetriever voteRetriever = new VoteRetriever();

    public VoteChecker(VoteRepository voteRepository, List<VoteSource> voteSources) {
        this.voteSources = voteSources;
        this.voteRepository = voteRepository;
    }

    public List<VoteEntry> checkNewVotes(L2PcInstance player) {
        Map<String, VoteEntry> latestVotes = voteRepository.findAllLatestVotesBySource(player.getObjectId());
        List<VoteEntry> results = new ArrayList<>();
        for (VoteSource voteSource : voteSources) {
            VoteEntryMapper mapper = voteMappers.get(voteSource.getCode());
            if (mapper == null) {
                LOG.warn("Skipping vote source {} because it has no matching mappers", voteSource);
                continue;
            }

            VoteEntry latestKnownVoteEntry = latestVotes.get(voteSource.getCode());
            LocalDateTime latestVoteTime = latestKnownVoteEntry != null ? latestKnownVoteEntry.getTimestamp() : LocalDateTime.MIN;

            List<VoteEntry> sourceVotes;
            try {
                sourceVotes = voteRetriever.retrieveVotes(voteSource.getUrl(), player, mapper);
            } catch (RuntimeException e) {
                LOG.error("Failed to retrieve votes for {} from {} because of: {}", player, voteSource, e.getMessage());
                continue;
            }

            List<VoteEntry> newVotes = sourceVotes.stream().filter(sourceVote -> sourceVote.getTimestamp().isAfter(latestVoteTime)).collect(Collectors.toList());
            results.addAll(newVotes);
        }
        return results;
    }

}
