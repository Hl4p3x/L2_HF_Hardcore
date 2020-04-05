package handlers.communityboard;

import com.l2jserver.Config;
import com.l2jserver.common.DecimalFormatStandard;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.*;
import handlers.communityboard.custom.actions.*;
import handlers.communityboard.custom.bufflists.sets.*;
import handlers.communityboard.custom.bufflists.sets.presets.*;
import handlers.communityboard.custom.renderers.BuffCategoriesRender;
import handlers.communityboard.custom.teleport.CustomTeleportTable;
import handlers.communityboard.custom.teleport.TeleportDestination;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomHomeBoard implements IParseBoardHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CustomHomeBoard.class);

    private final Map<String, BoardAction> actions;

    public CustomHomeBoard() {
        actions = new HashMap<>();
        actions.put("restore", new BoardRestoreAction());
        actions.put("cancel", new BoardCancelAction());
        actions.put("tank_buff", new PresetBuffAction(new TankBuff()));
        actions.put("tank_song_dance", new PresetBuffAction(new TankDanceAndSong()));
        actions.put("fighter_buff", new PresetBuffAction(new FighterBuff()));
        actions.put("fighter_song_dance", new PresetBuffAction(new FighterDanceAndSong()));
        actions.put("mage_buff", new PresetBuffAction(new MageBuff()));
        actions.put("mage_song_dance", new PresetBuffAction(new MageDanceAndSong()));

        Map<String, BoardAction> listBuffActions = new HashMap<>();
        listBuffActions.put("prophet", new ShowBuffListAction("prophet", new Hierophant()));
        listBuffActions.put("dances", new ShowBuffListAction("dances", new SpectralDancer()));
        listBuffActions.put("songs", new ShowBuffListAction("songs", new SwordMuse()));
        listBuffActions.put("elven_elder", new ShowBuffListAction("elven_elder", new ElvenSaint()));
        listBuffActions.put("shillen_elder", new ShowBuffListAction("shillen_elder", new ShillenSaint()));
        listBuffActions.put("overlord", new ShowBuffListAction("overlord", new Dominator()));
        listBuffActions.put("warcryer", new ShowBuffListAction("warcryer", new Doomcryer()));
        listBuffActions.put("others", new ShowBuffListAction("others", new Others()));


        actions.put("list_buff", new RouteAction(listBuffActions));

        actions.put("buff", new BuffHandlerAction(AllBuffs.getBuffMap()));

        actions.put("delete_preset", new DeletePresetAction());
        actions.put("create_preset", new CreatePresetAction());
        actions.put("create_preset_dialog", new CreatePresetDialogAction());
        actions.put("update_preset", new UpdatePresetAction());

        actions.put("remove_preset_buff", new RemovePresetBuffAction());
        actions.put("add_preset_buff", new AddPresetBuffAction());

        actions.put("buff_preset", new PlayerCustomBuffAction());
        actions.put("add_current_to_preset", new AddCurrentBuffsToPresetAction());

        Map<String, BoardAction> listAddToPresetBuffActions = new HashMap<>();
        listAddToPresetBuffActions.put("prophet", new ShowAddToPresetBuffAction(new Hierophant()));
        listAddToPresetBuffActions.put("dances", new ShowAddToPresetBuffAction(new SpectralDancer()));
        listAddToPresetBuffActions.put("songs", new ShowAddToPresetBuffAction(new SwordMuse()));
        listAddToPresetBuffActions.put("elven_elder", new ShowAddToPresetBuffAction(new ElvenSaint()));
        listAddToPresetBuffActions.put("shillen_elder", new ShowAddToPresetBuffAction(new ShillenSaint()));
        listAddToPresetBuffActions.put("overlord", new ShowAddToPresetBuffAction(new Dominator()));
        listAddToPresetBuffActions.put("warcryer", new ShowAddToPresetBuffAction(new Doomcryer()));
        listAddToPresetBuffActions.put("others", new ShowAddToPresetBuffAction(new Others()));

        actions.put("list_add_to_preset_buff", new RouteAction(listAddToPresetBuffActions));
    }


    @Override
    public String[] getCommunityBoardCommands() {
        return new String[]{
                "_bbshome",
                "_bbstop",
                "_bbs_buff",
                "_bbs_teleport"
        };
    }

    @Override
    public boolean parseCommunityBoardCommand(String command, L2PcInstance player) {
        if (command.equals("_bbshome") || command.startsWith("_bbstop")) {
            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff.html");

            if (Config.COMMUNITY_RESTORE_ENABLED) {
                String healButton = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/restore_button.html");
                html = html.replace("%restore_button%", healButton);
            } else {
                html = html.replace("%restore_button%", "");
            }

            if (PaymentHelper.checkFreeService(player)) {
                String free = Strings.of(player).get("free");
                html = html.replace("%teleport_price%", free);
                html = html.replace("%single_buff_price%", free);
                html = html.replace("%preset_buff_price%", free);
            } else {
                html = html.replace("%teleport_price%", DecimalFormatStandard.moneyFormat().format(Config.COMMUNITY_TELEPORT_PRICE));
                html = html.replace("%single_buff_price%", DecimalFormatStandard.moneyFormat().format(Config.COMMUNITY_SINGLE_BUFF_PRICE));
                html = html.replace("%preset_buff_price%", DecimalFormatStandard.moneyFormat().format(Config.COMMUNITY_DEFAULT_PRESET_PRICE));
            }

            List<CommunityBuffList> presets = DAOFactory.getInstance().getCommunityBuffListDao().findAllCommunityBuffSets(player.getObjectId());
            List<String> buffPresetNames = presets.stream().map(CommunityBuffList::getName).collect(Collectors.toList());

            html = html.replace("%user_buff_presets%", String.join(";", buffPresetNames));
            html = html.replace("%buff_list%", BuffCategoriesRender.renderBuffCategoriesList("list_buff", player));

            CommunityBoardHandler.separateAndSend(html, player);
            return true;
        } else if (command.startsWith("_bbs_buff")) {
            Optional<ActionArgs> actionArgsOption = ActionArgs.parse(command);

            if (actionArgsOption.isEmpty()) {
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
        } else if (command.startsWith("_bbs_teleport")) {
            return handleTeleport(command, player);
        } else {
            return false;
        }
    }

    private boolean handleTeleport(String command, L2PcInstance player) {
        Optional<ActionArgs> actionArgsOption = ActionArgs.parse(command);

        if (actionArgsOption.isEmpty()) {
            return false;
        }

        String teleportDestinationCode = actionArgsOption.get().getActionName();

        Optional<TeleportDestination> teleportDestination = CustomTeleportTable.getInstance().findByCode(teleportDestinationCode);
        if (teleportDestination.isEmpty()) {
            LOG.warn("Player {} requested teleport to unknown location {}", player, teleportDestinationCode);
            return false;
        }

        ProcessResult result = BuffCondition.checkCondition(player);
        if (result.isFailure()) {
            player.sendPacket(new ExShowScreenMessage(result.getResult(), 2000));
            return false;
        }

        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && player.getKarma() > 0) {
            player.sendPacket(new ExShowScreenMessage(Strings.of(player).get("karma_players_cannot_use_gk"), 2000));
            return false;
        }

        ProcessResult payment = PaymentHelper.payForService(player, Config.COMMUNITY_TELEPORT_PRICE);
        if (payment.isFailure()) {
            return false;
        }

        SkillHolder soe = new SkillHolder(2013, 1);
        player.fakeCast(soe, Config.COMMUNITY_TELEPORT_DELAY, () -> player.teleToLocation(teleportDestination.get().getCoordinates(), true));
        return true;
    }


}
