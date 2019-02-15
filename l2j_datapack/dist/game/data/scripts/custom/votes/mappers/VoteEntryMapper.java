package custom.votes.mappers;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import custom.votes.VoteEntry;

import java.util.List;

public interface VoteEntryMapper {

    List<VoteEntry> convert(L2PcInstance player, String contents);

    String getSourceCode();

}
