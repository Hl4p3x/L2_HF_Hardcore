package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.localization.Strings;
import com.l2jserver.localization.StringsTable;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.renderers.ButtonRender;

public class CreatePresetDialogAction implements BoardAction {

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        StringsTable lang = Strings.of(player);
        player.sendPacket(new NpcHtmlMessage("<center><table><tr><td valign=middle>" +
                lang.get("preset_name") + ": <edit var=\"preset_name\" width=150>" +
                ButtonRender.render(lang.get("create"), "bypass -h _bbs_buff create_preset $preset_name") +
                ButtonRender.render(lang.get("cancel"), "bypass -h _bbshome") +
                "</td></tr></table></center>"));
        return ProcessResult.success();
    }

}
