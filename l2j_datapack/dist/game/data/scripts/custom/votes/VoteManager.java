package custom.votes;

import ai.SpawnManager;
import ai.npc.AbstractNpcAI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.l2jserver.common.Log;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.NpcLocation;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.multisell.ListContainer;
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

    private Map<Integer, ListContainer> multisells = new ConcurrentHashMap<>();
    private SpawnManager spawnManager = new SpawnManager();
    private VoteConfig config;
    private L2NpcTemplate voteManagerTemplate;

    private VoteManager() {
        super(VoteManager.class.getSimpleName(), "custom/votes");
        load();
    }

    public void load() {
        LOG.info("Loading Vote Manager ...");
        loadConfig();
        loadNpc();
        loadRewardMultisells();
        spawnNpcs();
    }

    private void loadNpc() {
        ShortNpc npc = config.getNpc();
        voteManagerTemplate = new L2NpcTemplate(npc.getId(), npc.getDisplayId(), npc.getName(), npc.getTitle());
        NpcData.getInstance().addNpc(voteManagerTemplate);
    }

    private void loadConfig() {
        String voteConfigFilePath = "./config/vote_config.yml";
        try {
            config = YamlMapper.getInstance().readValue(getClass().getResourceAsStream(voteConfigFilePath), VoteConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load config from " + voteConfigFilePath, e);
        }
    }

    private void spawnNpcs() {
        String spawnlistFilename = "./vote_manager_spawnlist.yml";
        List<Location> spawnlist;
        try {
            spawnlist = YamlMapper.getInstance().readValue(getClass().getResourceAsStream(spawnlistFilename), new TypeReference<Location>() {
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
                            multisells.put(multisell.getListId(), multisell);
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
        config = null;
        return super.unload();
    }

    private void despawnNpcs() {
        spawnManager.decayAll();
    }

    private void unloadRewardMultisells() {
        multisells = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        new VoteManager();
    }

}
