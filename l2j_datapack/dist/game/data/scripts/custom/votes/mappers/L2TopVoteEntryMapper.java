package custom.votes.mappers;

import custom.votes.VoteEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class L2TopVoteEntryMapper implements VoteEntryMapper {

    private Pattern pattern = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})\\s([a-zA-Z]+)");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss");

    private String sourceCode;

    public L2TopVoteEntryMapper(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public List<VoteEntry> convert(String contents) {
        Matcher matcher = pattern.matcher(contents);
        List<VoteEntry> entries = new ArrayList<>();

        while (matcher.find()) {
            String timestamp = matcher.group(0);
            String playerName = matcher.group(1);
            entries.add(new VoteEntry(sourceCode, playerName, LocalDateTime.parse(timestamp, formatter)));
        }

        return entries;
    }

    public String getSourceCode() {
        return sourceCode;
    }

}
