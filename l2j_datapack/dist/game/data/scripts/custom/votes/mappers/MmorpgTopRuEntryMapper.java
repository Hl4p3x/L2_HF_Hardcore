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

public class MmorpgTopRuEntryMapper implements VoteEntryMapper {

    private static final Log LOG = new Log("[VoteManager]", MmorpgTopRuEntryMapper.class);

    private Pattern pattern = Pattern.compile("(\\d+)\\s+(\\d{2}.\\d{2}.\\d{4} \\d{2}:\\d{2}:\\d{2})\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+(\\w+)\\s+(\\d)");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private String sourceCode;

    public MmorpgTopRuEntryMapper(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public List<VoteEntry> convert(L2PcInstance player, String contents) {
        Matcher matcher = pattern.matcher(contents);
        List<VoteEntry> entries = new ArrayList<>();

        while (matcher.find()) {
            String timestamp = matcher.group(2);
            String playerName = matcher.group(4);
            if (playerName.equalsIgnoreCase(player.getName())) {
                entries.add(new VoteEntry(sourceCode, playerName, LocalDateTime.parse(timestamp, formatter)));
            } else {
                LOG.debug("Could not match vote found for {} from {}, got incorrect player name {}", player, sourceCode, playerName);
            }
        }

        return entries;
    }

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

}
