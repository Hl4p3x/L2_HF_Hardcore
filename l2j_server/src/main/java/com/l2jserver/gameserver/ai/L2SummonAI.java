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
package com.l2jserver.gameserver.ai;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.pathfinding.PathFinding;
import com.l2jserver.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static com.l2jserver.gameserver.ai.CtrlIntention.*;

public class L2SummonAI extends L2PlayableAI implements Runnable
{

	private static final Logger LOG = LoggerFactory.getLogger(L2SummonAI.class);
	private static final int AVOID_RADIUS = 70;
	
	private volatile boolean _thinking; // to prevent recursive thinking
	private volatile boolean _startFollow = ((L2Summon) _actor).getFollowStatus();
	private L2Character _lastAttack = null;
	
	private volatile boolean _startAvoid = false;
	private Future<?> _avoidTask = null;
	
	public L2SummonAI(L2Summon creature)
	{
		super(creature);
	}
	
	@Override
	protected void onIntentionAttack(L2Character target)
	{
		if ((Config.PATHFINDING > 0) && (PathFinding.getInstance().findPath(_actor.getX(), _actor.getY(), _actor.getZ(), target.getX(), target.getY(), target.getZ(), _actor.getInstanceId(), true) == null))
		{
			return;
		}
		LOG.debug("{} Intention attack", getActor());

		super.onIntentionAttack(target);
	}
	
	@Override
	protected void onIntentionIdle()
	{
		stopFollow();
		_startFollow = false;
		onIntentionActive();
	}
	
	@Override
	protected void onIntentionActive()
	{
		L2Summon summon = (L2Summon) _actor;
		if (_startFollow)
		{
			setIntention(AI_INTENTION_FOLLOW, summon.getOwner());
		}
		else
		{
			super.onIntentionActive();
		}
	}
	
	@Override
	synchronized void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		switch (intention)
		{
			case AI_INTENTION_ACTIVE:
			case AI_INTENTION_FOLLOW:
				startAvoidTask();
				break;
			default:
				stopAvoidTask();
		}
		
		super.changeIntention(intention, arg0, arg1);
	}
	
	private void thinkAttack()
	{
		if (checkTargetLostOrDead(getAttackTarget()))
		{
			LOG.debug("{} Target [{}] is lost or dead, setting attack target to null", getActor(), getAttackTarget());
			setAttackTarget(null);
			return;
		}
		if (maybeMoveToPawn(getAttackTarget(), _actor.getPhysicalAttackRange()))
		{
			LOG.debug("{} Target is too far away, moving to {}", getActor(), getAttackTarget());
			return;
		}
		LOG.debug("{} Stopping movement", getActor());
		clientStopMoving(null);
		LOG.debug("{} Auto attacking {}", getActor(), getAttackTarget());
		_actor.doAttack(getAttackTarget());
	}
	
	private void thinkCast()
	{
		L2Summon summon = (L2Summon) _actor;
		if (checkTargetLost(getCastTarget()))
		{
			setCastTarget(null);
			return;
		}
		boolean val = _startFollow;
		if (maybeMoveToPawn(getCastTarget(), _actor.getMagicalAttackRange(_skill)))
		{
			return;
		}
		clientStopMoving(null);
		summon.setFollowStatus(false);
		_startFollow = val;
		_actor.doCast(_skill);
        if (getAttackTarget() != null) {
            setIntention(AI_INTENTION_ATTACK);
        } else {
            setIntention(AI_INTENTION_IDLE);
        }
	}
	
	private void thinkPickUp()
	{
		if (checkTargetLost(getTarget()))
		{
			return;
		}
		if (maybeMoveToPawn(getTarget(), 36))
		{
			return;
		}
		((L2Summon) _actor).doPickupItem(getTarget());
        setIntention(AI_INTENTION_IDLE);
	}
	
	private void thinkInteract()
	{
		if (checkTargetLost(getTarget()))
		{
			return;
		}
		if (maybeMoveToPawn(getTarget(), 36))
		{
			return;
		}
		setIntention(AI_INTENTION_IDLE);
	}
	
	@Override
	protected void onEvtThink()
	{
		if (_thinking || _actor.isCastingNow() || _actor.isAllSkillsDisabled())
		{
			LOG.debug("{} is skipping think: {} all skills disabled {}", getActor(), _thinking, _actor.isAllSkillsDisabled());
			return;
		}
		_thinking = true;
		LOG.debug("{} is thinking {}", getActor(), getIntention());
		try {
			switch (getIntention()) {
				case AI_INTENTION_ATTACK:
					thinkAttack();
					break;
				case AI_INTENTION_CAST:
					thinkCast();
					break;
				case AI_INTENTION_PICK_UP:
					thinkPickUp();
					break;
				case AI_INTENTION_INTERACT:
					thinkInteract();
					break;
			}
		} catch (Exception e) {
			LOG.warn("{}: {} - onEvtThink() for {} failed!", getClass().getSimpleName(), this, getIntention(), e);
		}
		finally
		{
			_thinking = false;
		}
	}
	
	@Override
	protected void onEvtFinishCasting()
	{
		if (_lastAttack == null)
		{
			((L2Summon) _actor).setFollowStatus(_startFollow);
		}
		else
		{
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, _lastAttack);
			_lastAttack = null;
		}
	}
	
	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		super.onEvtAttacked(attacker);
		
		avoidAttack(attacker);
	}
	
	@Override
	protected void onEvtEvaded(L2Character attacker)
	{
		super.onEvtEvaded(attacker);
		
		avoidAttack(attacker);
	}
	
	private void avoidAttack(L2Character attacker)
	{
		// trying to avoid if summon near owner
		if ((((L2Summon) _actor).getOwner() != null) && (((L2Summon) _actor).getOwner() != attacker) && ((L2Summon) _actor).getOwner().isInsideRadius(_actor, 2 * AVOID_RADIUS, true, false))
		{
			_startAvoid = true;
		}
	}
	
	@Override
	public void run()
	{
		if (_startAvoid)
		{
			_startAvoid = false;
			
			if (!_clientMoving && !_actor.isDead() && !_actor.isMovementDisabled())
			{
				final int ownerX = ((L2Summon) _actor).getOwner().getX();
				final int ownerY = ((L2Summon) _actor).getOwner().getY();
				final double angle = Math.toRadians(Rnd.get(-90, 90)) + Math.atan2(ownerY - _actor.getY(), ownerX - _actor.getX());
				
				final int targetX = ownerX + (int) (AVOID_RADIUS * Math.cos(angle));
				final int targetY = ownerY + (int) (AVOID_RADIUS * Math.sin(angle));
				if (GeoData.getInstance().canMove(_actor.getX(), _actor.getY(), _actor.getZ(), targetX, targetY, _actor.getZ(), _actor.getInstanceId()))
				{
					moveTo(targetX, targetY, _actor.getZ());
				}
			}
		}
	}
	
	public void notifyFollowStatusChange()
	{
		_startFollow = !_startFollow;
		switch (getIntention())
		{
			case AI_INTENTION_ACTIVE:
			case AI_INTENTION_FOLLOW:
			case AI_INTENTION_IDLE:
			case AI_INTENTION_MOVE_TO:
			case AI_INTENTION_PICK_UP:
				((L2Summon) _actor).setFollowStatus(_startFollow);
		}
	}
	
	public void setStartFollowController(boolean val)
	{
		_startFollow = val;
	}
	
	@Override
	protected void onIntentionCast(Skill skill, L2Object target)
	{
		if (getIntention() == AI_INTENTION_ATTACK)
		{
			_lastAttack = getAttackTarget();
		}
		else
		{
			_lastAttack = null;
		}
		super.onIntentionCast(skill, target);
	}
	
	private void startAvoidTask()
	{
		if (_avoidTask == null)
		{
			_avoidTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, 100, 100);
		}
	}
	
	private void stopAvoidTask()
	{
		if (_avoidTask != null)
		{
			_avoidTask.cancel(false);
			_avoidTask = null;
		}
	}
	
	@Override
	public void stopAITask()
	{
		stopAvoidTask();
		super.stopAITask();
	}
}
