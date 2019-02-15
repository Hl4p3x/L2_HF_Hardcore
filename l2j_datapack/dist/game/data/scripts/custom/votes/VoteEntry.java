package custom.votes;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

public class VoteEntry {

    private String sourceCode;
    private String characterName;
    private LocalDateTime timestamp;

    public VoteEntry(String sourceCode, String characterName, LocalDateTime timestamp) {
        this.sourceCode = sourceCode;
        this.characterName = characterName;
        this.timestamp = timestamp;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getCharacterName() {
        return characterName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VoteEntry.class.getSimpleName() + "[", "]")
                .add(String.valueOf(characterName))
                .add(Objects.toString(timestamp))
                .toString();
    }

}
