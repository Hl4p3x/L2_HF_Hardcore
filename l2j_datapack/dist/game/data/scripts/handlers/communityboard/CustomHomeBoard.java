package handlers.communityboard;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.BoardHealAction;
import handlers.communityboard.custom.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CustomHomeBoard implements IParseBoardHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CustomHomeBoard.class);

    private final Map<String, BoardAction> actions;

    public CustomHomeBoard() {
        actions = new HashMap<>();
        actions.put("restore", new BoardHealAction());
    }

    @Override
    public String[] getCommunityBoardCommands() {
        return new String[]{
                "_bbshome",
                "_bbstop",
                "_bbs_buff"
        };
    }

    @Override
    public boolean parseCommunityBoardCommand(String command, L2PcInstance player) {
        if (command.equals("_bbshome") || command.startsWith("_bbstop")) {
            final String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff.html");
            CommunityBoardHandler.separateAndSend(html, player);
            return true;
        } else if (command.startsWith("_bbs_buff")) {
            String[] args = command.split(" ");
            if (args.length <= 0) {
                return false;
            }

            CommunityBoardHandler.getInstance().addBypass(player, "Buff", command);

            String actionName = args[1];
            BoardAction action = actions.get(actionName);
            if (action == null) {
                LOG.warn("Illegal action selected {}", actionName);
                return false;
            }

            ProcessResult result = action.process(player, args);
            if (result.isFailure()) {
                player.sendPacket(new ExShowScreenMessage(result.getComment(), 2000));
            }
            return true;
        } else {
            return false;
        }
    }


}
