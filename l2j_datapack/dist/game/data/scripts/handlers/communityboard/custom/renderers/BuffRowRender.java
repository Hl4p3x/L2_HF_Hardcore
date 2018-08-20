package handlers.communityboard.custom.renderers;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.util.Util;
import handlers.communityboard.custom.bufflists.SkillDisplayNameHelper;

import java.util.List;

public class BuffRowRender {

    public static void appendBuffRow(String action, StringBuilder buffHtml, List<Skill> buffs) {
        buffHtml.append("<tr>");
        buffs.forEach(buff ->
                buffHtml.append("<td>")
                        .append("<center>").append(IconRender.render(buff.getIcon())).append("</center>")
                        .append(ButtonRender.render(
                                SkillDisplayNameHelper.changeDisplayName(buff.getName()),
                                action.replace("%buff_id%", String.valueOf(buff.getId()))
                                )
                        )
                        .append("</td>")
        );
        buffHtml.append("</tr>");
    }

    public static String render(String action, List<Skill> buffs) {
        StringBuilder buffHtml = new StringBuilder();
        List<List<Skill>> buffRows = Util.chunks(buffs, Config.DISPLAY_NUMBER_OF_BUFFS_IN_ROW);
        buffRows.forEach(buffRow -> appendBuffRow(action, buffHtml, buffRow));
        return buffHtml.toString();
    }

}
