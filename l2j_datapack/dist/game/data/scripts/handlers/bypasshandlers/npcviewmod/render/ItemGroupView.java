package handlers.bypasshandlers.npcviewmod.render;

import com.l2jserver.gameserver.model.actor.templates.drop.Range;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import handlers.bypasshandlers.npcviewmod.PageRenderable;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class ItemGroupView implements PageRenderable {

    private String name;
    private DropStats dropStats;
    private List<ItemView> items;

    public ItemGroupView(String name, DropStats dropStats, List<ItemView> items) {
        this.name = name;
        this.dropStats = dropStats;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public List<ItemView> getItems() {
        return items;
    }

    public DropStats getDropStats() {
        return dropStats;
    }

    @Override
    public String render(Range range) {
        if (range.getLow() >= items.size() || range.getHigh() >= items.size()) {
            throw new IllegalArgumentException("Cannot render range that is out of category size (" + items.size() + ") bounds " + range);
        }

        if (!range.isPositive()) {
            throw new IllegalArgumentException("Cannot render a negative item range " + range);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<table width=500 cellpadding=2 cellspacing=1 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
        sb.append("<tr>");
        sb.append("<td width=32><img src=\"L2UI_CT1.ICON_DF_premiumItem\" width=32 height=32></td>");
        sb.append("<td width=300 align=left valign=middle><font color=\"LEVEL\">").append(name).append("</font></td>");
        sb.append("</tr>");

        sb.append("<tr><td width=32></td><td width=300>");
        sb.append(renderStats(dropStats));
        sb.append("</td></tr>");

        sb.append("<tr><td width=32></td><td width=300>");
        for (int i = range.getLow(); i <= range.getHigh(); i++) {
            sb.append(items.get(i).render());
        }
        sb.append("</td></tr>");

        sb.append("<tr><td width=32></td><td width=300>&nbsp;</td></tr>");
        sb.append("</table>");
        return sb.toString();
    }

    private String renderStats(DropStats dropStats) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table width=300 cellpadding=0 cellspacing=0>");

        sb.append("<tr>");
        sb.append("<td width=50 align=right><font color=\"LEVEL\">Amount: </font></td>");
        sb.append("<td width=250 align=left>");
        if (dropStats.isSingleStack()) {
            sb.append(dropStats.getCount());
        } else {
            sb.append(dropStats.getStacks()).append(" stacks by ").append(dropStats.getCount()).append(" items");
        }
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr><td width=50 align=right><font color=\"LEVEL\">Chance: </font></td>");
        sb.append("<td width=250 align=left>");
        sb.append(dropStats.getChance()).append("%");
        sb.append("</td></tr></table>");
        return sb.toString();
    }

    public int getSize() {
        return items.size();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ItemGroupView.class.getSimpleName() + "[", "]")
                .add(name)
                .add(Objects.toString(items))
                .toString();
    }

}
