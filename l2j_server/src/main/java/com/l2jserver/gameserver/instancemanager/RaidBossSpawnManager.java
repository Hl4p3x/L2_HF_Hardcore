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
package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.Config;
import com.l2jserver.common.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jserver.util.Rnd;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Raid Boss spawn manager.
 * @author godson
 */
public class RaidBossSpawnManager
{
	private static final Logger _log = Logger.getLogger(RaidBossSpawnManager.class.getName());
	
	protected static final Map<Integer, L2RaidBossInstance> _bosses = new ConcurrentHashMap<>();
	protected static final Map<Integer, L2Spawn> _spawns = new ConcurrentHashMap<>();
	protected static final Map<Integer, StatsSet> _storedInfo = new ConcurrentHashMap<>();
	protected static final Map<Integer, ScheduledFuture<?>> _schedules = new ConcurrentHashMap<>();
	
	public static enum StatusEnum
	{
		ALIVE,
		DEAD,
		UNDEFINED
	}
	
	/**
	 * Instantiates a new raid boss spawn manager.
	 */
	protected RaidBossSpawnManager()
	{
		load();
	}
	
	/**
	 * Load.
	 */
	public void load()
	{
		_bosses.clear();
		_spawns.clear();
		_storedInfo.clear();
		_schedules.clear();
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM raidboss_spawnlist ORDER BY boss_id"))
		{
			while (rs.next())
			{
				final L2Spawn spawnDat = new L2Spawn(rs.getInt("boss_id"));
				spawnDat.setX(rs.getInt("loc_x"));
				spawnDat.setY(rs.getInt("loc_y"));
				spawnDat.setZ(rs.getInt("loc_z"));
				spawnDat.setAmount(rs.getInt("amount"));
				spawnDat.setHeading(rs.getInt("heading"));
				spawnDat.setRespawnDelay(rs.getInt("respawn_delay"), rs.getInt("respawn_random"));
				
				addNewSpawn(spawnDat, rs.getLong("respawn_time"), rs.getDouble("currentHP"), rs.getDouble("currentMP"), false);
			}
			
			_log.info(getClass().getSimpleName() + ": Loaded " + _bosses.size() + " Instances");
			_log.info(getClass().getSimpleName() + ": Scheduled " + _schedules.size() + " Instances");
		}
		catch (SQLException e)
		{
			_log.warning(getClass().getSimpleName() + ": Couldnt load raidboss_spawnlist table");
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, getClass().getSimpleName() + ": Error while initializing RaidBossSpawnManager: " + e.getMessage(), e);
		}
	}

	private class SpawnSchedule implements Runnable {
		private final Logger _log = Logger.getLogger(SpawnSchedule.class.getName());
		
		private final int bossId;
		
		/**
		 * Instantiates a new spawn schedule.
		 * @param npcId the npc id
		 */
		public SpawnSchedule(int npcId)
		{
			bossId = npcId;
		}
		
		@Override
		public void run()
		{
			L2RaidBossInstance raidboss = null;
			
			if (bossId == 25328)
			{
				raidboss = DayNightSpawnManager.getInstance().handleBoss(_spawns.get(bossId));
			}
			else
			{
				raidboss = (L2RaidBossInstance) _spawns.get(bossId).doSpawn();
			}
			
			if (raidboss != null)
			{
				raidboss.setRaidStatus(StatusEnum.ALIVE);
				
				final StatsSet info = new StatsSet();
                info.set("currentHP", raidboss.getMaxHp());
                info.set("currentMP", raidboss.getMaxMp());
				info.set("respawnTime", 0L);
				
				_storedInfo.put(bossId, info);
				
				_log.info(getClass().getSimpleName() + ": Spawning Raid Boss " + raidboss.getName());
				
				_bosses.put(bossId, raidboss);
				syncSingleBoss(raidboss);
			}
			
			_schedules.remove(bossId);
		}
	}

	private void syncSingleBoss(L2RaidBossInstance boss) {
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			 PreparedStatement ps = con.prepareStatement("UPDATE raidboss_spawnlist SET respawn_time = ?, currentHP = ?, currentMP = ? WHERE boss_id = ?")) {

			if (boss == null) {
				return;
			}

			StatsSet info = _storedInfo.get(boss.getId());
			if (info == null) {
				return;
			}

			if (boss.getRaidStatus().equals(StatusEnum.ALIVE)) {
				info.set("currentHP", boss.getCurrentHp());
				info.set("currentMP", boss.getCurrentMp());
				info.set("respawnTime", 0L);
			}

			ps.setLong(1, info.getLong("respawnTime"));
			ps.setDouble(2, info.getDouble("currentHP"));
			ps.setDouble(3, info.getDouble("currentMP"));
			ps.setInt(4, boss.getId());

			try {
				ps.execute();
			} catch (SQLException e) {
				_log.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't update raidboss_spawnlist table " + e.getMessage() + " for boss " + boss, e);
			}
		} catch (SQLException e) {
			_log.log(Level.WARNING, getClass().getSimpleName() + ": SQL error while updating RaidBoss " + boss + " spawn to database: " + e.getMessage(), e);
		}
	}

	/**
	 * Update status.
	 * @param boss the boss
	 */
	public void setBossDead(L2RaidBossInstance boss) {
		final StatsSet info = _storedInfo.get(boss.getId());
		if (info == null) {
			return;
		}

		boss.setRaidStatus(StatusEnum.DEAD);

		final int respawnMinDelay = (int) (boss.getSpawn().getRespawnMinDelay() * Config.RAID_MIN_RESPAWN_MULTIPLIER);
		final int respawnMaxDelay = (int) (boss.getSpawn().getRespawnMaxDelay() * Config.RAID_MAX_RESPAWN_MULTIPLIER);
		final int respawnDelay = Rnd.get(respawnMinDelay, respawnMaxDelay);
		final long respawnTime = Calendar.getInstance().getTimeInMillis() + respawnDelay;

		info.set("currentHP", boss.getMaxHp());
		info.set("currentMP", boss.getMaxMp());
		info.set("respawnTime", respawnTime);

		if (!_schedules.containsKey(boss.getId()) && ((respawnMinDelay > 0) || (respawnMaxDelay > 0))) {
			final Calendar time = Calendar.getInstance();
			time.setTimeInMillis(respawnTime);
			_log.info(getClass().getSimpleName() + ": Updated " + boss.getName() + " respawn time to " + time.getTime());

			_schedules.put(boss.getId(), ThreadPoolManager.getInstance().scheduleGeneral(new SpawnSchedule(boss.getId()), respawnDelay));
			syncSingleBoss(boss);
		}
	}
	
	/**
	 * Adds the new spawn.
	 * @param spawnDat the spawn dat
	 * @param respawnTime the respawn time
	 * @param currentHP the current hp
	 * @param currentMP the current mp
	 * @param storeInDb the store in db
	 */
	public void addNewSpawn(L2Spawn spawnDat, long respawnTime, double currentHP, double currentMP, boolean storeInDb)
	{
		if (spawnDat == null)
		{
			return;
		}
		if (_spawns.containsKey(spawnDat.getId()))
		{
			return;
		}
		
		final int bossId = spawnDat.getId();
		final long time = Calendar.getInstance().getTimeInMillis();
		
		SpawnTable.getInstance().addNewSpawn(spawnDat, false);
		
		if ((respawnTime == 0L) || (time > respawnTime))
		{
			L2RaidBossInstance raidboss = null;
			if (bossId == 25328)
			{
				raidboss = DayNightSpawnManager.getInstance().handleBoss(spawnDat);
			}
			else
			{
				raidboss = (L2RaidBossInstance) spawnDat.doSpawn();
			}
			
			if (raidboss != null)
			{
				raidboss.setCurrentHp(currentHP);
				raidboss.setCurrentMp(currentMP);
				raidboss.setRaidStatus(StatusEnum.ALIVE);
				
				_bosses.put(bossId, raidboss);
				
				final StatsSet info = new StatsSet();
				info.set("currentHP", currentHP);
				info.set("currentMP", currentMP);
				info.set("respawnTime", 0L);
				
				_storedInfo.put(bossId, info);
			}
		}
		else
		{
			final long spawnTime = respawnTime - Calendar.getInstance().getTimeInMillis();
			_schedules.put(bossId, ThreadPoolManager.getInstance().scheduleGeneral(new SpawnSchedule(bossId), spawnTime));
		}
		
		_spawns.put(bossId, spawnDat);
		
		if (storeInDb)
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("INSERT INTO raidboss_spawnlist (boss_id,amount,loc_x,loc_y,loc_z,heading,respawn_time,currentHp,currentMp) VALUES(?,?,?,?,?,?,?,?,?)"))
			{
				ps.setInt(1, spawnDat.getId());
				ps.setInt(2, spawnDat.getAmount());
				ps.setInt(3, spawnDat.getX());
				ps.setInt(4, spawnDat.getY());
				ps.setInt(5, spawnDat.getZ());
				ps.setInt(6, spawnDat.getHeading());
				ps.setLong(7, respawnTime);
				ps.setDouble(8, currentHP);
				ps.setDouble(9, currentMP);
				ps.execute();
			}
			catch (Exception e)
			{
				// problem with storing spawn
				_log.log(Level.WARNING, getClass().getSimpleName() + ": Could not store raidboss #" + bossId + " in the DB:" + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Delete spawn.
	 * @param spawnDat the spawn dat
	 * @param updateDb the update db
	 */
	public void deleteSpawn(L2Spawn spawnDat, boolean updateDb)
	{
		if (spawnDat == null)
		{
			return;
		}
		
		final int bossId = spawnDat.getId();
		if (!_spawns.containsKey(bossId))
		{
			return;
		}
		
		SpawnTable.getInstance().deleteSpawn(spawnDat, false);
		_spawns.remove(bossId);
		_bosses.remove(bossId);
		
		if (_schedules.containsKey(bossId))
		{
			final ScheduledFuture<?> f = _schedules.remove(bossId);
			f.cancel(true);
		}

		_storedInfo.remove(bossId);
		
		if (updateDb)
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM raidboss_spawnlist WHERE boss_id=?"))
			{
				ps.setInt(1, bossId);
				ps.execute();
			}
			catch (Exception e)
			{
				// problem with deleting spawn
				_log.log(Level.WARNING, getClass().getSimpleName() + ": Could not remove raidboss #" + bossId + " from DB: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Update database.
	 */
	private void syncToDb() {
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			 PreparedStatement ps = con.prepareStatement("UPDATE raidboss_spawnlist SET respawn_time = ?, currentHP = ?, currentMP = ? WHERE boss_id = ?")) {

			for (Integer bossId : _storedInfo.keySet()) {
				if (bossId == null) {
					continue;
				}
				
				L2RaidBossInstance boss = _bosses.get(bossId);
				if (boss == null) {
					continue;
				}

				StatsSet info = _storedInfo.get(bossId);
				if (info == null) {
					continue;
				}

				if (boss.getRaidStatus().equals(StatusEnum.ALIVE)) {
					info.set("currentHP", boss.getCurrentHp());
					info.set("currentMP", boss.getCurrentMp());
					info.set("respawnTime", 0L);
				}

				ps.setLong(1, info.getLong("respawnTime"));
				ps.setDouble(2, info.getDouble("currentHP"));
				ps.setDouble(3, info.getDouble("currentMP"));
				ps.setInt(4, bossId);
				ps.addBatch();
			}

			try {
				ps.executeBatch();
			} catch (SQLException e) {
				_log.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't update raidboss_spawnlist table " + e.getMessage(), e);
			}
		} catch (SQLException e) {
			_log.log(Level.WARNING, getClass().getSimpleName() + ": SQL error while updating RaidBoss spawn to database: " + e.getMessage(), e);
		}
	}

	/**
	 * Gets the raid boss status id.
	 * @param bossId the boss id
	 * @return the raid boss status id
	 */
	public StatusEnum getRaidBossStatusId(int bossId)
	{
		if (_bosses.containsKey(bossId))
		{
			return _bosses.get(bossId).getRaidStatus();
		}
		else if (_schedules.containsKey(bossId))
		{
			return StatusEnum.DEAD;
		}
		else
		{
			return StatusEnum.UNDEFINED;
		}
	}
	
	/**
	 * Notify spawn night boss.
	 * @param raidboss the raidboss
	 */
	public void notifySpawnNightBoss(L2RaidBossInstance raidboss)
	{
		final StatsSet info = new StatsSet();
        info.set("currentHP", raidboss.getMaxHp());
        info.set("currentMP", raidboss.getMaxMp());
		info.set("respawnTime", 0L);
		
		raidboss.setRaidStatus(StatusEnum.ALIVE);
		
		_storedInfo.put(raidboss.getId(), info);
		
		_log.info(getClass().getSimpleName() + ": Spawning Night Raid Boss " + raidboss.getName());
		
		_bosses.put(raidboss.getId(), raidboss);
		syncSingleBoss(raidboss);
	}
	
	/**
	 * Checks if the boss is defined.
	 * @param bossId the boss id
	 * @return {@code true} if is defined
	 */
	public boolean isDefined(int bossId)
	{
		return _spawns.containsKey(bossId);
	}
	
	/**
	 * Gets the bosses.
	 * @return the bosses
	 */
	public Map<Integer, L2RaidBossInstance> getBosses()
	{
		return _bosses;
	}
	
	/**
	 * Gets the spawns.
	 * @return the spawns
	 */
	public Map<Integer, L2Spawn> getSpawns()
	{
		return _spawns;
	}
	
	/**
	 * Gets the stored info.
	 * @return the stored info
	 */
	public Map<Integer, StatsSet> getStoredInfo()
	{
		return _storedInfo;
	}
	
	/**
	 * Saves and clears the raid bosses status, including all schedules.
	 */
	public void cleanUp()
	{
		syncToDb();
		
		_bosses.clear();
		
		if (_schedules != null)
		{
			for (Integer bossId : _schedules.keySet())
			{
				ScheduledFuture<?> f = _schedules.get(bossId);
				f.cancel(true);
			}
			_schedules.clear();
		}
		
		_storedInfo.clear();
		_spawns.clear();
	}
	
	/**
	 * Gets the single instance of RaidBossSpawnManager.
	 * @return single instance of RaidBossSpawnManager
	 */
	public static RaidBossSpawnManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final RaidBossSpawnManager _instance = new RaidBossSpawnManager();
	}
}
