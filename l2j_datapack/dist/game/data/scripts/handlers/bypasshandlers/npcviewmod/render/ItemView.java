package handlers.bypasshandlers.npcviewmod.render;

import handlers.bypasshandlers.npcviewmod.Rendrable;

import java.util.Objects;
import java.util.StringJoiner;

public class ItemView implements Rendrable {

    private Integer id;
    private String name;
    private String icon;

    public ItemView(Integer id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
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

    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table width=300 cellpadding=1 cellspacing=1 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
        sb.append("<tr>");
        sb.append("<td width=32 valign=top>");
        sb.append("<img src=\"").append(icon).append("\" width=32 height=32>");
        sb.append("</td>");
        sb.append("<td width=150 align=left>");
        sb.append("<font name=\"hs9\" color=\"CD9000\">");
        sb.append(name);
        sb.append("</font>");
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("<tr><td width=32></td><td width=150>&nbsp;</td></tr>");
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
