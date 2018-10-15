package handlers.bypasshandlers.npcviewmod;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.drops.DropListScope;

public class DropListPagesView {

    public static String render(int pages, L2Npc npc, DropListScope dropListScope) {
        final StringBuilder pagesSb = new StringBuilder();
        if (pages > 1) {
            pagesSb.append("<table><tr>");
            for (int i = 0; i < pages; i++) {
                pagesSb.append("<td align=center><button value=\"").append(i + 1).append("\" width=20 height=20 action=\"bypass NpcViewMod dropList ").append(dropListScope).append(" ").append(npc.getObjectId()).append(" ").append(i).append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }
            pagesSb.append("</tr></table>");
        }
        return pagesSb.toString();
    }

}
