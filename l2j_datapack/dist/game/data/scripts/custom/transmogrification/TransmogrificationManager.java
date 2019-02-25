package custom.transmogrification;

import ai.npc.AbstractNpcAI;
import com.l2jserver.common.Log;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.multisell.ListContainer;
import custom.common.Config;
import custom.common.ShortNpc;

public class TransmogrificationManager extends AbstractNpcAI {

    private static final Log LOG = Log.of("Transmogrification", TransmogrificationManager.class);

    private L2NpcTemplate transmogrificationManagerTemplate;
    private TransmogrificationConfig config;
    private L2Npc spawn;

    public TransmogrificationManager() {
        super(TransmogrificationManager.class.getSimpleName(), "Transmogrification");
        load();
    }

    private void load() {
        loadConfig();
        loadNpc();
        spawnNpc();
    }

    public void spawnNpc() {
        spawn = addSpawn(transmogrificationManagerTemplate.getId(), new Location(82203, 148540, -3464, 16000));
    }

    @Override
    public boolean unload() {
        NpcData.getInstance().removeNpc(transmogrificationManagerTemplate);
        if (spawn != null) {
            spawn.deleteMe();
            spawn = null;
        }
        return super.unload();
    }

    private void loadConfig() {
        config = Config.load("./transmogrification_config.yml", TransmogrificationConfig.class, LOG);
    }

    private void loadNpc() {
        ShortNpc npc = config.getNpc();
        transmogrificationManagerTemplate = new L2NpcTemplate(npc.getId(), npc.getDisplayId(), npc.getName(), npc.getTitle(), npc.getCollisionRadius(), npc.getCollisionHeight());
        transmogrificationManagerTemplate.setUsingServerSideName(true);
        transmogrificationManagerTemplate.setUsingServerSideTitle(true);
        NpcData.getInstance().addNpc(transmogrificationManagerTemplate);
        addFirstTalkId(npc.getId());
        addTalkId(npc.getId());
        addStartNpc(npc.getId());
        LOG.info("Loaded NPC {}", npc);
    }

    @Override
    public String onFirstTalk(L2Npc npc, L2PcInstance player) {
        return onTalk(npc, player);
    }

    @Override
    public String onTalk(L2Npc npc, L2PcInstance player) {
        return HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/scripts/custom/transmogrification/transmogrification.html");
    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
        if (event.equals("bestow")) {
            ListContainer bestowTransmogrificationList = ListContainer.prepareTransmogrificationBestow(1238976, npc, player, true);
            MultisellData.getInstance().separateAndSend(bestowTransmogrificationList, player, npc);
            return null;
        } else if (event.contains("remove")) {
            ListContainer removeTransmogrificationList = ListContainer.prepareTransmogrificationRemove(1238977, npc, player, true);
            MultisellData.getInstance().separateAndSend(removeTransmogrificationList, player, npc);
            return null;
        }
        return super.onAdvEvent(event, npc, player);
    }

    public static void main(String[] args) {
        new TransmogrificationManager();
    }

}
