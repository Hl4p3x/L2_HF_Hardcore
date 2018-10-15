package handlers.bypasshandlers.npcviewmod;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.drops.DropListScope;
import com.l2jserver.gameserver.model.drops.IDropItem;

import java.util.List;
import java.util.Map;

public class DropListButtonsView {

    public static String render(L2Npc npc) {
        final StringBuilder sb = new StringBuilder();
        final Map<DropListScope, List<IDropItem>> dropLists = npc.getTemplate().getDropLists();
        if ((dropLists != null) && !dropLists.isEmpty() && (dropLists.containsKey(DropListScope.DEATH) || dropLists.containsKey(DropListScope.CORPSE))) {
            sb.append("<table width=295 cellpadding=0 cellspacing=0><tr>");
            if (dropLists.containsKey(DropListScope.DEATH)) {
                sb.append("<td align=center><button value=\"Show Drop\" width=110 height=25 action=\"bypass NpcViewMod dropList DEATH ").append(npc.getObjectId()).append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }

            if (dropLists.containsKey(DropListScope.CORPSE)) {
                sb.append("<td align=center><button value=\"Show Spoil\" width=110 height=25 action=\"bypass NpcViewMod dropList CORPSE ").append(npc.getObjectId()).append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }
            sb.append("</tr></table>");
        }
        return sb.toString();
    }

}
