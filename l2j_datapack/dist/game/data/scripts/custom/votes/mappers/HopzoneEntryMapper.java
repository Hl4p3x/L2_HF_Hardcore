package custom.votes.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.l2jserver.common.Log;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import custom.votes.VoteEntry;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class HopzoneEntryMapper implements VoteEntryMapper {

    private static final Log LOG = new Log("[VoteManager]", HopzoneEntryMapper.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private String sourceCode;

    public HopzoneEntryMapper(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public List<VoteEntry> convert(L2PcInstance player, String contents) {
        try {
            HopzoneVote hopzoneVote = objectMapper.readValue(contents, HopzoneVote.class);
            if (hopzoneVote.isVoted()) {
                return Collections.singletonList(new VoteEntry(sourceCode, player.getName(), hopzoneVote.getVoteTime()));
            } else {
                return Collections.emptyList();
            }
        } catch (IOException e) {
            LOG.error("Could not retrieve vote for {} from {} because of: {}", player, sourceCode, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

}
