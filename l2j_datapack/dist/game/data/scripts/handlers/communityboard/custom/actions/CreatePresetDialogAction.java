package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.renderers.ButtonRender;

public class CreatePresetDialogAction implements BoardAction {

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        player.sendPacket(new NpcHtmlMessage("<center><table><tr><td valign=middle>" +
                "New Preset Name: <edit var=\"preset_name\" width=150>" +
                ButtonRender.render("Create", "bypass -h _bbs_buff create_preset $preset_name") +
                ButtonRender.render("Cancel", "bypass -h _bbshome") +
                "</td></tr></table></center>"));
        return ProcessResult.success();
    }

}
