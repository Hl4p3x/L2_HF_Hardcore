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
package com.l2jserver.gameserver.model.actor.templates;

import com.l2jserver.Config;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.enums.AISkillScope;
import com.l2jserver.gameserver.enums.AIType;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.Sex;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.calculators.DynamicDropCalculator;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.drops.DropListScope;
import com.l2jserver.gameserver.model.drops.IDropItem;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.interfaces.IIdentifiable;
import com.l2jserver.gameserver.model.skills.Skill;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * NPC template.
 * @author NosBit
 */
public final class L2NpcTemplate extends L2CharTemplate implements IIdentifiable
{
	private int _id;
	private int _displayId;
	private byte _level;
	private String _type;
	private String _name;
	private boolean _usingServerSideName;
	private String _title;
	private boolean _usingServerSideTitle;
	private StatsSet _parameters;
	private Sex _sex;
	private int _chestId;
	private int _rhandId;
	private int _lhandId;
	private int _weaponEnchant;
	private double _expRate;
	private double _sp;
	private double _raidPoints;
	private boolean _unique;
	private boolean _attackable;
	private boolean _targetable;
	private boolean _undying;
	private boolean _showName;
	private boolean _flying;
	private boolean _canMove;
	private boolean _noSleepMode;
	private boolean _passableDoor;
	private boolean _hasSummoner;
	private boolean _canBeSown;
	private int _corpseTime;
	private AIType _aiType;
	private int _aggroRange;
	private int _clanHelpRange;
	private int _dodge;
	private boolean _isChaos;
	private boolean _isAggressive;
	private int _soulShot;
	private int _spiritShot;
	private int _soulShotChance;
	private int _spiritShotChance;
	private int _minSkillChance;
	private int _maxSkillChance;
	private int shortRangeSkillChance;
	private int longRangeSkillChance;
	private Map<Integer, Skill> _skills = Collections.emptyMap();
	private Map<AISkillScope, List<Skill>> _aiSkillLists = Collections.emptyMap();
	private Set<Integer> _clans;
	private Set<Integer> _ignoreClanNpcIds;
	private Map<DropListScope, List<IDropItem>> _dropLists;
	private double _collisionRadiusGrown;
	private double _collisionHeightGrown;
	
	private final List<ClassId> _teachInfo = new ArrayList<>();
	
	/**
	 * Constructor of L2Character.
	 * @param set The StatsSet object to transfer data to the method
	 */
	public L2NpcTemplate(StatsSet set)
	{
		super(set);
	}

	public L2NpcTemplate(int id, int displayId, String name, String title, double collisionRadius, double collisionHeight) {
		StatsSet statsSet = new StatsSet();
		statsSet.set("id", id);
		statsSet.set("displayId", displayId);
		statsSet.set("name", name);
		statsSet.set("title", title);
		statsSet.set("collisionRadius", collisionRadius);
		statsSet.set("collisionHeight", collisionHeight);
		set(statsSet);
	}


	@Override
	public void set(StatsSet set)
	{
		super.set(set);
		_id = set.getInt("id");
		_displayId = set.getInt("displayId", _id);
		_level = set.getByte("level", (byte) 85);
		_type = set.getString("type", "L2Npc");
		_name = set.getString("name", "");
		_usingServerSideName = set.getBoolean("usingServerSideName", false);
		_title = set.getString("title", "");
		_usingServerSideTitle = set.getBoolean("usingServerSideTitle", false);
		setRace(set.getEnum("race", Race.class, Race.NONE));
		_sex = set.getEnum("sex", Sex.class, Sex.ETC);
		
		_chestId = set.getInt("chestId", 0);
		_rhandId = set.getInt("rhandId", 0);
		_lhandId = set.getInt("lhandId", 0);
		_weaponEnchant = set.getInt("weaponEnchant", 0);
		
		_expRate = set.getDouble("expRate", 0);
		_sp = set.getDouble("sp", 0);
		_raidPoints = set.getDouble("raidPoints", 0);
		
		_unique = set.getBoolean("unique", false);
		_attackable = set.getBoolean("attackable", true);
		_targetable = set.getBoolean("targetable", true);
		_undying = set.getBoolean("undying", true);
		_showName = set.getBoolean("showName", true);
		_flying = set.getBoolean("flying", false);
		_canMove = set.getBoolean("canMove", true);
		_noSleepMode = set.getBoolean("noSleepMode", false);
		_passableDoor = set.getBoolean("passableDoor", false);
		_hasSummoner = set.getBoolean("hasSummoner", false);
		_canBeSown = set.getBoolean("canBeSown", false);
		
		_corpseTime = set.getInt("corpseTime", Config.DEFAULT_CORPSE_TIME);
		
		_aiType = set.getEnum("aiType", AIType.class, AIType.FIGHTER);
		_aggroRange = set.getInt("aggroRange", 0);
		_clanHelpRange = set.getInt("clanHelpRange", 0);
		_dodge = set.getInt("dodge", 0);
		_isChaos = set.getBoolean("isChaos", false);
		_isAggressive = set.getBoolean("isAggressive", true);
		
		_soulShot = set.getInt("soulShot", 0);
		_spiritShot = set.getInt("spiritShot", 0);
		_soulShotChance = set.getInt("shotShotChance", 0);
		_spiritShotChance = set.getInt("spiritShotChance", 0);
		
		_minSkillChance = set.getInt("minSkillChance", Config.NPC_DEFAULT_MIN_SKILL_CHANCE);
		_maxSkillChance = set.getInt("maxSkillChance", Config.NPC_DEFAULT_MAX_SKILL_CHANCE);
		
		shortRangeSkillChance = set.getInt("shortRangeSkillChance", -1);
		longRangeSkillChance = set.getInt("longRangeSkillChance", -1);
		
		_collisionRadiusGrown = set.getDouble("collisionRadiusGrown", 0);
		_collisionHeightGrown = set.getDouble("collisionHeightGrown", 0);
	}

	public void setUsingServerSideName(boolean usingServerSideName) {
		this._usingServerSideName = usingServerSideName;
	}

	public void setUsingServerSideTitle(boolean usingServerSideTitle) {
		this._usingServerSideTitle = usingServerSideTitle;
	}

	@Override
	public int getId()
	{
		return _id;
	}
	
	public int getDisplayId()
	{
		return _displayId;
	}
	
	public byte getLevel()
	{
		return _level;
	}

	public String getType()
	{
		return _type;
	}
	
	public boolean isType(String type)
	{
		return getType().equalsIgnoreCase(type);
	}
	
	public String getName()
	{
		return _name;
	}
	
	public boolean isUsingServerSideName()
	{
		return _usingServerSideName;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public boolean isUsingServerSideTitle()
	{
		return _usingServerSideTitle;
	}
	
	public StatsSet getParameters()
	{
		return _parameters;
	}
	
	public void setParameters(StatsSet set)
	{
		_parameters = set;
	}
	
	public Sex getSex()
	{
		return _sex;
	}
	
	public int getChestId()
	{
		return _chestId;
	}
	
	public int getRHandId()
	{
		return _rhandId;
	}
	
	public int getLHandId()
	{
		return _lhandId;
	}
	
	public int getWeaponEnchant()
	{
		return _weaponEnchant;
	}
	
	public double getExpRate()
	{
		return _expRate;
	}
	
	public double getSP()
	{
		return _sp;
	}
	
	public double getRaidPoints()
	{
		return _raidPoints;
	}
	
	public boolean isUnique()
	{
		return _unique;
	}
	
	public boolean isAttackable()
	{
		return _attackable;
	}
	
	public boolean isTargetable()
	{
		return _targetable;
	}
	
	public boolean isUndying()
	{
		return _undying;
	}
	
	public boolean isShowName()
	{
		return _showName;
	}
	
	public boolean isFlying()
	{
		return _flying;
	}
	
	public boolean canMove()
	{
		return _canMove;
	}
	
	public boolean isNoSleepMode()
	{
		return _noSleepMode;
	}
	
	public boolean isPassableDoor()
	{
		return _passableDoor;
	}
	
	public boolean hasSummoner()
	{
		return _hasSummoner;
	}
	
	public boolean canBeSown()
	{
		return _canBeSown;
	}
	
	public int getCorpseTime()
	{
		return _corpseTime;
	}
	
	public AIType getAIType()
	{
		return _aiType;
	}
	
	public int getAggroRange()
	{
		return _aggroRange;
	}
	
	public int getClanHelpRange()
	{
		return _clanHelpRange;
	}
	
	public int getDodge()
	{
		return _dodge;
	}
	
	public boolean isChaos()
	{
		return _isChaos;
	}
	
	public boolean isAggressive()
	{
		return _isAggressive;
	}
	
	public int getSoulShot()
	{
		return _soulShot;
	}
	
	public int getSpiritShot()
	{
		return _spiritShot;
	}
	
	public int getSoulShotChance()
	{
		return _soulShotChance;
	}
	
	public int getSpiritShotChance()
	{
		return _spiritShotChance;
	}
	
	public int getMinSkillChance()
	{
		return _minSkillChance;
	}
	
	public int getMaxSkillChance()
	{
		return _maxSkillChance;
	}
	
	public int getShortRangeSkillChance()
	{
		return shortRangeSkillChance;
	}
	
	public int getLongRangeSkillChance()
	{
		return longRangeSkillChance;
	}
	
	@Override
	public Map<Integer, Skill> getSkills()
	{
		return _skills != null ? _skills : Collections.emptyMap();
	}
	
	public void setSkills(Map<Integer, Skill> skills)
	{
		_skills = skills != null ? Collections.unmodifiableMap(skills) : Collections.emptyMap();
	}
	
	public List<Skill> getAISkills(AISkillScope aiSkillScope)
	{
		return _aiSkillLists.getOrDefault(aiSkillScope, new ArrayList<>());
	}
	
	public void setAISkillLists(Map<AISkillScope, List<Skill>> aiSkillLists)
	{
		_aiSkillLists = aiSkillLists != null ? Collections.unmodifiableMap(aiSkillLists) : Collections.emptyMap();
	}
	
	public Set<Integer> getClans()
	{
		return _clans;
	}
	
	/**
	 * @param clans A sorted array of clan ids
	 */
	public void setClans(Set<Integer> clans)
	{
		_clans = clans != null ? Collections.unmodifiableSet(clans) : null;
	}
	
	/**
	 * @param clanName clan name to check if it belongs to this NPC template clans.
	 * @param clanNames clan names to check if they belong to this NPC template clans.
	 * @return {@code true} if at least one of the clan names belong to this NPC template clans, {@code false} otherwise.
	 */
	public boolean isClan(String clanName, String... clanNames)
	{
		// Using local variable for the sake of reloading since it can be turned to null.
		final Set<Integer> clans = _clans;
		
		if (clans == null)
		{
			return false;
		}
		
		int clanId = NpcData.getInstance().getClanId("ALL");
		if (clans.contains(clanId))
		{
			return true;
		}
		
		clanId = NpcData.getInstance().getClanId(clanName);
		if (clans.contains(clanId))
		{
			return true;
		}
		
		for (String name : clanNames)
		{
			clanId = NpcData.getInstance().getClanId(name);
			if (clans.contains(clanId))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param clans A set of clan names to check if they belong to this NPC template clans.
	 * @return {@code true} if at least one of the clan names belong to this NPC template clans, {@code false} otherwise.
	 */
	public boolean isClan(Set<Integer> clans)
	{
		// Using local variable for the sake of reloading since it can be turned to null.
		final Set<Integer> clanSet = _clans;
		
		if ((clanSet == null) || (clans == null))
		{
			return false;
		}
		
		int clanId = NpcData.getInstance().getClanId("ALL");
		if (clanSet.contains(clanId))
		{
			return true;
		}
		
		for (Integer id : clans)
		{
			if (clanSet.contains(id))
			{
				return true;
			}
		}
		return false;
	}
	
	public Set<Integer> getIgnoreClanNpcIds()
	{
		return _ignoreClanNpcIds;
	}
	
	/**
	 * @param ignoreClanNpcIds the ignore clan npc ids
	 */
	public void setIgnoreClanNpcIds(Set<Integer> ignoreClanNpcIds)
	{
		_ignoreClanNpcIds = ignoreClanNpcIds != null ? Collections.unmodifiableSet(ignoreClanNpcIds) : null;
	}
	
	public Map<DropListScope, List<IDropItem>> getDropLists()
	{
		return _dropLists;
	}
	
	public void setDropLists(Map<DropListScope, List<IDropItem>> dropLists)
	{
		_dropLists = dropLists != null ? Collections.unmodifiableMap(dropLists) : null;
	}
	
	public List<IDropItem> getDropList(DropListScope dropListScope)
	{
		Map<DropListScope, List<IDropItem>> dropLists = _dropLists;
		return dropLists != null ? dropLists.get(dropListScope) : null;
	}

	public Collection<ItemHolder> calculateDrops(DropListScope dropListScope, L2Character victim, L2Character killer) {
		if (Config.DYNAMIC_LOOT) {
			Collection<ItemHolder> normalDropList = calculateNormalDrops(dropListScope, victim, killer);
			if (normalDropList == null || normalDropList.isEmpty()) {
				return Collections.emptyList();
			}

			List<ItemHolder> normalNonDynamicDrop = normalDropList.stream().filter(drop -> !DynamicDropCalculator.getInstance().getAllDynamicItemsIds().contains(drop.getId())).collect(Collectors.toList());
			List<ItemHolder> dynamicDrop = DynamicDropCalculator.getInstance().calculate(victim);

			return Stream.concat(normalNonDynamicDrop.stream(), dynamicDrop.stream()).collect(Collectors.toList());
		} else {
			return calculateNormalDrops(dropListScope, victim, killer);
		}
	}

	public Collection<ItemHolder> calculateNormalDrops(DropListScope dropListScope, L2Character victim, L2Character killer) {
		List<IDropItem> dropList = getDropList(dropListScope);
		if (dropList == null) {
			return null;
		}

		Collection<ItemHolder> calculatedDrops = null;
		for (IDropItem dropItem : dropList) {
			final Collection<ItemHolder> drops = dropItem.calculateDrops(victim, killer);
			if ((drops == null) || drops.isEmpty()) {
				continue;
			}

			if (calculatedDrops == null) {
				calculatedDrops = new LinkedList<>();
			}

			calculatedDrops.addAll(drops);
		}

		return calculatedDrops;
	}



	public double getCollisionRadiusGrown()
	{
		return _collisionRadiusGrown;
	}
	
	public double getCollisionHeightGrown()
	{
		return _collisionHeightGrown;
	}
	
	public static boolean isAssignableTo(Class<?> sub, Class<?> clazz)
	{
		// If clazz represents an interface
		if (clazz.isInterface())
		{
			// check if obj implements the clazz interface
			Class<?>[] interfaces = sub.getInterfaces();
			for (Class<?> interface1 : interfaces)
			{
				if (clazz.getName().equals(interface1.getName()))
				{
					return true;
				}
			}
		}
		else
		{
			do
			{
				if (sub.getName().equals(clazz.getName()))
				{
					return true;
				}
				
				sub = sub.getSuperclass();
			}
			while (sub != null);
		}
		return false;
	}
	
	public boolean isMonster()
	{
		return _type.equals("L2Monster");
	}
	
	/**
	 * Checks if obj can be assigned to the Class represented by clazz.<br>
	 * This is true if, and only if, obj is the same class represented by clazz, or a subclass of it or obj implements the interface represented by clazz.
	 * @param obj
	 * @param clazz
	 * @return {@code true} if the object can be assigned to the class, {@code false} otherwise
	 */
	public static boolean isAssignableTo(Object obj, Class<?> clazz)
	{
		return L2NpcTemplate.isAssignableTo(obj.getClass(), clazz);
	}
	
	public boolean canTeach(ClassId classId)
	{
		// If the player is on a third class, fetch the class teacher
		// information for its parent class.
		if (classId.level() == 3)
		{
			return _teachInfo.contains(classId.getParent());
		}
		return _teachInfo.contains(classId);
	}
	
	public List<ClassId> getTeachInfo()
	{
		return _teachInfo;
	}
	
	public void addTeachInfo(List<ClassId> teachInfo)
	{
		_teachInfo.addAll(teachInfo);
	}
}
