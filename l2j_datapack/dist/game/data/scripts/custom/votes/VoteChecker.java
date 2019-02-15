package custom.votes;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoteChecker {

    private List<VoteSource> voteSources;
    private VoteRepository voteRepository;

    public VoteChecker(VoteRepository voteRepository, List<VoteSource> voteSources) {
        this.voteSources = voteSources;
        this.voteRepository = voteRepository;
    }

    public List<VoteEntry> checkNewVotes(L2PcInstance player) {
        Map<String, VoteEntry> latestVotes = voteRepository.findAllLatestVotesBySource(player.getObjectId());
        for (VoteSource voteSource : voteSources) {
            VoteEntry latestKnownVoteEntry = latestVotes.get(voteSource.getName());
            LocalDateTime latestVoteTime = latestKnownVoteEntry != null ? latestKnownVoteEntry.getTimestamp() : LocalDateTime.MIN;
            VoteEntry latestSourceVoteEntry = voteSource.checkNewVote(player);

        }
        return new ArrayList<>();
    }

}
