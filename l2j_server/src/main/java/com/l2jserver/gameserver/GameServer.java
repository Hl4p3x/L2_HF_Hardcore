/*
 * Copyright (C) 2004-2016 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver;

import com.google.common.base.Stopwatch;
import com.l2jserver.Config;
import com.l2jserver.Server;
import com.l2jserver.common.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.*;
import com.l2jserver.gameserver.data.xml.impl.*;
import com.l2jserver.gameserver.datatables.*;
import com.l2jserver.gameserver.handler.EffectHandler;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.instancemanager.*;
import com.l2jserver.gameserver.model.AutoSpawnHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.PartyMatchRoomList;
import com.l2jserver.gameserver.model.PartyMatchWaitingList;
import com.l2jserver.gameserver.model.actor.templates.drop.calculators.DynamicDropCalculator;
import com.l2jserver.gameserver.model.entity.Hero;
import com.l2jserver.gameserver.model.entity.TvTManager;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.olympiad.Olympiad;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.L2GamePacketHandler;
import com.l2jserver.gameserver.pathfinding.PathFinding;
import com.l2jserver.gameserver.scripting.L2ScriptEngineManager;
import com.l2jserver.gameserver.taskmanager.KnownListUpdateTaskManager;
import com.l2jserver.gameserver.taskmanager.TaskManager;
import com.l2jserver.localization.MultilangTables;
import com.l2jserver.mmocore.SelectorConfig;
import com.l2jserver.mmocore.SelectorThread;
import com.l2jserver.status.Status;
import com.l2jserver.util.DeadLockDetector;
import com.l2jserver.util.IPv4Filter;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameServer
{
	private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);
	private static final String LOG_FOLDER = "log"; // Name of folder for log file
	private static final String LOG_NAME = "./log.cfg"; // Name of log file
	
	private final SelectorThread<L2GameClient> _selectorThread;
	private final L2GamePacketHandler _gamePacketHandler;
	private final DeadLockDetector _deadDetectThread;
	public static GameServer gameServer;
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();

	public GameServer() throws Exception {
		long serverLoadStart = System.currentTimeMillis();

		long usedMemoryStart = getUsedMemoryMB();
		LOG.info("{}: Used memory: {} MB.", getClass().getSimpleName(), usedMemoryStart);
		
		if (!IdFactory.getInstance().isInitialized())
		{
			LOG.error("{}: Could not read object IDs from database. Please check your configuration.", getClass().getSimpleName());
			throw new Exception("Could not initialize the ID factory!");
		}
		
		ThreadPoolManager.getInstance();
		EventDispatcher.getInstance();
		
		new File("log/game").mkdirs();
		
		// load script engines
		timeIt("Engines", () -> L2ScriptEngineManager.getInstance());

		timeIt("World", () -> {
			// start game time control early
			GameTimeController.init();
			InstanceManager.getInstance();
			L2World.getInstance();
			MapRegionManager.getInstance();
			AnnouncementsTable.getInstance();
			GlobalVariablesManager.getInstance();
		});

		timeIt("Data", () -> {
			CategoryData.getInstance();
			SecondaryAuthData.getInstance();
		});

		timeIt("Effects", () -> EffectHandler.getInstance().executeScript());
		timeIt("Enchant Skill Groups", () -> EnchantSkillGroupsData.getInstance());
		timeIt("Skill Trees", () -> SkillTreesData.getInstance());
		timeIt("Skills", () -> {
			SkillData.getInstance();
			SummonSkillsTable.getInstance();
		});

		timeIt("Items", () -> {
			ItemTable.getInstance();
			EnchantItemGroupsData.getInstance();
			EnchantItemData.getInstance();
			EnchantItemOptionsData.getInstance();
			OptionData.getInstance();
			EnchantItemHPBonusData.getInstance();
			MerchantPriceConfigTable.getInstance().loadInstances();
			BuyListData.getInstance();
			MultisellData.getInstance();
			RecipeData.getInstance();
			ArmorSetsData.getInstance();
			FishData.getInstance();
			FishingMonstersData.getInstance();
			FishingRodsData.getInstance();
			HennaData.getInstance();
		});

		timeIt("Characters", () -> {
			ClassListData.getInstance();
			InitialEquipmentData.getInstance();
			InitialShortcutData.getInstance();
			ExperienceData.getInstance();
			PlayerXpPercentLostData.getInstance();
			KarmaData.getInstance();
			HitConditionBonusData.getInstance();
			PlayerTemplateData.getInstance();
			CharNameTable.getInstance();
			AdminData.getInstance();
			RaidBossPointsManager.getInstance();
			PetDataTable.getInstance();
			CharSummonTable.getInstance().init();
		});

		timeIt("Clans", () -> {
			ClanTable.getInstance();
			CHSiegeManager.getInstance();
			ClanHallManager.getInstance();
			AuctionManager.getInstance();
		});

		timeIt("Geodata", () -> {
			GeoData.getInstance();

			if (Config.PATHFINDING > 0) {
				PathFinding.getInstance();
			}
		});

		timeIt("NPCs", () -> {
			SkillLearnData.getInstance();
			NpcData.getInstance();
			WalkingManager.getInstance();
			StaticObjectData.getInstance();
			ZoneManager.getInstance();
			DoorData.getInstance();
			CastleManager.getInstance().loadInstances();
			NpcBufferTable.getInstance();
			GrandBossManager.getInstance().initZones();
			EventDroplist.getInstance();
		});

		timeIt("Auction Manager", ItemAuctionManager::getInstance);

		timeIt("Olympiad", () -> {
			Olympiad.getInstance();
			Hero.getInstance();
		});

		timeIt("Seven Signs", SevenSigns::getInstance);
		
		// Call to load caches
		timeIt("Cache", () -> {
			HtmCache.getInstance();
			CrestTable.getInstance();
			TeleportLocationTable.getInstance();
			UIData.getInstance();
			PartyMatchWaitingList.getInstance();
			PartyMatchRoomList.getInstance();
			PetitionManager.getInstance();
			AugmentationData.getInstance();
			CursedWeaponsManager.getInstance();
			TransformData.getInstance();
			BotReportTable.getInstance();
		});

		timeIt("Multilang", () -> MultilangTables.getInstance().load());

		timeIt("Scripts", () -> {
			QuestManager.getInstance();
			BoatManager.getInstance();
			// AirShipManager.getInstance();
			// GraciaSeedsManager.getInstance();

			try {
				LOG.info("{}: Loading server scripts:", getClass().getSimpleName());
				if (!Config.ALT_DEV_NO_HANDLERS || !Config.ALT_DEV_NO_QUESTS) {
					L2ScriptEngineManager.getInstance().executeScriptList(new File(Config.DATAPACK_ROOT, "data/scripts.cfg"));
				}
			} catch (IOException ioe) {
				LOG.error("{}: Failed loading scripts.cfg, scripts are not going to be loaded!", getClass().getSimpleName());
			}
		});

		timeIt("Spawns", () -> {
			SpawnTable.getInstance().load();
			DayNightSpawnManager.getInstance().trim().notifyChangeMode();
			RaidBossSpawnManager.getInstance();
		});

		timeIt("Four Sepulchers", () -> FourSepulchersManager.getInstance().init());
		timeIt("Dimensional Rift", DimensionalRiftManager::getInstance);

		timeIt("Dynamic Drop : " + Config.DYNAMIC_LOOT, () -> {
			if (Config.DYNAMIC_LOOT) {
				DynamicDropCalculator.getInstance();
			}
		});

		timeIt("Siege", () -> {
			SiegeManager.getInstance().getSieges();
			CastleManager.getInstance().activateInstances();
			FortManager.getInstance().loadInstances();
			FortManager.getInstance().activateInstances();
			FortSiegeManager.getInstance();
			SiegeScheduleData.getInstance();

			MerchantPriceConfigTable.getInstance().updateReferences();
			TerritoryWarManager.getInstance();
			CastleManorManager.getInstance();
			MercTicketManager.getInstance();
		});
		
		QuestManager.getInstance().report();
		
		if (Config.SAVE_DROPPED_ITEM)
		{
			ItemsOnGroundManager.getInstance();
		}
		
		if ((Config.AUTODESTROY_ITEM_AFTER > 0) || (Config.HERB_AUTO_DESTROY_TIME > 0))
		{
			ItemsAutoDestroy.getInstance();
		}
		
		MonsterRace.getInstance();

		timeIt("Seven Signs", () -> {
			SevenSigns.getInstance().spawnSevenSignsNPC();
			SevenSignsFestival.getInstance();
		});
		AutoSpawnHandler.getInstance();

		LOG.info("AutoSpawnHandler: Loaded {} handlers in total.", AutoSpawnHandler.getInstance().size());
		
		if (Config.L2JMOD_ALLOW_WEDDING)
		{
			CoupleManager.getInstance();
		}
		
		TaskManager.getInstance();
		
		AntiFeedManager.getInstance().registerEvent(AntiFeedManager.GAME_ID);
		
		if (Config.ALLOW_MAIL)
		{
			MailManager.getInstance();
		}
		
		PunishmentManager.getInstance();
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		LOG.info("IdFactory: Free ObjectID's remaining: {}", IdFactory.getInstance().size());
		
		TvTManager.getInstance();
		KnownListUpdateTaskManager.getInstance();
		
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
		{
			OfflineTradersTable.getInstance().restoreOfflineTraders();
		}
		
		if (Config.DEADLOCK_DETECTOR)
		{
			_deadDetectThread = new DeadLockDetector();
			_deadDetectThread.setDaemon(true);
			_deadDetectThread.start();
		}
		else
		{
			_deadDetectThread = null;
		}
		System.gc();
		// maxMemory is the upper limit the jvm can use, totalMemory the size of
		// the current allocation pool, freeMemory the unused memory in the allocation pool
		long freeMem = ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory()) + Runtime.getRuntime().freeMemory()) / 1048576;
		long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		LOG.info("{}: Started, free memory {} Mb of {} Mb", getClass().getSimpleName(), freeMem, totalMem);
		Toolkit.getDefaultToolkit().beep();
		LoginServerThread.getInstance().start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		sc.TCP_NODELAY = Config.MMO_TCP_NODELAY;
		
		_gamePacketHandler = new L2GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, _gamePacketHandler, _gamePacketHandler, _gamePacketHandler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (UnknownHostException e1)
			{
				LOG.error("{}: The GameServer bind address is invalid, using all avaliable IPs!", getClass().getSimpleName(), e1);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
			_selectorThread.start();
			LOG.info("{}: is now listening on: {}:{}", getClass().getSimpleName(), Config.GAMESERVER_HOSTNAME, Config.PORT_GAME);
		}
		catch (IOException e)
		{
			LOG.error("{}: Failed to open server socket!", getClass().getSimpleName(), e);
			System.exit(1);
		}

		long loadedMemory = getUsedMemoryMB();

		LOG.info("{}: Maximum numbers of connected players: {}", getClass().getSimpleName(), Config.MAXIMUM_ONLINE_USERS);
		LOG.info("{}: Server loaded in {} seconds and loading memory footprint is {}.", getClass().getSimpleName(), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - serverLoadStart), loadedMemory - usedMemoryStart);
	}
	
	public static void main(String[] args) throws Exception
	{
		Server.serverMode = Server.MODE_GAMESERVER;
		
		/*** Main ***/
		// Create log folder
		File logFolder = new File(Config.DATAPACK_ROOT, LOG_FOLDER);
		logFolder.mkdir();
		
		// Create input stream for log file -- or store file data into memory
		try (InputStream is = new FileInputStream(new File(LOG_NAME)))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		
		// Initialize config
		Config.load();
		printSection("Database");
		DAOFactory.getInstance();
		ConnectionFactory.getInstance();
		
		gameServer = new GameServer();
		
		if (Config.IS_TELNET_ENABLED)
		{
			new Status(Server.serverMode).start();
		}
		else
		{
			LOG.info("{}: Telnet server is currently disabled.", GameServer.class.getSimpleName());
		}
	}
	
	public long getUsedMemoryMB()
	{
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
	}
	
	public SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
	
	public L2GamePacketHandler getL2GamePacketHandler()
	{
		return _gamePacketHandler;
	}
	
	public DeadLockDetector getDeadLockDetectorThread()
	{
		return _deadDetectThread;
	}

	public static void timeIt(String name, Runnable runnable) {
		printSection(name);
		Stopwatch stopwatch = Stopwatch.createStarted();
		runnable.run();
		LOG.info("Loading {} took {} seconds", name, stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000D);
	}

	public static void printSection(String s)
	{
		StringBuilder sBuilder = new StringBuilder("=[ " + s + " ]");
		while (sBuilder.length() < 61) {
			sBuilder.insert(0, "-");
		}
		s = sBuilder.toString();
		LOG.info(s);
	}
}
