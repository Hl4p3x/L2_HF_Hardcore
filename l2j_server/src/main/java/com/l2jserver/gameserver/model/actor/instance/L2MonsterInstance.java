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
package com.l2jserver.gameserver.model.actor.instance;

import com.l2jserver.Config;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.AISkillScope;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.knownlist.MonsterKnownList;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.util.MinionList;
import com.l2jserver.util.Rnd;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * This class manages all Monsters. L2MonsterInstance:
 * <ul>
 * <li>L2MinionInstance</li>
 * <li>L2RaidBossInstance</li>
 * <li>L2GrandBossInstance</li>
 * </ul>
 */
public class L2MonsterInstance extends L2Attackable
{
	private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;
	
	protected boolean _enableMinions = true;
	
	private L2MonsterInstance _master = null;
	private volatile MinionList _minionList = null;
	
	protected ScheduledFuture<?> _maintenanceTask = null;

	private List<Skill> dynamicShortRangeSkills = new ArrayList<>();
	private List<Skill> dynamicLongRangeSkills = new ArrayList<>();
	private List<Skill> dynamicBuffSkills = new ArrayList<>();
	private List<Skill> dynamicHealSkills = new ArrayList<>();
	
	/**
	 * Creates a monster.
	 * @param template the monster NPC template
	 */
	public L2MonsterInstance(L2NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2MonsterInstance);
		setAutoAttackable(true);
		addRandomSkills();
	}

	private void addRandomSkills() {
		if (Config.ADD_RANDOM_NPC_SKILLS && !isRaid() && !isRaidMinion()) {
			boolean isFighter = getTemplate().getBasePAtk() > getTemplate().getBaseMAtk();
			L2Weapon activeWeapon = getActiveWeaponItem();
			WeaponType activeWeaponType;
			if (activeWeapon == null) {
				activeWeaponType = WeaponType.NONE;
			} else {
				activeWeaponType = activeWeapon.getItemType();
			}

			List<Skill> possibleSkills = SkillData.getInstance()
					.lookupAllSkillLearns(SkillTreesData.getInstance().getAllSkillsByAcquiredLevel(getLevel()))
					.stream()
					.filter(skill -> {
						if (skill.isPassive()) {
							return false;
						}
						if (isFighter) {
							return skill.hasEffectType(
									L2EffectType.PHYSICAL_ATTACK,
                                    L2EffectType.SUMMON,
									L2EffectType.STUN);
						} else {
							return skill.hasEffectType(
									L2EffectType.MAGICAL_ATTACK,
                                    L2EffectType.HEAL, L2EffectType.SUMMON, L2EffectType.PARALYZE,
									L2EffectType.SLEEP, L2EffectType.ROOT);
						}
					})
					.filter(skill -> {
						Optional<Boolean> result = skill.getConditionUsingItemType()
								.map(conditionUsingItemType -> conditionUsingItemType.checkUsingWeaponMask(activeWeaponType));
						return result.isEmpty() || result.get();
					})
					.collect(Collectors.toList());


			Rnd.getUniqueRandom(possibleSkills, Config.RANDOM_NPC_SKILLS_COUNT).forEach(skill -> {
				AISkillScope rangeSkillScope = NpcData.getSkillRangeScope(skill);
				if (skill.hasEffectType(L2EffectType.HEAL)) {
					dynamicHealSkills.add(skill);
				} else if (skill.hasEffectType(L2EffectType.BUFF)) {
					dynamicBuffSkills.add(skill);
				} else if (rangeSkillScope.equals(AISkillScope.SHORT_RANGE)) {
					dynamicShortRangeSkills.add(skill);
				} else if (rangeSkillScope.equals(AISkillScope.LONG_RANGE)) {
					dynamicLongRangeSkills.add(skill);
				}
			});
		}
	}

	@Override
	public List<Skill> getBuffSkills() {
		List<Skill> skills = new ArrayList<>(super.getBuffSkills());
		skills.addAll(dynamicBuffSkills);
		return skills;
	}

	@Override
	public List<Skill> getHealSkills() {
		List<Skill> skills = new ArrayList<>(super.getHealSkills());
		skills.addAll(dynamicHealSkills);
		return skills;
	}

	@Override
	public List<Skill> getLongRangeSkills() {
		List<Skill> skills = new ArrayList<>(super.getLongRangeSkills());
		skills.addAll(dynamicLongRangeSkills);
		return skills;
	}

	@Override
	public List<Skill> getShortRangeSkills() {
		List<Skill> skills = new ArrayList<>(super.getShortRangeSkills());
		skills.addAll(dynamicShortRangeSkills);
		return skills;
	}

	@Override
	public final MonsterKnownList getKnownList()
	{
		return (MonsterKnownList) super.getKnownList();
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new MonsterKnownList(this));
	}
	
	/**
	 * Return True if the attacker is not another L2MonsterInstance.
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return super.isAutoAttackable(attacker) && !isEventMob();
	}
	
	@Override
	public boolean isAggressive()
	{
		return getTemplate().isAggressive() && !isEventMob();
	}
	
	@Override
	public void onSpawn()
	{
		if (!isTeleporting())
		{
			if (getLeader() != null)
			{
				setIsNoRndWalk(true);
				setIsRaidMinion(getLeader().isRaid());
				getLeader().getMinionList().onMinionSpawn(this);
			}
			
			// delete spawned minions before dynamic minions spawned by script
			if (hasMinions())
			{
				getMinionList().onMasterSpawn();
			}
			
			startMaintenanceTask();
		}
		
		// dynamic script-based minions spawned here, after all preparations.
		super.onSpawn();
	}
	
	@Override
	public void onTeleported()
	{
		super.onTeleported();
		
		if (hasMinions())
		{
			getMinionList().onMasterTeleported();
		}
	}
	
	protected int getMaintenanceInterval()
	{
		return MONSTER_MAINTENANCE_INTERVAL;
	}
	
	protected void startMaintenanceTask()
	{
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false); // doesn't do it?
			_maintenanceTask = null;
		}
		
		return true;
	}
	
	@Override
	public boolean deleteMe()
	{
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false);
			_maintenanceTask = null;
		}
		
		if (hasMinions())
		{
			getMinionList().onMasterDie(true);
		}
		
		if (getLeader() != null)
		{
			getLeader().getMinionList().onMinionDie(this, 0);
		}
		
		return super.deleteMe();
	}
	
	@Override
	public L2MonsterInstance getLeader()
	{
		return _master;
	}
	
	public void setLeader(L2MonsterInstance leader)
	{
		_master = leader;
	}
	
	public void enableMinions(boolean b)
	{
		_enableMinions = b;
	}
	
	public boolean hasMinions()
	{
		return _minionList != null;
	}
	
	public MinionList getMinionList()
	{
		if (_minionList == null)
		{
			synchronized (this)
			{
				if (_minionList == null)
				{
					_minionList = new MinionList(this);
				}
			}
		}
		return _minionList;
	}
	
	@Override
	public boolean isMonster()
	{
		return true;
	}
	
	/**
	 * @return true if this L2MonsterInstance (or its master) is registered in WalkingManager
	 */
	@Override
	public boolean isWalker()
	{
		return ((getLeader() == null) ? super.isWalker() : getLeader().isWalker());
	}
	
	/**
	 * @return {@code true} if this L2MonsterInstance is not raid minion, master state otherwise.
	 */
	@Override
	public boolean giveRaidCurse()
	{
		return (isRaidMinion() && (getLeader() != null)) ? getLeader().giveRaidCurse() : super.giveRaidCurse();
	}
}
