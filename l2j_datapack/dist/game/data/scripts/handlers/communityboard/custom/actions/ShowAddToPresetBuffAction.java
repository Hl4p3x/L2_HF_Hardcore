package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.bufflists.BuffList;
import handlers.communityboard.custom.renderers.BuffRowRender;

import java.util.List;
import java.util.stream.Collectors;

public class ShowAddToPresetBuffAction implements BoardAction {

    private final BuffList buffList;

    public ShowAddToPresetBuffAction(BuffList buffList) {
        this.buffList = buffList;
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        List<Skill> buffSkills = buffList
                .getBuffs()
                .stream()
                .map(SkillHolder::getSkill)
                .collect(Collectors.toList());

        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff_list.html");

        String buffRows = BuffRowRender.render("bypass -h _bbs_buff add_preset_buff %buff_id%", buffSkills);
        html = html.replace("%buff_list%", buffRows);
        CommunityBoardHandler.separateAndSend(html, player);
        return ProcessResult.success();
    }

}
