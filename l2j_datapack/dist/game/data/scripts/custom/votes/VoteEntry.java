package custom.votes;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

public class VoteEntry {

    private String characterName;
    private LocalDateTime timestamp;

    public String getCharacterName() {
        return characterName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VoteEntry.class.getSimpleName() + "[", "]")
                .add(characterName)
                .add(Objects.toString(timestamp))
                .toString();
    }

}
