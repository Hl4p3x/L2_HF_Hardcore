package custom.votes.mappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HopzoneVote {

    private boolean voted;
    private LocalDateTime voteTime;

    public LocalDateTime getVoteTime() {
        return voteTime;
    }

    public boolean isVoted() {
        return voted;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HopzoneVote.class.getSimpleName() + "[", "]")
                .add(Objects.toString(voteTime))
                .toString();
    }

}
