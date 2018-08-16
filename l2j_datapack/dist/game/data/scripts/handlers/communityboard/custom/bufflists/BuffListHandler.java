package handlers.communityboard.custom.bufflists;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;

import java.util.List;

public class BuffListHandler implements BoardAction {

    private final BuffList buffList;

    public BuffListHandler(BuffList buffList) {
        this.buffList = buffList;
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {


        return null;
    }

    private void render(List<Skill> buffs) {
     /*   String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff_list.html");
        html = html.replace("%restore_button%", buffList);
        CommunityBoardHandler.separateAndSend(html, player);*/

    }
}
