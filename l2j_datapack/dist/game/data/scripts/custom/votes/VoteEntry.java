package custom.votes;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

public class VoteEntry {

    private String sourceType;
    private int characterId;
    private LocalDateTime timestamp;

    public int getCharacterId() {
        return characterId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSourceType() {
        return sourceType;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VoteEntry.class.getSimpleName() + "[", "]")
                .add(String.valueOf(characterId))
                .add(Objects.toString(timestamp))
                .toString();
    }

}
