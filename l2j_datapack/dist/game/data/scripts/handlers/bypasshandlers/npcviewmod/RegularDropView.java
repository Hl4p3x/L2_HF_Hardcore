package handlers.bypasshandlers.npcviewmod;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.drops.DropListScope;
import com.l2jserver.gameserver.model.drops.GeneralDropItem;
import com.l2jserver.gameserver.model.drops.GroupedGeneralDropItem;
import com.l2jserver.gameserver.model.drops.IDropItem;
import com.l2jserver.gameserver.model.items.L2Item;
import handlers.bypasshandlers.NpcViewMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;


public class RegularDropView {

    private static final Logger LOG = LoggerFactory.getLogger(RegularDropView.class);

    private static final int DROP_LIST_ITEMS_PER_PAGE = 10;

    public Optional<String> renderHtml(L2PcInstance activeChar, L2Npc npc, DropListScope dropListScope, int page) {
        final List<IDropItem> dropList = npc.getTemplate().getDropList(dropListScope);
        if ((dropList == null) || dropList.isEmpty()) {
            return Optional.empty();
        }

        int pages = dropList.size() / DROP_LIST_ITEMS_PER_PAGE;
        if ((DROP_LIST_ITEMS_PER_PAGE * pages) < dropList.size()) {
            pages++;
        }

        if (page >= pages) {
            page = pages - 1;
        }

        final int start = page > 0 ? page * DROP_LIST_ITEMS_PER_PAGE : 0;

        int end = (page * DROP_LIST_ITEMS_PER_PAGE) + DROP_LIST_ITEMS_PER_PAGE;
        if (end > dropList.size()) {
            end = dropList.size();
        }

        final DecimalFormat amountFormat = new DecimalFormat("#,###");
        final DecimalFormat chanceFormat = new DecimalFormat("0.00##");

        int leftHeight = 0;
        int rightHeight = 0;
        final StringBuilder leftSb = new StringBuilder();
        final StringBuilder rightSb = new StringBuilder();
        for (int i = start; i < end; i++) {
            final StringBuilder sb = new StringBuilder();

            int height = 64;
            final IDropItem dropItem = dropList.get(i);
            if (dropItem instanceof GeneralDropItem) {
                addGeneralDropItem(activeChar, npc, amountFormat, chanceFormat, sb, (GeneralDropItem) dropItem);
            } else if (dropItem instanceof GroupedGeneralDropItem) {
                final GroupedGeneralDropItem generalGroupedDropItem = (GroupedGeneralDropItem) dropItem;
                if (generalGroupedDropItem.getItems().size() == 1) {
                    final GeneralDropItem generalDropItem = generalGroupedDropItem.getItems().get(0);
                    addGeneralDropItem(activeChar, npc, amountFormat, chanceFormat, sb, new GeneralDropItem(generalDropItem.getItemId(), generalDropItem.getMin(), generalDropItem.getMax(), (generalDropItem.getChance() * generalGroupedDropItem.getChance())
                            / 100, generalDropItem.getAmountStrategy(), generalDropItem.getChanceStrategy(), generalGroupedDropItem.getPreciseStrategy(), generalGroupedDropItem.getKillerChanceModifierStrategy(), generalDropItem.getDropCalculationStrategy()));
                } else {
                    GroupedGeneralDropItem normalized = generalGroupedDropItem.normalizeMe(npc, activeChar);
                    sb.append("<table width=332 cellpadding=2 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
                    sb.append("<tr><td width=32 valign=top><img src=\"L2UI_CT1.ICON_DF_premiumItem\" width=32 height=32></td>");
                    sb.append("<td fixwidth=300 align=center><font name=\"ScreenMessageSmall\" color=\"CD9000\">One from group</font>");
                    sb.append("</td></tr><tr><td width=32></td><td width=300><table width=295 cellpadding=0 cellspacing=0><tr>");
                    sb.append("<td width=48 align=right valign=top><font color=\"LEVEL\">Chance:</font></td>");
                    sb.append("<td width=247 align=center>");
                    sb.append(chanceFormat.format(Math.min(normalized.getChance(), 100)));
                    sb.append("%</td></tr></table><br>");

                    for (GeneralDropItem generalDropItem : normalized.getItems()) {
                        final L2Item item = ItemTable.getInstance().getTemplate(generalDropItem.getItemId());
                        sb.append("<table width=291 cellpadding=2 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
                        sb.append("<tr><td width=32 valign=top>");
                        String icon = item.getIcon();
                        if (icon == null) {
                            icon = "icon.etc_question_mark_i00";
                        }
                        sb.append("<img src=\"").append(icon).append("\" width=32 height=32>");
                        sb.append("</td><td fixwidth=259 align=center><font name=\"hs9\" color=\"CD9000\">");
                        sb.append(item.getName());
                        sb.append("</font></td></tr><tr><td width=32></td><td width=259><table width=253 cellpadding=0 cellspacing=0>");
                        sb.append("<tr><td width=48 align=right valign=top><font color=\"LEVEL\">Amount:</font></td><td width=205 align=center>");
                        RegularDropView.MinMax minMax = getPreciseMinMax(normalized.getChance(), generalDropItem.getMin(npc), generalDropItem.getMax(npc), generalDropItem.isPreciseCalculated());
                        final long min = minMax.min;
                        final long max = minMax.max;
                        if (min == max) {
                            sb.append(amountFormat.format(min));
                        } else {
                            sb.append(amountFormat.format(min));
                            sb.append(" - ");
                            sb.append(amountFormat.format(max));
                        }

                        sb.append("</td></tr><tr><td width=48 align=right valign=top><font color=\"LEVEL\">Chance:</font></td>");
                        sb.append("<td width=205 align=center>");
                        sb.append(chanceFormat.format(Math.min(generalDropItem.getChance(), 100)));
                        sb.append("%</td></tr></table></td></tr><tr><td width=32></td><td width=259>&nbsp;</td></tr></table>");

                        height += 64;
                    }

                    sb.append("</td></tr><tr><td width=32></td><td width=300>&nbsp;</td></tr></table>");
                }
            }

            if (leftHeight >= (rightHeight + height)) {
                rightSb.append(sb);
                rightHeight += height;
            } else {
                leftSb.append(sb);
                leftHeight += height;
            }
        }

        final StringBuilder bodySb = new StringBuilder();
        bodySb.append("<table><tr>");
        bodySb.append("<td>");
        bodySb.append(leftSb.toString());
        bodySb.append("</td><td>");
        bodySb.append(rightSb.toString());
        bodySb.append("</td>");
        bodySb.append("</tr></table>");

        String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/NpcView/DropList.htm");
        if (html == null) {
            LOG.warn(NpcViewMod.class.getSimpleName() + ": The html file data/html/mods/NpcView/DropList.htm could not be found.");
            return Optional.empty();
        }
        html = html.replaceAll("%name%", npc.getName());
        html = html.replaceAll("%dropListButtons%", DropListButtonsView.render(npc));
        html = html.replaceAll("%pages%", DropListPagesView.render(pages, npc, dropListScope));
        html = html.replaceAll("%items%", bodySb.toString());

        return Optional.of(html);
    }

    private static void addGeneralDropItem(L2PcInstance activeChar, L2Npc npc, final DecimalFormat amountFormat, final DecimalFormat chanceFormat, final StringBuilder sb, final GeneralDropItem dropItem) {
        final L2Item item = ItemTable.getInstance().getTemplate(dropItem.getItemId());
        sb.append("<table width=332 cellpadding=2 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
        sb.append("<tr><td width=32 valign=top>");
        sb.append("<img src=\"").append(item.getIcon()).append("\" width=32 height=32>");
        sb.append("</td><td fixwidth=300 align=center><font name=\"hs9\" color=\"CD9000\">");
        sb.append(item.getName());
        sb.append("</font></td></tr><tr><td width=32></td><td width=300><table width=295 cellpadding=0 cellspacing=0>");
        sb.append("<tr><td width=48 align=right valign=top><font color=\"LEVEL\">Amount:</font></td>");
        sb.append("<td width=247 align=center>");
        RegularDropView.MinMax minMax = getPreciseMinMax(dropItem.getChance(npc, activeChar), dropItem.getMin(npc), dropItem.getMax(npc), dropItem.isPreciseCalculated());

        final long min = minMax.min;
        final long max = minMax.max;
        if (min == max) {
            sb.append(amountFormat.format(min));
        } else {
            sb.append(amountFormat.format(min));
            sb.append(" - ");
            sb.append(amountFormat.format(max));
        }

        sb.append("</td></tr><tr><td width=48 align=right valign=top><font color=\"LEVEL\">Chance:</font></td>");
        sb.append("<td width=247 align=center>");
        sb.append(chanceFormat.format(Math.min(dropItem.getChance(npc, activeChar), 100)));
        sb.append("%</td></tr></table></td></tr><tr><td width=32></td><td width=300>&nbsp;</td></tr></table>");
    }

    private static RegularDropView.MinMax getPreciseMinMax(double chance, long min, long max, boolean isPrecise) {
        if (!isPrecise || (chance <= 100)) {
            return new RegularDropView.MinMax(min, max);
        }

        int mult = (int) (chance) / 100;
        return new RegularDropView.MinMax(mult * min, (chance % 100) > 0 ? (mult + 1) * max : mult * max);
    }

    private static class MinMax {

        public final long min, max;

        MinMax(long min, long max) {
            this.min = min;
            this.max = max;
        }

    }

}
