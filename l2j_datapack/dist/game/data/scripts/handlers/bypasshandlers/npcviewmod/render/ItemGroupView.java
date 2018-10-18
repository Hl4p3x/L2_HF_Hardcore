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

    public ItemGroupView(String name, List<ItemView> items) {
        this(name, DropStats.empty(), items);
    }

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

        int itemsPerRow = 3;

        StringBuilder sb = new StringBuilder();
        sb.append("<table width=600 cellpadding=2 cellspacing=1 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
        sb.append("<tr>");
        sb.append("<td align=center><font color=\"LEVEL\">").append(name).append("</font></td>");
        sb.append("</tr>");

        sb.append("<tr><td>");
        sb.append(DropStatsView.renderStats(432, dropStats));
        sb.append("</td></tr>");

        sb.append("<tr><td align=center>");

        sb.append("<table>");
        sb.append("<tr>");

        int itemPerRowCounter = 0;
        for (int i = range.getLow(); i <= range.getHigh(); i++) {
            if (itemPerRowCounter >= 3) {
                sb.append("</tr><tr>");
                itemPerRowCounter = 0;
            }
            sb.append("<td>");
            sb.append(items.get(i).render());
            sb.append("</td>");

            itemPerRowCounter += 1;
        }

        for (int i = 0; i < itemsPerRow - itemPerRowCounter; i++) {
            sb.append("<td width=172></td>");
        }

        sb.append("</tr>");
        sb.append("</table>");

        sb.append("</td></tr>");

        sb.append("<tr><td>&nbsp;</td></tr>");
        sb.append("</table>");
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
