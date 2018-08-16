package handlers.communityboard;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import handlers.communityboard.custom.*;
import handlers.communityboard.custom.bufflists.BuffListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomHomeBoard implements IParseBoardHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CustomHomeBoard.class);

    private final Map<String, BoardAction> actions;

    public CustomHomeBoard() {
        actions = new HashMap<>();
        actions.put("restore", new BoardRestoreAction());
        actions.put("cancel", new BoardCancelAction());
        actions.put("tank_buff_24", new DefaultPresetBuff("tank_buff_24"));
        actions.put("tank_song_dance", new DefaultPresetBuff("tank_song_dance"));
        actions.put("fighter_buff_24", new DefaultPresetBuff("fighter_buff_24"));
        actions.put("fighter_song_dance", new DefaultPresetBuff("fighter_song_dance"));
        actions.put("mage_buff_24", new DefaultPresetBuff("mage_buff_24"));
        actions.put("mage_song_dance", new DefaultPresetBuff("mage_song_dance"));

        actions.put("dances", new BuffListHandler("dances"));
        actions.put("songs", new BuffListHandler("songs"));

        actions.put("prophet", new BuffListHandler("prophet"));
        actions.put("warsmith_summoners", new BuffListHandler("warsmith_summoners"));

        actions.put("elven_elder", new BuffListHandler("elven_elder"));
        actions.put("shillen_elder", new BuffListHandler("shillen_elder"));

        actions.put("overlord", new BuffListHandler("overlord"));
        actions.put("warcryer", new BuffListHandler("warcryer"));
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
            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff.html");

            if (Config.COMMUNITY_RESTORE_ENABLED) {
                String healButton = "<button value=\"Restore CP/HP/MP\"" +
                        "action=\"bypass -h _bbs_buff restore $buff_target\"" +
                        "width=120" +
                        "height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
                html = html.replace("%restore_button%", healButton);
            } else {
                html = html.replace("%restore_button%", "");
            }

            CommunityBoardHandler.separateAndSend(html, player);
            return true;
        } else if (command.startsWith("_bbs_buff")) {
            Optional<ActionArgs> actionArgsOption = ActionArgs.parse(command);

            if (!actionArgsOption.isPresent()) {
                return false;
            }

            CommunityBoardHandler.getInstance().addBypass(player, "Buff", command);

            ActionArgs actionArgs = actionArgsOption.get();
            String actionName = actionArgs.getActionName();
            BoardAction action = actions.get(actionName);
            if (action == null) {
                LOG.warn("Illegal action selected {}", actionName);
                return false;
            }

            ProcessResult result = action.process(player, actionArgs);
            if (result.isFailure()) {
                player.sendPacket(new ExShowScreenMessage(result.getResult(), 2000));
            } else {
                CommunityBoardHandler.separateAndSend(result.getResult(), player);
            }

            return true;
        } else {
            return false;
        }
    }


}
