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
package com.l2jserver.gameserver.model.drops.strategy;

import com.l2jserver.Config;
import com.l2jserver.common.util.Rnd;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.drops.GeneralDropItem;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.L2Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Battlecruiser
 */
public interface IDropCalculationStrategy
{
	Logger LOG = Logger.getLogger(IDropCalculationStrategy.class.getName());

	IDropCalculationStrategy DEFAULT_STRATEGY = (item, victim, killer) -> calculateDrop(item, victim, item.getChance(victim, killer));

	static List<ItemHolder> calculateDrop(GeneralDropItem item, L2Character victim, double dropChance) {
		return calculateDrop(item, victim, dropChance, 1L);
	}

	static List<ItemHolder> calculateDrop(GeneralDropItem item, L2Character victim, double dropChance, long amountMultiplier) {
		String itemName = ItemTable.getInstance().getTemplate( item.getItemId()).getName();

		List<ItemHolder> items = new ArrayList<>();
		long totalAmount = 0L;
		for (long i = 0; i < amountMultiplier; i++) {
		  if (dropChance > (Rnd.nextDouble() * 100))	{
				int amountMultiply = 1;
				if (item.isPreciseCalculated() && (dropChance > 100))	{
					amountMultiply = (int) dropChance / 100;
					if ((dropChance % 100) > (Rnd.nextDouble() * 100)) {
						dropChance++;
					}
				}
				totalAmount += Rnd.get(item.getMin(victim), item.getMax(victim)) * amountMultiply;
			}
		}

		if (totalAmount > 0L) {
			return Collections.singletonList(new ItemHolder(item.getItemId(), totalAmount));
		} else {
			return null;
		}
	}

	IDropCalculationStrategy VALUABLE_STRATEGY = (item, victim, killer) -> calculateValuableDrop(item, victim, item.getChance(victim, killer));

	static List<ItemHolder> calculateValuableDrop(GeneralDropItem item, L2Character victim, double dropChance) {
		return calculateValuableDrop(item, victim, dropChance, 1L);
	}

	static List<ItemHolder> calculateValuableDrop(GeneralDropItem item, L2Character victim, double dropChance, long amountMultiplier) {
		long iterations = 0;
		if (item.getMin(victim) == item.getMax(victim) || item.getMin(victim) > item.getMax(victim)) {
			iterations = item.getMin(victim);
		} else {
			iterations = Rnd.get(item.getMin(victim), item.getMax(victim));
		}

		iterations *= amountMultiplier;

		String itemName = ItemTable.getInstance().getTemplate(item.getItemId()).getName();
		if (Config.VALUABLE_ITEMS_LIMIT_LOG && iterations > Config.VALUABLE_ITEMS_WARNING_LIMIT) {
			LOG.warning("Warning, valuable iterations calculation for large number of items " + iterations + "of " + itemName + " caused by kill of " + victim);
		}

		List<ItemHolder> results = new ArrayList<>();
		for(long i = 0; i < iterations; i++) {
			double random = Rnd.nextDouble() * 100;
			if (dropChance > random) {
				results.add(new ItemHolder(item.getItemId(), 1));
			}
		}

		return results.isEmpty() ? null : results;
	}

	static boolean isValuable(L2Item item) {
		return item.getType2() == L2Item.TYPE2_WEAPON ||
					 item.getType2() == L2Item.TYPE2_SHIELD_ARMOR ||
			 		 item.getType2() == L2Item.TYPE2_ACCESSORY;
	}

	List<ItemHolder> calculateDrops(GeneralDropItem item, L2Character victim, L2Character killer);

}
