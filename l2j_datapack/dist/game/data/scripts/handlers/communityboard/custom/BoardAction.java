package handlers.communityboard.custom;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public interface BoardAction {

    ProcessResult process(L2PcInstance player, String[] args);

}
