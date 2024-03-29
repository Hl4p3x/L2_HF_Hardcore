package custom.votes.mappers;

import com.l2jserver.common.Log;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import custom.votes.VoteEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class L2TopVoteEntryMapper implements VoteEntryMapper {

    private static final Log LOG = new Log("[VoteManager]", L2TopVoteEntryMapper.class);

    private Pattern pattern = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})\\s([a-zA-Z]+)");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String sourceCode;

    public L2TopVoteEntryMapper(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public List<VoteEntry> convert(L2PcInstance player, String contents) {
        Matcher matcher = pattern.matcher(contents);
        List<VoteEntry> entries = new ArrayList<>();

        while (matcher.find()) {
            String timestamp = matcher.group(1);
            String playerName = matcher.group(2);
            if (playerName.equalsIgnoreCase(player.getName())) {
                entries.add(new VoteEntry(sourceCode, playerName, LocalDateTime.parse(timestamp, formatter)));
            } else {
                LOG.debug("Could not match vote found for {} from {}, got incorrect player name {}", player, sourceCode, playerName);
            }
        }

        return entries;
    }

    public String getSourceCode() {
        return sourceCode;
    }

}
