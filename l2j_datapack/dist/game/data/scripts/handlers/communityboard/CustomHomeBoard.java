package handlers.communityboard;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.actions.BoardCancelAction;
import handlers.communityboard.custom.actions.BoardRestoreAction;
import handlers.communityboard.custom.actions.PresetBuffAction;
import handlers.communityboard.custom.actions.RouteAction;
import handlers.communityboard.custom.bufflists.BuffHandler;
import handlers.communityboard.custom.bufflists.ShowBuffListAction;
import handlers.communityboard.custom.bufflists.sets.*;
import handlers.communityboard.custom.bufflists.sets.presets.*;
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
        actions.put("tank_buff", new PresetBuffAction("tank_buff", new TankBuff()));
        actions.put("tank_song_dance", new PresetBuffAction("tank_song_dance", new TankDanceAndSong()));
        actions.put("fighter_buff", new PresetBuffAction("fighter_buff", new FighterBuff()));
        actions.put("fighter_song_dance", new PresetBuffAction("fighter_song_dance", new FighterDanceAndSong()));
        actions.put("mage_buff", new PresetBuffAction("mage_buff", new MageBuff()));
        actions.put("mage_song_dance", new PresetBuffAction("mage_song_dance", new MageDanceAndSong()));


        Map<String, BoardAction> listBuffActions = new HashMap<>();
        listBuffActions.put("prophet", new ShowBuffListAction("prophet", new Hierophant()));
        listBuffActions.put("dances", new ShowBuffListAction("dances", new SpectralDancer()));
        listBuffActions.put("songs", new ShowBuffListAction("songs", new SwordMuse()));
        listBuffActions.put("elven_elder", new ShowBuffListAction("elven_elder", new ElvenSaint()));
        listBuffActions.put("shillen_elder", new ShowBuffListAction("shillen_elder", new ShillenSaint()));
        listBuffActions.put("overlord", new ShowBuffListAction("overlord", new Dominator()));
        listBuffActions.put("warcryer", new ShowBuffListAction("warcryer", new Doomcryer()));

        actions.put("list_buff", new RouteAction(listBuffActions));

        actions.put("buff", new BuffHandler(AllBuffs.getBuffMap()));

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
            }
            return true;
        } else {
            return false;
        }
    }


}
