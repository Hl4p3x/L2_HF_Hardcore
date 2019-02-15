package custom.votes.mappers;

import custom.votes.VoteEntry;

import java.util.List;

public interface VoteEntryMapper {

    List<VoteEntry> convert(String contents);

    String getSourceCode();

}
