package handlers.bypasshandlers.bulk;

import com.l2jserver.common.DecimalFormatStandart;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.transfer.bulk.shop.BulkSellService;
import com.l2jserver.localization.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class BulkSell implements IBypassHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BulkSell.class);

    private static final String[] COMMANDS = {
            "bulk_sell"
    };

    @Override
    public boolean useBypass(String command, L2PcInstance player, L2Character target) {
        if (!(target instanceof L2MerchantInstance)) {
            LOG.warn("Player {} is trying to use bulk sell without a trader", player);
            return false;
        }

        StringTokenizer st = new StringTokenizer(command, " ");

        String action = st.nextToken();
        if (action.toLowerCase().equals("bulk_sell") && st.countTokens() == 0) {
            String htmlText = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "./data/html/custom/bulk/bulk_sell_options.html");
            htmlText = htmlText.replaceAll("%npc_name%", target.getName());
            htmlText = htmlText.replace("%objectId%", String.valueOf(target.getObjectId()));
            player.sendPacket(new NpcHtmlMessage(htmlText));
            return true;
        } else if (action.toLowerCase().startsWith("bulk_sell") && st.countTokens() == 1) {
            String type = st.nextToken();
            EtcItemType itemType = EtcItemType.of(type);
            String htmlText = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "./data/html/custom/bulk/bulk_sell.html");
            htmlText = htmlText.replace("%objectId%", String.valueOf(target.getObjectId()));
            htmlText = htmlText.replaceAll("%npc_name%", target.getName());
            htmlText = htmlText.replaceAll("%item_group_name%", type);
            htmlText = htmlText.replaceAll("%item_total_count%", String.valueOf(BulkSellService.getInstance().sellAllByTypeCount(itemType, player)));
            htmlText = htmlText.replaceAll("%item_total_sum%", DecimalFormatStandart.moneyFormat().format(BulkSellService.getInstance().sellAllByTypePrice(itemType, player)));
            htmlText = htmlText.replaceAll("%item_group_type%", type);
            player.sendPacket(new NpcHtmlMessage(htmlText));
            return true;
        } else if (action.toLowerCase().startsWith("bulk_sell") && st.countTokens() == 2) {
            String type = st.nextToken();
            BulkSellService.getInstance().sellAllByType(EtcItemType.of(type), player);
            return true;
        }

        player.sendMessage(Strings.of(player).get("bulk_sell_failed"));
        LOG.warn("Player {} is trying to use bulk sell with incorrect arguments: {}", player, command);
        return false;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }

}
