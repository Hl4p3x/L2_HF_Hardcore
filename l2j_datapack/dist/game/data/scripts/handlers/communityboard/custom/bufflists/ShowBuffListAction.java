package handlers.communityboard.custom.bufflists;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.util.Util;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;

import java.util.List;
import java.util.stream.Collectors;

public class ShowBuffListAction implements BoardAction {

    private final String buffListName;
    private final BuffList buffList;

    public ShowBuffListAction(String buffListName, BuffList buffList) {
        this.buffListName = buffListName;
        this.buffList = buffList;
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        List<Skill> buffSkills = buffList
                .getBuffs()
                .stream()
                .map(SkillHolder::getSkill)
                .collect(Collectors.toList());
        CommunityBoardHandler.separateAndSend(render(player, buffSkills), player);
        return ProcessResult.success();
    }

    private void appendBuffRow(StringBuilder buffHtml, List<Skill> buffs) {
        buffHtml.append("<tr>");
        buffs.forEach(buff ->
                buffHtml.append("<td>")
                        .append("<center>").append(IconRender.render(buff.getIcon())).append("</center>")
                        .append(ButtonRender.render(SkillDisplayNameHelper.changeDisplayName(buff.getName()), "bypass -h _bbs_buff buff " + buffListName + " " + buff.getId() + " $buff_target"))
                        .append("</td>")
        );
        buffHtml.append("</tr>");
    }

    private String render(L2PcInstance player, List<Skill> buffs) {
        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff_list.html");
        StringBuilder buffHtml = new StringBuilder();
        List<List<Skill>> buffRows = Util.chunks(buffs, Config.DISPLAY_NUMBER_OF_BUFFS_IN_ROW);
        buffRows.forEach(buffRow -> appendBuffRow(buffHtml, buffRow));
        return html.replace("%buff_list%", buffHtml);
    }

}
