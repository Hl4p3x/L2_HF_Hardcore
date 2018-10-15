package handlers.bypasshandlers.npcviewmod;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropTable;

public class DynamicDropView {

    public String render(L2Npc npc, int page) {
        DynamicDropTable.getInstance().getDynamicDropData(npc.getLevel());
    }

    private String drawHeader() {

    }

    private String drawItem() {

    }

    private String drawRegularDropItem() {

    }

    private String drawPageSelector() {

    }

    private String drawFooter() {

    }
}

