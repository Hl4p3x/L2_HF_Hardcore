package handlers.bypasshandlers.npcviewmod.render;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;

import java.text.DecimalFormat;

public class DropStatsView {

    public static String renderStats(int width, DropStats dropStats) {
        final DecimalFormat chanceFormat = new DecimalFormat("0.0##");
        StringBuilder sb = new StringBuilder();
        sb.append("<table width=" + width + " cellpadding=0 cellspacing=0>");

        sb.append("<tr>");
        sb.append("<td width=50 align=right><font color=\"LEVEL\">Amount: </font></td>");
        sb.append("<td width=100 align=left>");
        if (dropStats.isSingleStack()) {
            sb.append(dropStats.getCount());
        } else {
            sb.append(dropStats.getStacks()).append(" stacks by ").append(dropStats.getCount()).append(" items");
        }
        sb.append("</td>");
        sb.append("</tr>");

        if (dropStats.getChance() > 0) {
            sb.append("<tr><td width=50 align=right><font color=\"LEVEL\">Chance: </font></td>");
            sb.append("<td width=100 align=left>");
            sb.append(chanceFormat.format(dropStats.getChance())).append("%");
            sb.append("</td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

}
