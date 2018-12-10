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
package com.l2jserver.gameserver.model.holders;

import com.l2jserver.gameserver.model.interfaces.IIdentifiable;

/**
 * A simple DTO for items; contains item ID and count.<br>
 * Extended by {@link ItemChanceHolder}, {@link QuestItemHolder}, {@link UniqueItemHolder}.
 * @author UnAfraid
 */
public class ItemHolder implements IIdentifiable
{
	private final int id;
	private final long count;

    public ItemHolder() {
        id = 0;
        count = 0L;
    }

	public ItemHolder(int id, long count)
	{
		this.id = id;
		this.count = count;
	}
	
	/**
	 * @return the ID of the item contained in this object
	 */
	@Override
	public int getId()
	{
		return id;
	}
	
	/**
	 * @return the count of items contained in this object
	 */
	public long getCount()
	{
		return count;
	}
	
	@Override
	public String toString()
	{
		return "[" + getClass().getSimpleName() + "] ID: " + id + ", count: " + count;
	}
}
