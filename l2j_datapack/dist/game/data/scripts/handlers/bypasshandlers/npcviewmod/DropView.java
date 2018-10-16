package handlers.bypasshandlers.npcviewmod;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.drops.DropListScope;

import java.util.Optional;

public interface DropView {

    Optional<String> render(L2PcInstance activeChar, L2Npc npc, DropListScope dropListScop, int page);

}
