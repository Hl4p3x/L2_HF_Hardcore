package handlers.communityboard.custom.renderers;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class BuffCategoriesRender {

    public static String renderBuffCategoriesList(String bypassActionName, L2PcInstance player) {
        return renderBuffCategoriesList(bypassActionName, "", player);
    }

    public static String renderBuffCategoriesList(String bypassActionName, String arguments, L2PcInstance player) {
        return HtmCache.getInstance()
                .getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff_categories.html")
                .replace("%bypass_action%", bypassActionName)
                .replace("%arguments%", arguments);
    }

}
