package handlers.bypasshandlers.npcviewmod.render;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import handlers.bypasshandlers.npcviewmod.Rendrable;

import java.util.Objects;
import java.util.StringJoiner;

public class ItemView implements Rendrable {

    private Integer id;
    private String name;
    private String icon;
    private DropStats dropStats = DropStats.empty();

    public ItemView(Integer id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public ItemView(Integer id, String name, String icon, DropStats dropStats) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.dropStats = dropStats;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public DropStats getDropStats() {
        return dropStats;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table width=172 cellpadding=1 cellspacing=1 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
        sb.append("<tr>");
        sb.append("<td width=32 valign=top>");
        sb.append("<img src=\"").append(icon).append("\" width=32 height=32>");
        sb.append("</td>");
        sb.append("<td fixwidth=140 align=left>");
        sb.append("<font name=\"hs9\" color=\"CD9000\">");
        sb.append(name);
        sb.append("</font>");
        if (dropStats.getChance() > 0) {
            sb.append(DropStatsView.renderStats(140, dropStats));
        }
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("<tr><td width=32></td><td width=140>&nbsp;</td></tr>");
        sb.append("</table>");
        return sb.toString();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ItemView.class.getSimpleName() + "[", "]")
                .add(Objects.toString(id))
                .add(name)
                .add(icon)
                .toString();
    }

}
