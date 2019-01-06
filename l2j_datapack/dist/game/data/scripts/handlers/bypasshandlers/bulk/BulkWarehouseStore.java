package handlers.bypasshandlers.bulk;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.WarehouseType;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.transfer.bulk.store.BulkStoreService;
import com.l2jserver.localization.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class BulkWarehouseStore implements IBypassHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BulkWarehouseStore.class);

    private static final String[] COMMANDS = {
            "bulk_deposit"
    };

    @Override
    public boolean useBypass(String command, L2PcInstance player, L2Character target) {
        if (!player.getLastFolkNPC().isWarehouse()) {
            return false;
        }

        StringTokenizer st = new StringTokenizer(command, " ");

        String action = st.nextToken();
        if (action.toLowerCase().equals("bulk_deposit") && st.countTokens() == 1) {
            String warehouseType = st.nextToken();
            String htmlText = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "./data/html/custom/bulk/bulk_deposit.html");
            htmlText = htmlText.replaceAll("%npc_name%", target.getName());
            htmlText = htmlText.replaceAll("%objectId%", String.valueOf(target.getObjectId()));
            htmlText = htmlText.replaceAll("%warehouse_type%", warehouseType);
            player.sendPacket(new NpcHtmlMessage(htmlText));
            return true;
        } else if (action.toLowerCase().startsWith("bulk_deposit") && st.countTokens() == 2) {
            String warehouseType = st.nextToken();
            String itemType = st.nextToken();
            BulkStoreService.getInstance().storeAllByType(EtcItemType.of(itemType), WarehouseType.of(warehouseType), player);
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
