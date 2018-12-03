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
package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.gameplay.crystallization.CrystallizationHelper;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;

import java.util.Optional;

/**
 * This class ...
 * @version $Revision: 1.2.2.3.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestCrystallizeItem extends L2GameClientPacket
{
	private static final String _C__2F_REQUESTDCRYSTALLIZEITEM = "[C] 2F RequestCrystallizeItem";
	
	private int _objectId;
	private long _count;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readQ();
	}
	
	@Override
	protected void runImpl() {
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null) {
			_log.fine("RequestCrystallizeItem: activeChar was null");
			return;
		}

		if (!getClient().getFloodProtectors().getTransaction().tryPerformAction("crystallize")) {
			activeChar.sendMessage("You are crystallizing too fast.");
			return;
		}

		Optional<L2ItemInstance> itemToRemoveOptional = activeChar.getInventory().getItemByObjectIdPossibly(_objectId);
		if (!itemToRemoveOptional.isPresent()) {
			_log.warning("Player " + activeChar + " is trying to crystallize non existent object " + _objectId);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		L2ItemInstance itemToRemove = itemToRemoveOptional.get();

		boolean allowedToCrystallize = CrystallizationHelper.canItemBeCrystallized(activeChar, itemToRemove);
		if (!allowedToCrystallize) {
			return;
		}

		boolean validCount = CrystallizationHelper.validateCrystallizationCount(activeChar, _count, _objectId);
		if (!validCount) {
			return;
		}

		_count = CrystallizationHelper.normalizeCrystallizeCount(activeChar, _count, _objectId);

		boolean hasCrystallizationSkills = CrystallizationHelper.hasCrystallizationSkills(activeChar, _objectId);
		if (!hasCrystallizationSkills) {
			return;
		}

		boolean canPlayerCrystallizeNow = CrystallizationHelper.canPlayerCrystallizeNow(activeChar);
		if (!canPlayerCrystallizeNow) {
			return;
		}

		CrystallizationHelper.crystallizeItem(activeChar, _count, _objectId);
	}
	
	@Override
	public String getType()
	{
		return _C__2F_REQUESTDCRYSTALLIZEITEM;
	}
}
