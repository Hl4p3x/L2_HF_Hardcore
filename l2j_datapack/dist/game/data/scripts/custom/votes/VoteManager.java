package custom.votes;

import ai.SpawnManager;
import ai.npc.AbstractNpcAI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.l2jserver.common.Log;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.NpcLocation;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.multisell.ListContainer;
import com.l2jserver.localization.Strings;
import com.l2jserver.util.YamlMapper;
import custom.votes.config.VoteConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class VoteManager extends AbstractNpcAI {

    private static final Log LOG = new Log("[VoteManager]", VoteManager.class);

    private Map<String, ListContainer> multisells = new ConcurrentHashMap<>();
    private SpawnManager spawnManager = new SpawnManager();
    private VoteConfig config;
    private L2NpcTemplate voteManagerTemplate;
    private VoteRepository voteRepository = new VoteRepository();
    private VoteChecker voteChecker;

    private VoteManager() {
        super(VoteManager.class.getSimpleName(), "Vote Manager");
        load();
    }

    public void load() {
        LOG.info("Loading Vote Manager ...");
        loadConfig();
        loadVoteChecker();
        loadNpc();
        loadRewardMultisells();
        spawnNpcs();
    }

    private void loadVoteChecker() {
        voteChecker = new VoteChecker(voteRepository, config.getSources());
        LOG.info("Vote Checker loaded");
    }

    private void loadNpc() {
        ShortNpc npc = config.getNpc();
        voteManagerTemplate = new L2NpcTemplate(npc.getId(), npc.getDisplayId(), npc.getName(), npc.getTitle(), npc.getCollisionRadius(), npc.getCollisionHeight());
        voteManagerTemplate.setUsingServerSideName(true);
        voteManagerTemplate.setUsingServerSideTitle(true);
        NpcData.getInstance().addNpc(voteManagerTemplate);
        addFirstTalkId(npc.getId());
        addTalkId(npc.getId());
        addStartNpc(npc.getId());
        LOG.info("Loaded NPC {}", npc);
    }

    private void loadConfig() {
        String voteConfigFilePath = "./config/vote_config.yml";
        try {
            config = YamlMapper.getInstance().readValue(getClass().getResourceAsStream(voteConfigFilePath), VoteConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load config from " + voteConfigFilePath, e);
        }
        LOG.info("Config loaded");
    }

    private void spawnNpcs() {
        String spawnlistFilename = "./config/vote_manager_spawnlist.yml";
        List<Location> spawnlist;
        try {
            spawnlist = YamlMapper.getInstance().readValue(getClass().getResourceAsStream(spawnlistFilename), new TypeReference<List<Location>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Could not load spawnlist from " + spawnlistFilename, e);
        }
        LOG.info("Loaded {} spawns", spawnlist.size());

        spawnlist.stream()
                .map(spawn -> new NpcLocation(config.getNpc().getId(), spawn))
                .map(spawn -> addSpawn(spawn.getNpcId(), spawn.getLocation()))
                .forEach(spawnManager::registerSpawn);
        LOG.info("Spawned {} npcs", spawnManager.size());
    }

    private void loadRewardMultisells() {
        String rewardFolderPath = "./rewards/";
        try (Stream<Path> files = Files.list(Path.of(getClass().getResource(rewardFolderPath).toURI()))) {
            files.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            ListContainer multisell = YamlMapper.getInstance().readValue(file.toFile(), new TypeReference<ListContainer>() {
                            });
                            multisell.allowNpc(config.getNpc().getId());
                            multisells.put(file.toFile().getName().replace(".yml", ""), multisell);
                        } catch (IOException e) {
                            throw new IllegalStateException("Could not read multisell file " + file.getFileName().toString(), e);
                        }
                    });
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Could not read reward files from " + rewardFolderPath, e);
        }

        LOG.info("Loaded {} reward multisells", multisells.keySet().size());
    }

    @Override
    public boolean unload() {
        despawnNpcs();
        unloadRewardMultisells();
        NpcData.getInstance().removeNpc(voteManagerTemplate);
        voteManagerTemplate = null;
        voteChecker = null;
        config = null;
        return super.unload();
    }

    private void despawnNpcs() {
        spawnManager.deleteAll();
    }

    private void unloadRewardMultisells() {
        multisells = new ConcurrentHashMap<>();
    }

    @Override
    public String onFirstTalk(L2Npc npc, L2PcInstance player) {
        return onTalk(npc, player);
    }

    @Override
    public String onTalk(L2Npc npc, L2PcInstance player) {
        return HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/scripts/custom/votes/html/vote_manager.html");
    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
        if (event.equals("claim_rewards")) {
            List<VoteEntry> votes = voteChecker.checkNewVotes(player);
            if (votes.isEmpty()) {
                player.sendScreenMessage(Strings.of(player).get("no_new_votes"));
                return null;
            }
        } else if (event.contains("exchange")) {
            MultisellData.getInstance().separateAndSend(multisells.get(event), player, npc);
        }
        return super.onAdvEvent(event, npc, player);
    }

    public static void main(String[] args) {
        new VoteManager();
    }

}
