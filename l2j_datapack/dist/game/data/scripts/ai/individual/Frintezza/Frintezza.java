/*
 * Copyright (C) 2004-2016 L2J DataPack
 *
 * This file is part of L2J DataPack.
 *
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.individual.Frintezza;

import ai.GrandBossStatusManager;
import ai.IdleBossManager;
import ai.SpawnManager;
import ai.npc.AbstractNpcAI;
import com.l2jserver.Config;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.L2Territory;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.type.L2NoRestartZone;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import com.l2jserver.gameserver.network.serverpackets.Earthquake;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillCanceld;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.network.serverpackets.SpecialCamera;
import com.l2jserver.gameserver.util.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class Frintezza extends AbstractNpcAI {

    protected class FrintezzaStatus {

        protected Lock lock = new ReentrantLock();
        protected List<L2Npc> npcList = new CopyOnWriteArrayList<>();
        protected int darkChoirPlayerCount = 0;
        protected FrintezzaSong OnSong = null;
        protected ScheduledFuture<?> songTask = null;
        protected ScheduledFuture<?> songEffectTask = null;
        protected boolean isVideo = false;
        protected L2Npc frintezzaDummy = null;
        protected L2Npc overheadDummy = null;
        protected L2Npc portraitDummy1 = null;
        protected L2Npc portraitDummy3 = null;
        protected L2Npc scarletDummy = null;
        protected L2GrandBossInstance frintezza = null;
        protected L2GrandBossInstance activeScarlet = null;
        protected List<L2MonsterInstance> demons = new CopyOnWriteArrayList<>();
        protected Map<L2MonsterInstance, Integer> portraits = new ConcurrentHashMap<>();
        protected int scarlet_x = 0;
        protected int scarlet_y = 0;
        protected int scarlet_z = 0;
        protected int scarlet_h = 0;
        protected int scarlet_a = 0;

        public int getInstanceId() {
            return 0;
        }

        private void setRespawn(long respawnTime) {
            StatsSet statsSet = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
            statsSet.set("respawn_time", (System.currentTimeMillis() + respawnTime));
            GrandBossManager.getInstance().setStatsSet(FRINTEZZA, statsSet);
        }

        private long getRespawnDelay() {
            return (Config.FRINTEZZA_SPAWN_INTERVAL + getRandom(-Config.FRINTEZZA_SPAWN_RANDOM, Config.FRINTEZZA_SPAWN_RANDOM)) * 3600000;
        }

        private int getRespawn() {
            return GrandBossManager.getInstance().getStatsSet(FRINTEZZA).getInt("respawn_time");
        }

    }

    protected static class FETSpawn {

        public boolean isZone = false;
        public boolean isNeededNextFlag = false;
        public int npcId;
        public int x = 0;
        public int y = 0;
        public int z = 0;
        public int h = 0;
        public int zone = 0;
        public int count = 0;
    }

    private static class FrintezzaSong {

        public SkillHolder skill;
        public SkillHolder effectSkill;
        public NpcStringId songName;
        public int chance;

        public FrintezzaSong(SkillHolder sk, SkillHolder esk, NpcStringId sn, int ch) {
            skill = sk;
            effectSkill = esk;
            songName = sn;
            chance = ch;
        }
    }

    // NPCs
    private static final int GUIDE = 32011;
    private static final int CUBE = 29061;
    private static final int SCARLET1 = 29046;
    private static final int SCARLET2 = 29047;
    private static final int FRINTEZZA = 29045;
    private static final int[] PORTRAITS =
        {
            29048,
            29049
        };
    private static final int[] DEMONS =
        {
            29050,
            29051
        };
    private static final int HALL_ALARM = 18328;
    private static final int HALL_KEEPER_CAPTAIN = 18329;
    // Items
    private static final int HALL_KEEPER_SUICIDAL_SOLDIER = 18333;
    private static final int DARK_CHOIR_PLAYER = 18339;
    private static final int[] AI_DISABLED_MOBS =
        {
            18328
        };
    private static final int DEWDROP_OF_DESTRUCTION_ITEM_ID = 8556;
    private static final int FIRST_SCARLET_WEAPON = 8204;
    private static final int SECOND_SCARLET_WEAPON = 7903;
    // Skills
    private static final int DEWDROP_OF_DESTRUCTION_SKILL_ID = 2276;
    private static final int SOUL_BREAKING_ARROW_SKILL_ID = 2234;
    protected static final SkillHolder INTRO_SKILL = new SkillHolder(5004, 1);
    private static final SkillHolder FIRST_MORPH_SKILL = new SkillHolder(5017, 1);

    protected static final FrintezzaSong[] FRINTEZZASONGLIST =
        {
            new FrintezzaSong(new SkillHolder(5007, 1), new SkillHolder(5008, 1), NpcStringId.REQUIEM_OF_HATRED, 5),
            new FrintezzaSong(new SkillHolder(5007, 2), new SkillHolder(5008, 2), NpcStringId.RONDO_OF_SOLITUDE, 50),
            new FrintezzaSong(new SkillHolder(5007, 3), new SkillHolder(5008, 3), NpcStringId.FRENETIC_TOCCATA, 70),
            new FrintezzaSong(new SkillHolder(5007, 4), new SkillHolder(5008, 4), NpcStringId.FUGUE_OF_JUBILATION, 90),
            new FrintezzaSong(new SkillHolder(5007, 5), new SkillHolder(5008, 5), NpcStringId.HYPNOTIC_MAZURKA, 100),
        };

    private static final L2NoRestartZone zone = ZoneManager.getInstance().getZoneById(FRINTEZZA, L2NoRestartZone.class);

    // Locations
    private static final Location ENTER_TELEPORT = new Location(-88015, -141153, -9168);
    private static final Location ALARM_SPAWN_LOCATION = new Location(-87905, -141200, -9168);
    private static final Location EXIT_LOCATION = new Location(-87534, -153048, -9165);
    protected static final Location MOVE_TO_CENTER = new Location(-87904, -141296, -9168, 0);

    private static final int MAX_PLAYERS = 100;
    private static final int TIME_BETWEEN_DEMON_SPAWNS = 20000;
    private static final int MAX_DEMONS = 24;
    private static final boolean debug = false;
    private final Map<Integer, L2Territory> _spawnZoneList = new HashMap<>();
    private final Map<Integer, List<FETSpawn>> _spawnList = new HashMap<>();
    private final List<Integer> _mustKillMobsId = new ArrayList<>();
    protected static final int[] FIRST_ROOM_DOORS =
        {
            17130051,
            17130052,
            17130053,
            17130054,
            17130055,
            17130056,
            17130057,
            17130058
        };
    protected static final int[] SECOND_ROOM_DOORS =
        {
            17130061,
            17130062,
            17130063,
            17130064,
            17130065,
            17130066,
            17130067,
            17130068,
            17130069,
            17130070
        };
    protected static final int[] FIRST_ROUTE_DOORS =
        {
            17130042,
            17130043
        };
    protected static final int[] SECOND_ROUTE_DOORS =
        {
            17130045,
            17130046
        };

    protected static final int[][] PORTRAIT_SPAWNS =
        {
            {29048, -89381, -153981, -9168, 3368, -89378, -153968, -9168, 3368},
            {29048, -86234, -152467, -9168, 37656, -86261, -152492, -9168, 37656},
            {29049, -89342, -152479, -9168, -5152, -89311, -152491, -9168, -5152},
            {29049, -86189, -153968, -9168, 29456, -86217, -153956, -9168, 29456},
        };

    private final FrintezzaStatus state;
    private final GrandBossStatusManager grandBossStatusManager;
    private final IdleBossManager idleBossManager;
    private final SpawnManager spawnManager;

    public Frintezza() {
        super(Frintezza.class.getSimpleName(), "ai/individual");
        state = new FrintezzaStatus();
        grandBossStatusManager = new GrandBossStatusManager(FRINTEZZA, FrintezzaStatuses.asList());
        idleBossManager = new IdleBossManager(this, Config.FRINTEZZA_RESET_TIMEOUT, this::clearDungeon);
        spawnManager = new SpawnManager();

        load();
        addAttackId(SCARLET1, FRINTEZZA);
        addAttackId(PORTRAITS);
        addStartNpc(GUIDE, CUBE);
        addTalkId(GUIDE, CUBE);
        addKillId(HALL_ALARM, HALL_KEEPER_CAPTAIN, DARK_CHOIR_PLAYER, SCARLET2);
        addKillId(PORTRAITS);
        addKillId(DEMONS);
        addKillId(_mustKillMobsId);
        addSpellFinishedId(HALL_KEEPER_SUICIDAL_SOLDIER);

        if (!grandBossStatusManager.getStatus().equals(FrintezzaStatuses.DEAD)) {
            startDungeon();
        } else {
            final long remain = state.getRespawn() - System.currentTimeMillis();
            if (remain > 0) {
                startQuestTimer("RESPAWN", remain, null, null);
            } else {
                startDungeon();
            }
        }
    }

    private void startDungeon() {
        grandBossStatusManager.setStatus(FrintezzaStatuses.ALIVE);
        controlStatus();
    }

    private void clearDungeon() {

    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
        switch (event) {
            case "RESPAWN": {
                startDungeon();
                break;
            }
        }

        return super.onAdvEvent(event, npc, player);
    }

    private void load() {
        @SuppressWarnings("unused")
        int spawnCount = 0;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            File file = new File(Config.DATAPACK_ROOT + "/data/spawnZones/final_imperial_tomb.xml");
            if (!file.exists()) {
                _log.severe("[Final Imperial Tomb] Missing final_imperial_tomb.xml. The quest wont work without it!");
                return;
            }

            Document doc = factory.newDocumentBuilder().parse(file);
            Node first = doc.getFirstChild();
            if ((first != null) && "list".equalsIgnoreCase(first.getNodeName())) {
                for (Node n = first.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if ("npc".equalsIgnoreCase(n.getNodeName())) {
                        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                            if ("spawn".equalsIgnoreCase(d.getNodeName())) {
                                NamedNodeMap attrs = d.getAttributes();
                                Node att = attrs.getNamedItem("npcId");
                                if (att == null) {
                                    _log.severe("[Final Imperial Tomb] Missing npcId in npc List, skipping");
                                    continue;
                                }
                                int npcId = Integer.parseInt(attrs.getNamedItem("npcId").getNodeValue());

                                att = attrs.getNamedItem("flag");
                                if (att == null) {
                                    _log.severe("[Final Imperial Tomb] Missing flag in npc List npcId: " + npcId
                                        + ", skipping");
                                    continue;
                                }

                                int flag = Integer.parseInt(attrs.getNamedItem("flag").getNodeValue());
                                _spawnList.putIfAbsent(flag, new ArrayList<>());

                                for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                                    if ("loc".equalsIgnoreCase(cd.getNodeName())) {
                                        attrs = cd.getAttributes();
                                        FETSpawn spw = new FETSpawn();
                                        spw.npcId = npcId;

                                        att = attrs.getNamedItem("x");
                                        if (att != null) {
                                            spw.x = Integer.parseInt(att.getNodeValue());
                                        } else {
                                            continue;
                                        }
                                        att = attrs.getNamedItem("y");
                                        if (att != null) {
                                            spw.y = Integer.parseInt(att.getNodeValue());
                                        } else {
                                            continue;
                                        }
                                        att = attrs.getNamedItem("z");
                                        if (att != null) {
                                            spw.z = Integer.parseInt(att.getNodeValue());
                                        } else {
                                            continue;
                                        }
                                        att = attrs.getNamedItem("heading");
                                        if (att != null) {
                                            spw.h = Integer.parseInt(att.getNodeValue());
                                        } else {
                                            continue;
                                        }
                                        att = attrs.getNamedItem("mustKill");
                                        if (att != null) {
                                            spw.isNeededNextFlag = Boolean.parseBoolean(att.getNodeValue());
                                        }
                                        if (spw.isNeededNextFlag) {
                                            _mustKillMobsId.add(npcId);
                                        }
                                        _spawnList.get(flag).add(spw);
                                        spawnCount++;
                                    } else if ("zone".equalsIgnoreCase(cd.getNodeName())) {
                                        attrs = cd.getAttributes();
                                        FETSpawn spw = new FETSpawn();
                                        spw.npcId = npcId;
                                        spw.isZone = true;

                                        att = attrs.getNamedItem("id");
                                        if (att != null) {
                                            spw.zone = Integer.parseInt(att.getNodeValue());
                                        } else {
                                            continue;
                                        }
                                        att = attrs.getNamedItem("count");
                                        if (att != null) {
                                            spw.count = Integer.parseInt(att.getNodeValue());
                                        } else {
                                            continue;
                                        }
                                        att = attrs.getNamedItem("mustKill");
                                        if (att != null) {
                                            spw.isNeededNextFlag = Boolean.parseBoolean(att.getNodeValue());
                                        }
                                        if (spw.isNeededNextFlag) {
                                            _mustKillMobsId.add(npcId);
                                        }
                                        _spawnList.get(flag).add(spw);
                                        spawnCount++;
                                    }
                                }
                            }
                        }
                    } else if ("spawnZones".equalsIgnoreCase(n.getNodeName())) {
                        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                            if ("zone".equalsIgnoreCase(d.getNodeName())) {
                                NamedNodeMap attrs = d.getAttributes();
                                Node att = attrs.getNamedItem("id");
                                if (att == null) {
                                    _log.severe("[Final Imperial Tomb] Missing id in spawnZones List, skipping");
                                    continue;
                                }
                                int id = Integer.parseInt(att.getNodeValue());
                                att = attrs.getNamedItem("minZ");
                                if (att == null) {
                                    _log.severe("[Final Imperial Tomb] Missing minZ in spawnZones List id: " + id
                                        + ", skipping");
                                    continue;
                                }
                                int minz = Integer.parseInt(att.getNodeValue());
                                att = attrs.getNamedItem("maxZ");
                                if (att == null) {
                                    _log.severe("[Final Imperial Tomb] Missing maxZ in spawnZones List id: " + id
                                        + ", skipping");
                                    continue;
                                }
                                int maxz = Integer.parseInt(att.getNodeValue());
                                L2Territory ter = new L2Territory(id);

                                for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                                    if ("point".equalsIgnoreCase(cd.getNodeName())) {
                                        attrs = cd.getAttributes();
                                        int x, y;
                                        att = attrs.getNamedItem("x");
                                        if (att != null) {
                                            x = Integer.parseInt(att.getNodeValue());
                                        } else {
                                            continue;
                                        }
                                        att = attrs.getNamedItem("y");
                                        if (att != null) {
                                            y = Integer.parseInt(att.getNodeValue());
                                        } else {
                                            continue;
                                        }

                                        ter.add(x, y, minz, maxz, 0);
                                    }
                                }

                                _spawnZoneList.put(id, ter);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.log(Level.WARNING,
                "[Final Imperial Tomb] Could not parse final_imperial_tomb.xml file: " + e.getMessage(), e);
        }
        if (debug) {
            _log.info("[Final Imperial Tomb] Loaded " + _spawnZoneList.size() + " spawn zones data.");
            _log.info("[Final Imperial Tomb] Loaded " + spawnCount + " spawns data.");
        }
    }

    public List<L2PcInstance> getPlayersInside() {
        return zone.getPlayersInside();
    }

    protected boolean checkConditions(L2PcInstance player) {
        if (debug || player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS)) {
            return true;
        }

        return zone.getPlayersInside().size() <= MAX_PLAYERS;
    }

    public void enterZone(L2PcInstance player) {
        if (checkConditions(player)) {
            player.destroyItemByItemId(getName(), DEWDROP_OF_DESTRUCTION_ITEM_ID,
                player.getInventory().getInventoryItemCount(DEWDROP_OF_DESTRUCTION_ITEM_ID, -1), null, true);
            teleportPlayer(player, ENTER_TELEPORT, state.getInstanceId(), false);
        } else {
            _log.fine("Player " + player + " cannot enter Frintezza's realm");
        }
    }

    protected boolean killProgress(L2Npc mob) {
        state.npcList.remove(mob);
        return state.npcList.isEmpty();
    }

    private void spawnFlaggedNPCs(int flag) {
        if (state.lock.tryLock()) {
            try {
                for (FETSpawn spw : _spawnList.get(flag)) {
                    if (spw.isZone) {
                        for (int i = 0; i < spw.count; i++) {
                            if (_spawnZoneList.containsKey(spw.zone)) {
                                final Location location = _spawnZoneList.get(spw.zone).getRandomPoint();
                                if (location != null) {
                                    spawn(spw.npcId, location.getX(), location.getY(),
                                        GeoData.getInstance().getSpawnHeight(location), getRandom(65535),
                                        spw.isNeededNextFlag);
                                }
                            } else {
                                _log.info("[Final Imperial Tomb] Missing zone: " + spw.zone);
                            }
                        }
                    } else {
                        spawn(spw.npcId, spw.x, spw.y, spw.z, spw.h, spw.isNeededNextFlag);
                    }
                }
            } finally {
                state.lock.unlock();
            }
        }
    }

    protected boolean controlStatus() {
        if (state.lock.tryLock()) {
            try {
                if (debug) {
                    _log.info("[Final Imperial Tomb] Starting " + grandBossStatusManager.getStatus() + ". status.");
                }
                state.npcList.clear();

                FrintezzaStatuses bossStatus = (FrintezzaStatuses) grandBossStatusManager.getStatus();
                switch (bossStatus) {
                    case ALIVE:
                        spawnFlaggedNPCs(0);
                        break;
                    case FIRST_ROOM:
                        for (int doorId : FIRST_ROUTE_DOORS) {
                            openDoor(doorId, state.getInstanceId());
                        }
                        spawnFlaggedNPCs(grandBossStatusManager.getStatus().getId());
                        break;
                    case SECOND_ROOM:
                        for (int doorId : SECOND_ROUTE_DOORS) {
                            openDoor(doorId, state.getInstanceId());
                        }
                        ThreadPoolManager.getInstance()
                            .scheduleGeneral(new IntroTask(state, 0), Config.FRINTEZZA_FIGHT_DELAY * 60000);
                        break;
                    case REGULAR_HALISHA: // first morph
                        if (state.songEffectTask != null) {
                            state.songEffectTask.cancel(false);
                        }
                        state.songEffectTask = null;
                        state.activeScarlet.setIsInvul(true);
                        if (state.activeScarlet.isCastingNow()) {
                            state.activeScarlet.abortCast();
                        }
                        state.activeScarlet.doCast(FIRST_MORPH_SKILL.getSkill());
                        ThreadPoolManager.getInstance().scheduleGeneral(new SongTask(state, 2), 1500);
                        break;
                    case FOUR_LEG_HALISHA: // second morph
                        state.isVideo = true;
                        broadCastPacket(new MagicSkillCanceld(state.frintezza.getObjectId()));
                        if (state.songEffectTask != null) {
                            state.songEffectTask.cancel(false);
                        }
                        state.songEffectTask = null;
                        ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 23), 2000);
                        ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 24), 2100);
                        break;
                    case FINISH: // raid success
                        state.isVideo = true;
                        broadCastPacket(new MagicSkillCanceld(state.frintezza.getObjectId()));
                        if (state.songTask != null) {
                            state.songTask.cancel(true);
                        }
                        if (state.songEffectTask != null) {
                            state.songEffectTask.cancel(false);
                        }
                        state.songTask = null;
                        state.songEffectTask = null;


                        long respawnDelay = state.getRespawnDelay();
                        state.setRespawn(respawnDelay);
                        startQuestTimer("RESPAWN", respawnDelay, null, null);

                        ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 33), 500);
                        break;
                    case DEAD: // open doors
                        for (int doorId : FIRST_ROOM_DOORS) {
                            openDoor(doorId, state.getInstanceId());
                        }
                        for (int doorId : FIRST_ROUTE_DOORS) {
                            openDoor(doorId, state.getInstanceId());
                        }
                        for (int doorId : SECOND_ROUTE_DOORS) {
                            openDoor(doorId, state.getInstanceId());
                        }
                        for (int doorId : SECOND_ROOM_DOORS) {
                            closeDoor(doorId, state.getInstanceId());
                        }

                        //TODO teleport out in 10 minutes
                        break;
                }
                grandBossStatusManager.transitionToNextStatus();
                return true;
            } finally {
                state.lock.unlock();
            }
        }
        return false;
    }

    protected void spawn(int npcId, int x, int y, int z, int h, boolean addToKillTable) {
        final L2Npc npc = addSpawn(npcId, x, y, z, h, false, 0, false, state.getInstanceId());
        if (addToKillTable) {
            state.npcList.add(npc);
        }
        npc.setIsNoRndWalk(true);
        if (npc.isInstanceTypes(InstanceType.L2Attackable)) {
            ((L2Attackable) npc).setSeeThroughSilentMove(true);
        }
        if (Util.contains(AI_DISABLED_MOBS, npcId)) {
            npc.disableCoreAI(true);
        }
        if (npcId == DARK_CHOIR_PLAYER) {
            state.darkChoirPlayerCount++;
        }
    }

    private class DemonSpawnTask implements Runnable {

        private final FrintezzaStatus _world;

        DemonSpawnTask(FrintezzaStatus world) {
            _world = world;
        }

        @Override
        public void run() {
            if (state.portraits.isEmpty()) {
                if (debug) {
                    _log.info("[Final Imperial Tomb] Instance all Portraits are killed.");
                }
                return;
            }
            for (int i : state.portraits.values()) {
                if (state.demons.size() > MAX_DEMONS) {
                    break;
                }
                L2MonsterInstance demon = (L2MonsterInstance) addSpawn(PORTRAIT_SPAWNS[i][0] + 2, PORTRAIT_SPAWNS[i][5],
                    PORTRAIT_SPAWNS[i][6], PORTRAIT_SPAWNS[i][7], PORTRAIT_SPAWNS[i][8], false, 0, false,
                    state.getInstanceId());
                updateKnownList(demon);
                state.demons.add(demon);
            }
            ThreadPoolManager.getInstance().scheduleGeneral(new DemonSpawnTask(_world), TIME_BETWEEN_DEMON_SPAWNS);
        }
    }

    private class SoulBreakingArrow implements Runnable {

        private final L2Npc _npc;

        protected SoulBreakingArrow(L2Npc npc) {
            _npc = npc;
        }

        @Override
        public void run() {
            _npc.setScriptValue(0);
        }
    }

    private class SongTask implements Runnable {

        private final FrintezzaStatus _world;
        private final int _status;

        SongTask(FrintezzaStatus world, int status) {
            _world = world;
            _status = status;
        }

        @Override
        public void run() {
            switch (_status) {
                case 0: // new song play
                    if (state.isVideo) {
                        state.songTask = ThreadPoolManager.getInstance()
                            .scheduleGeneral(new SongTask(_world, 0), 1000);
                    } else if ((state.frintezza != null) && !state.frintezza.isDead()) {
                        if (state.frintezza.getScriptValue() != 1) {
                            int rnd = getRandom(100);
                            for (FrintezzaSong element : FRINTEZZASONGLIST) {
                                if (rnd < element.chance) {
                                    state.OnSong = element;
                                    broadCastPacket(
                                        new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 4000, false, null,
                                            element.songName, null));
                                    broadCastPacket(new MagicSkillUse(state.frintezza, state.frintezza,
                                        element.skill.getSkillId(), element.skill.getSkillLvl(),
                                        element.skill.getSkill().getHitTime(), 0));
                                    state.songEffectTask = ThreadPoolManager.getInstance()
                                        .scheduleGeneral(new SongTask(_world, 1),
                                            element.skill.getSkill().getHitTime() - 10000);
                                    state.songTask = ThreadPoolManager.getInstance()
                                        .scheduleGeneral(new SongTask(_world, 0),
                                            element.skill.getSkill().getHitTime());
                                    break;
                                }
                            }
                        } else {
                            ThreadPoolManager.getInstance()
                                .scheduleGeneral(new SoulBreakingArrow(state.frintezza), 35000);
                        }
                    }
                    break;
                case 1: // Frintezza song effect
                    state.songEffectTask = null;
                    Skill skill = state.OnSong.effectSkill.getSkill();
                    if (skill == null) {
                        return;
                    }

                    if ((state.frintezza != null) && !state.frintezza.isDead() && (state.activeScarlet != null)
                        && !state.activeScarlet.isDead()) {
                        final List<L2Character> targetList = new ArrayList<>();
                        if (skill.hasEffectType(L2EffectType.STUN) || skill.isDebuff()) {
                            for (L2PcInstance player : getPlayersInside()) {
                                if ((player != null) && player.isOnline() && (player.getInstanceId() == _world
                                    .getInstanceId())) {
                                    if (!player.isDead()) {
                                        targetList.add(player);
                                    }
                                    if (player.hasSummon() && !player.getSummon().isDead()) {
                                        targetList.add(player.getSummon());
                                    }
                                }
                            }
                        } else {
                            targetList.add(state.activeScarlet);
                        }
                        if (!targetList.isEmpty()) {
                            state.frintezza.doCast(skill, targetList.get(0),
                                targetList.toArray(new L2Character[targetList.size()]));
                        }
                    }
                    break;
                case 2: // finish morph
                    state.activeScarlet.setRHandId(SECOND_SCARLET_WEAPON);
                    state.activeScarlet.setIsInvul(false);
                    break;
            }
        }
    }

    private class IntroTask implements Runnable {

        private final FrintezzaStatus state;
        private final int _status;

        IntroTask(FrintezzaStatus state, int status) {
            this.state = state;
            _status = status;
        }

        @Override
        public void run() {
            switch (_status) {
                case 0:
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 1), 27000);
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 2), 30000);
                    broadCastPacket(new Earthquake(-87784, -155083, -9087, 45, 27));
                    break;
                case 1:
                    for (int doorId : FIRST_ROOM_DOORS) {
                        closeDoor(doorId, state.getInstanceId());
                    }
                    for (int doorId : FIRST_ROUTE_DOORS) {
                        closeDoor(doorId, state.getInstanceId());
                    }
                    for (int doorId : SECOND_ROOM_DOORS) {
                        closeDoor(doorId, state.getInstanceId());
                    }
                    for (int doorId : SECOND_ROUTE_DOORS) {
                        closeDoor(doorId, state.getInstanceId());
                    }
                    addSpawn(CUBE, -87904, -141296, -9168, 0, false, 0, false, state.getInstanceId());
                    break;
                case 2:
                    state.frintezzaDummy = addSpawn(29052, -87784, -155083, -9087, 16048, false, 0, false,
                        state.getInstanceId());
                    state.frintezzaDummy.setIsInvul(true);
                    state.frintezzaDummy.setIsImmobilized(true);

                    state.overheadDummy = addSpawn(29052, -87784, -153298, -9175, 16384, false, 0, false,
                        state.getInstanceId());
                    state.overheadDummy.setIsInvul(true);
                    state.overheadDummy.setIsImmobilized(true);
                    state.overheadDummy.setCollisionHeight(600);
                    broadCastPacket(new NpcInfo(state.overheadDummy, null));

                    state.portraitDummy1 = addSpawn(29052, -89566, -153168, -9165, 16048, false, 0, false,
                        state.getInstanceId());
                    state.portraitDummy1.setIsImmobilized(true);
                    state.portraitDummy1.setIsInvul(true);

                    state.portraitDummy3 = addSpawn(29052, -86004, -153168, -9165, 16048, false, 0, false,
                        state.getInstanceId());
                    state.portraitDummy3.setIsImmobilized(true);
                    state.portraitDummy3.setIsInvul(true);

                    state.scarletDummy = addSpawn(29053, -87784, -153298, -9175, 16384, false, 0, false,
                        state.getInstanceId());
                    state.scarletDummy.setIsInvul(true);
                    state.scarletDummy.setIsImmobilized(true);

                    stopPc();
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 3), 1000);
                    break;
                case 3:
                    broadCastPacket(new SpecialCamera(state.overheadDummy, 0, 75, -89, 0, 100, 0, 0, 1, 0, 0));
                    broadCastPacket(new SpecialCamera(state.overheadDummy, 0, 75, -89, 0, 100, 0, 0, 1, 0, 0));
                    broadCastPacket(
                        new SpecialCamera(state.overheadDummy, 300, 90, -10, 6500, 7000, 0, 0, 1, 0, 0));

                    state.frintezza = (L2GrandBossInstance) addSpawn(FRINTEZZA, -87780, -155086, -9080, 16384, false,
                        0, false, state.getInstanceId());
                    state.frintezza.setIsImmobilized(true);
                    state.frintezza.setIsInvul(true);
                    state.frintezza.disableAllSkills();
                    updateKnownList(state.frintezza);

                    for (int[] element : PORTRAIT_SPAWNS) {
                        L2MonsterInstance demon = (L2MonsterInstance) addSpawn(element[0] + 2, element[5], element[6],
                            element[7], element[8], false, 0, false, state.getInstanceId());
                        demon.setIsImmobilized(true);
                        demon.disableAllSkills();
                        updateKnownList(demon);
                        state.demons.add(demon);
                    }
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 4), 6500);
                    break;
                case 4:
                    broadCastPacket(
                        new SpecialCamera(state.frintezzaDummy, 1800, 90, 8, 6500, 7000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 5), 900);
                    break;
                case 5:
                    broadCastPacket(
                        new SpecialCamera(state.frintezzaDummy, 140, 90, 10, 2500, 4500, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 6), 4000);
                    break;
                case 6:
                    broadCastPacket(new SpecialCamera(state.frintezza, 40, 75, -10, 0, 1000, 0, 0, 1, 0, 0));
                    broadCastPacket(new SpecialCamera(state.frintezza, 40, 75, -10, 0, 12000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 7), 1350);
                    break;
                case 7:
                    broadCastPacket(new SocialAction(state.frintezza.getObjectId(), 2));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 8), 7000);
                    break;
                case 8:
                    state.frintezzaDummy.deleteMe();
                    state.frintezzaDummy = null;
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 9), 1000);
                    break;
                case 9:
                    broadCastPacket(new SocialAction(state.demons.get(1).getObjectId(), 1));
                    broadCastPacket(new SocialAction(state.demons.get(2).getObjectId(), 1));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 10), 400);
                    break;
                case 10:
                    broadCastPacket(new SocialAction(state.demons.get(0).getObjectId(), 1));
                    broadCastPacket(new SocialAction(state.demons.get(3).getObjectId(), 1));
                    sendPacketX(new SpecialCamera(state.portraitDummy1, 1000, 118, 0, 0, 1000, 0, 0, 1, 0, 0),
                        new SpecialCamera(state.portraitDummy3, 1000, 62, 0, 0, 1000, 0, 0, 1, 0, 0), -87784);
                    sendPacketX(new SpecialCamera(state.portraitDummy1, 1000, 118, 0, 0, 10000, 0, 0, 1, 0, 0),
                        new SpecialCamera(state.portraitDummy3, 1000, 62, 0, 0, 10000, 0, 0, 1, 0, 0), -87784);
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 11), 2000);
                    break;
                case 11:
                    broadCastPacket(new SpecialCamera(state.frintezza, 240, 90, 0, 0, 1000, 0, 0, 1, 0, 0));
                    broadCastPacket(
                        new SpecialCamera(state.frintezza, 240, 90, 25, 5500, 10000, 0, 0, 1, 0, 0));
                    broadCastPacket(new SocialAction(state.frintezza.getObjectId(), 3));
                    state.portraitDummy1.deleteMe();
                    state.portraitDummy3.deleteMe();
                    state.portraitDummy1 = null;
                    state.portraitDummy3 = null;
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 12), 4500);
                    break;
                case 12:
                    broadCastPacket(new SpecialCamera(state.frintezza, 100, 195, 35, 0, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 13), 700);
                    break;
                case 13:
                    broadCastPacket(new SpecialCamera(state.frintezza, 100, 195, 35, 0, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 14), 1300);
                    break;
                case 14:
                    broadCastPacket(new ExShowScreenMessage(NpcStringId.MOURNFUL_CHORALE_PRELUDE, 2, 5000));
                    broadCastPacket(
                        new SpecialCamera(state.frintezza, 120, 180, 45, 1500, 10000, 0, 0, 1, 0, 0));
                    broadCastPacket(new MagicSkillUse(state.frintezza, state.frintezza, 5006, 1, 34000, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 15), 1500);
                    break;
                case 15:
                    broadCastPacket(
                        new SpecialCamera(state.frintezza, 520, 135, 45, 8000, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 16), 7500);
                    break;
                case 16:
                    broadCastPacket(
                        new SpecialCamera(state.frintezza, 1500, 110, 25, 10000, 13000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 17), 9500);
                    break;
                case 17:
                    broadCastPacket(
                        new SpecialCamera(state.overheadDummy, 930, 160, -20, 0, 1000, 0, 0, 1, 0, 0));
                    broadCastPacket(
                        new SpecialCamera(state.overheadDummy, 600, 180, -25, 0, 10000, 0, 0, 1, 0, 0));
                    broadCastPacket(
                        new MagicSkillUse(state.scarletDummy, state.overheadDummy, 5004, 1, 5800, 0));

                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 18), 5000);
                    break;
                case 18:
                    state.activeScarlet = (L2GrandBossInstance) addSpawn(29046, -87789, -153295, -9176, 16384, false,
                        0, false, state.getInstanceId());
                    state.activeScarlet.setRHandId(FIRST_SCARLET_WEAPON);
                    state.activeScarlet.setIsInvul(true);
                    state.activeScarlet.setIsImmobilized(true);
                    state.activeScarlet.disableAllSkills();
                    updateKnownList(state.activeScarlet);
                    broadCastPacket(new SocialAction(state.activeScarlet.getObjectId(), 3));
                    broadCastPacket(
                        new SpecialCamera(state.scarletDummy, 800, 180, 10, 1000, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 19), 2100);
                    break;
                case 19:
                    broadCastPacket(
                        new SpecialCamera(state.activeScarlet, 300, 60, 8, 0, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 20), 2000);
                    break;
                case 20:
                    broadCastPacket(
                        new SpecialCamera(state.activeScarlet, 500, 90, 10, 3000, 5000, 0, 0, 1, 0, 0));
                    state.songTask = ThreadPoolManager.getInstance().scheduleGeneral(new SongTask(state, 0), 100);
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 21), 3000);
                    break;
                case 21:
                    for (int i = 0; i < PORTRAIT_SPAWNS.length; i++) {
                        L2MonsterInstance portrait = (L2MonsterInstance) addSpawn(PORTRAIT_SPAWNS[i][0],
                            PORTRAIT_SPAWNS[i][1], PORTRAIT_SPAWNS[i][2], PORTRAIT_SPAWNS[i][3], PORTRAIT_SPAWNS[i][4],
                            false, 0, false, state.getInstanceId());
                        updateKnownList(portrait);
                        state.portraits.put(portrait, i);
                    }

                    state.overheadDummy.deleteMe();
                    state.scarletDummy.deleteMe();
                    state.overheadDummy = null;
                    state.scarletDummy = null;

                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 22), 2000);
                    break;
                case 22:
                    for (L2MonsterInstance demon : state.demons) {
                        demon.setIsImmobilized(false);
                        demon.enableAllSkills();
                    }
                    state.activeScarlet.setIsInvul(false);
                    state.activeScarlet.setIsImmobilized(false);
                    state.activeScarlet.enableAllSkills();
                    state.activeScarlet.setRunning();
                    state.activeScarlet.doCast(INTRO_SKILL.getSkill());
                    state.frintezza.enableAllSkills();
                    state.frintezza.disableCoreAI(true);
                    state.frintezza.setIsMortal(false);
                    startPc();

                    ThreadPoolManager.getInstance()
                        .scheduleGeneral(new DemonSpawnTask(state), TIME_BETWEEN_DEMON_SPAWNS);
                    break;
                case 23:
                    broadCastPacket(new SocialAction(state.frintezza.getObjectId(), 4));
                    break;
                case 24:
                    stopPc();
                    broadCastPacket(new SpecialCamera(state.frintezza, 250, 120, 15, 0, 1000, 0, 0, 1, 0, 0));
                    broadCastPacket(new SpecialCamera(state.frintezza, 250, 120, 15, 0, 10000, 0, 0, 1, 0, 0));
                    state.activeScarlet.abortAttack();
                    state.activeScarlet.abortCast();
                    state.activeScarlet.setIsInvul(true);
                    state.activeScarlet.setIsImmobilized(true);
                    state.activeScarlet.disableAllSkills();
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 25), 7000);
                    break;
                case 25:
                    broadCastPacket(new MagicSkillUse(state.frintezza, state.frintezza, 5006, 1, 34000, 0));
                    broadCastPacket(
                        new SpecialCamera(state.frintezza, 500, 70, 15, 3000, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 26), 3000);
                    break;
                case 26:
                    broadCastPacket(
                        new SpecialCamera(state.frintezza, 2500, 90, 12, 6000, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 27), 3000);
                    break;
                case 27:
                    state.scarlet_x = state.activeScarlet.getX();
                    state.scarlet_y = state.activeScarlet.getY();
                    state.scarlet_z = state.activeScarlet.getZ();
                    state.scarlet_h = state.activeScarlet.getHeading();
                    if (state.scarlet_h < 32768) {
                        state.scarlet_a = Math.abs(180 - (int) (state.scarlet_h / 182.044444444));
                    } else {
                        state.scarlet_a = Math.abs(540 - (int) (state.scarlet_h / 182.044444444));
                    }
                    broadCastPacket(
                        new SpecialCamera(state.activeScarlet, 250, state.scarlet_a, 12, 0, 1000, 0, 0, 1, 0, 0));
                    broadCastPacket(
                        new SpecialCamera(state.activeScarlet, 250, state.scarlet_a, 12, 0, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 28), 500);
                    break;
                case 28:
                    state.activeScarlet.doDie(state.activeScarlet);
                    broadCastPacket(
                        new SpecialCamera(state.activeScarlet, 450, state.scarlet_a, 14, 8000, 8000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 29), 6250);
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 30), 7200);
                    break;
                case 29:
                    state.activeScarlet.deleteMe();
                    state.activeScarlet = null;
                    break;
                case 30:
                    state.activeScarlet = (L2GrandBossInstance) addSpawn(SCARLET2, state.scarlet_x, state.scarlet_y,
                        state.scarlet_z, state.scarlet_h, false, 0, false, Frintezza.this.state
                            .getInstanceId());
                    state.activeScarlet.setIsInvul(true);
                    state.activeScarlet.setIsImmobilized(true);
                    state.activeScarlet.disableAllSkills();
                    updateKnownList(state.activeScarlet);

                    broadCastPacket(
                        new SpecialCamera(state.activeScarlet, 450, state.scarlet_a, 12, 500, 14000, 0, 0, 1, 0, 0));

                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 31), 8100);
                    break;
                case 31:
                    broadCastPacket(new SocialAction(state.activeScarlet.getObjectId(), 2));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 32), 9000);
                    break;
                case 32:
                    startPc();
                    state.activeScarlet.setIsInvul(false);
                    state.activeScarlet.setIsImmobilized(false);
                    state.activeScarlet.enableAllSkills();
                    state.isVideo = false;
                    break;
                case 33:
                    broadCastPacket(
                        new SpecialCamera(state.activeScarlet, 300, state.scarlet_a - 180, 5, 0, 7000, 0, 0, 1, 0,
                            0));
                    broadCastPacket(
                        new SpecialCamera(state.activeScarlet, 200, state.scarlet_a, 85, 4000, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 34), 7400);
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 35), 7500);
                    break;
                case 34:
                    state.frintezza.doDie(state.frintezza);
                    break;
                case 35:
                    broadCastPacket(new SpecialCamera(state.frintezza, 100, 120, 5, 0, 7000, 0, 0, 1, 0, 0));
                    broadCastPacket(
                        new SpecialCamera(state.frintezza, 100, 90, 5, 5000, 15000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 36), 7000);
                    break;
                case 36:
                    broadCastPacket(
                        new SpecialCamera(state.frintezza, 900, 90, 25, 7000, 10000, 0, 0, 1, 0, 0));
                    ThreadPoolManager.getInstance().scheduleGeneral(new IntroTask(state, 37), 9000);
                    break;
                case 37:
                    controlStatus();
                    state.isVideo = false;
                    startPc();
                    break;
            }
        }

        private void stopPc() {
            for (L2PcInstance player : getPlayersInside()) {
                if ((player != null) && player.isOnline()) {
                    player.abortAttack();
                    player.abortCast();
                    player.disableAllSkills();
                    player.setTarget(null);
                    player.stopMove(null);
                    player.setIsImmobilized(true);
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                }
            }
        }

        private void startPc() {
            for (L2PcInstance player : getPlayersInside()) {
                if ((player != null) && player.isOnline()) {
                    player.enableAllSkills();
                    player.setIsImmobilized(false);
                }
            }
        }

        private void sendPacketX(L2GameServerPacket packet1, L2GameServerPacket packet2, int x) {
            for (L2PcInstance player : getPlayersInside()) {
                if ((player != null) && player.isOnline()) {
                    if (player.getX() < x) {
                        player.sendPacket(packet1);
                    } else {
                        player.sendPacket(packet2);
                    }
                }
            }
        }
    }

    private class StatusTask implements Runnable {

        private final int _status;

        StatusTask(int status) {
            _status = status;
        }

        @Override
        public void run() {
            switch (_status) {
                case 0:
                    ThreadPoolManager.getInstance().scheduleGeneral(new StatusTask(1), 2000);
                    for (int doorId : FIRST_ROOM_DOORS) {
                        openDoor(doorId, state.getInstanceId());
                    }
                    break;
                case 1:
                    addAggroToMobs();
                    break;
                case 2:
                    ThreadPoolManager.getInstance().scheduleGeneral(new StatusTask(3), 100);
                    for (int doorId : SECOND_ROOM_DOORS) {
                        openDoor(doorId, state.getInstanceId());
                    }
                    break;
                case 3:
                    addAggroToMobs();
                    break;
                case 4:
                    controlStatus();
                    break;
            }
        }

        private void addAggroToMobs() {
            L2PcInstance target = getPlayersInside().get(getRandom(getPlayersInside().size()));
            if ((target == null) || target.isDead() || target.isFakeDeath()) {
                for (L2PcInstance player : getPlayersInside()) {
                    target = player;
                    if ((target != null) && (target.getInstanceId() == state.getInstanceId()) && !target.isDead()
                        && !target.isFakeDeath()) {
                        break;
                    }
                    target = null;
                }
            }
            for (L2Npc mob : state.npcList) {
                mob.setRunning();
                if (target != null) {
                    ((L2MonsterInstance) mob).addDamageHate(target, 0, 500);
                    mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
                } else {
                    mob.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, MOVE_TO_CENTER);
                }
            }
        }
    }

    protected void broadCastPacket(L2GameServerPacket packet) {
        for (L2PcInstance player : getPlayersInside()) {
            if ((player != null) && player.isOnline()) {
                player.sendPacket(packet);
            }
        }
    }

    protected void updateKnownList(L2Npc npc) {
        Map<Integer, L2PcInstance> npcKnownPlayers = npc.getKnownList().getKnownPlayers();
        for (L2PcInstance player : getPlayersInside()) {
            if ((player != null) && player.isOnline()) {
                npcKnownPlayers.put(player.getObjectId(), player);
            }
        }
    }

    @Override
    public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill) {
        if ((npc.getId() == SCARLET1) && grandBossStatusManager.isStatus(FrintezzaStatuses.REGULAR_HALISHA) && (
            npc.getCurrentHp() < (npc.getMaxHp() * 0.80))) {
            controlStatus();
        } else if ((npc.getId() == SCARLET1) && (grandBossStatusManager.isStatus(FrintezzaStatuses.FOUR_LEG_HALISHA))
            && (npc.getCurrentHp() < (npc.getMaxHp() * 0.20))) {
            controlStatus();
        }
        if (skill != null) {
            // When Dewdrop of Destruction is used on Portraits they suicide.
            if (Util.contains(PORTRAITS, npc.getId()) && (skill.getId() == DEWDROP_OF_DESTRUCTION_SKILL_ID)) {
                npc.doDie(attacker);
            } else if ((npc.getId() == FRINTEZZA) && (skill.getId() == SOUL_BREAKING_ARROW_SKILL_ID)) {
                npc.setScriptValue(1);
                npc.setTarget(null);
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            }
        }
        return null;
    }

    @Override
    public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill) {
        if (skill.isSuicideAttack()) {
            return onKill(npc, null, false);
        }
        return super.onSpellFinished(npc, player, skill);
    }

    @Override
    public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
        if (npc.getId() == HALL_ALARM) {
            ThreadPoolManager.getInstance().scheduleGeneral(new StatusTask(0), 2000);
            if (debug) {
                _log.info("[Final Imperial Tomb] Hall alarm is disabled, doors will open!");
            }
        } else if (npc.getId() == DARK_CHOIR_PLAYER) {
            state.darkChoirPlayerCount--;
            if (state.darkChoirPlayerCount < 1) {
                ThreadPoolManager.getInstance().scheduleGeneral(new StatusTask(2), 2000);
                if (debug) {
                    _log.info("[Final Imperial Tomb] All Dark Choir Players are killed, doors will open!");
                }
            }
        } else if (npc.getId() == SCARLET2) {
            controlStatus();
        } else if (grandBossStatusManager.getStatus().ordinal() <= 2) {
            if (npc.getId() == HALL_KEEPER_CAPTAIN) {
                if (getRandom(100) < 5) {
                    npc.dropItem(player, DEWDROP_OF_DESTRUCTION_ITEM_ID, 1);
                }
            }

            if (killProgress(npc)) {
                controlStatus();
            }
        } else if (state.demons.contains(npc)) {
            state.demons.remove(npc);
        } else if (state.portraits.containsKey(npc)) {
            state.portraits.remove(npc);
        }

        return "";
    }

    @Override
    public String onTalk(L2Npc npc, L2PcInstance player) {
        int npcId = npc.getId();
        getQuestState(player, true);
        if (npcId == GUIDE) {
            enterZone(player);
        } else if (npc.getId() == CUBE) {
            int x = EXIT_LOCATION.getX() + getRandom(500);
            int y = EXIT_LOCATION.getY() + getRandom(500);
            player.teleToLocation(x, y, EXIT_LOCATION.getY());
            return null;
        }
        return "";
    }

    public static void main(String[] args) {
        new Frintezza();
    }

}
