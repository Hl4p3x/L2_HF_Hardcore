package handlers.bypasshandlers.bulk;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.transfer.bulk.store.BulkStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class BulkWarehouseStore implements IBypassHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BulkWarehouseStore.class);

    private static final String[] COMMANDS = {
            "deposit_bulk"
    };

    @Override
    public boolean useBypass(String command, L2PcInstance player, L2Character target) {
        if (!player.getLastFolkNPC().isWarehouse()) {
            return false;
        }

        StringTokenizer st = new StringTokenizer(command, " ");
        if (st.countTokens() != 2) {
            LOG.warn("Player {} is trying to use bulk deposit with incorrect arguments: {}", player, command);
            return false;
        }

        String action = st.nextToken();
        if (action.toLowerCase().startsWith("deposit_bulk")) {
            String itemType = st.nextToken();
            BulkStoreService.getInstance().storeAllByType(EtcItemType.of(itemType), player);
            return true;
        }

        return false;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
