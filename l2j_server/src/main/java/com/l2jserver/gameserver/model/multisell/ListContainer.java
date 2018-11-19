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
package com.l2jserver.gameserver.model.multisell;

import com.google.common.base.Functions;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author DS
 */
public class ListContainer
{

	private static final Logger LOG = LoggerFactory.getLogger(ListContainer.class);

	private final int listId;
	private boolean applyTaxes = false;
	private boolean maintainEnchantment = false;
	private double useRate = 1.0;

	private List<Entry> entries = new ArrayList<>();
	private Set<Integer> npcsAllowed = new HashSet<>();

	private boolean dualcraft = false;
	
	public ListContainer(int listId)
	{
		this.listId = listId;
	}

	public ListContainer(int listId, boolean applyTaxes, boolean maintainEnchantment, double useRate, List<Entry> entries) {
		this.listId = listId;
		this.applyTaxes = applyTaxes;
		this.maintainEnchantment = maintainEnchantment;
		this.useRate = useRate;
		this.entries = entries;
	}

	public boolean isDualcraft() {
		return dualcraft;
	}

	public void setDualcraft(boolean dualcraft) {
		this.dualcraft = dualcraft;
	}

	public final List<Entry> getEntries()
	{
		return entries;
	}
	
	public final int getListId()
	{
		return listId;
	}
	
	public final void setApplyTaxes(boolean applyTaxes)
	{
		this.applyTaxes = applyTaxes;
	}
	
	public final boolean getApplyTaxes()
	{
		return applyTaxes;
	}
	
	public final void setMaintainEnchantment(boolean maintainEnchantment)
	{
		this.maintainEnchantment = maintainEnchantment;
	}
	
	public double getUseRate()
	{
		return useRate;
	}
	
	/**
	 * Set this to create multisell with increased products, all product counts will be multiplied by the rate specified.<br>
	 * <b>NOTE:</b> It affects only parser, it won't change values of already parsed multisell since MultiSells' parseEntry method handles this feature.
	 * @param rate
	 */
	public void setUseRate(double rate)
	{
		this.useRate = rate;
	}
	
	public final boolean getMaintainEnchantment()
	{
		return maintainEnchantment;
	}

	public void allowNpc(int npcId) {
		npcsAllowed.add(npcId);
	}

	public boolean isNpcNotAllowed(int npcId) {
		return !npcsAllowed.contains(npcId);
	}

	public boolean isNpcOnly() {
		return !npcsAllowed.isEmpty();
	}

	private static double calculateTaxRate(L2Npc npc) {
		if (npc != null &&
				npc.getIsInTown() &&
				(npc.getCastle() != null && npc.getCastle().getOwnerId() > 0)) {
			return npc.getCastle().getTaxRate();
		} else {
			return 0D;
		}
	}

	private static ListContainer prepareDualcraftMultisell(ListContainer template, L2PcInstance player, L2Npc npc) {
		if (player == null) {
			throw new IllegalStateException("Cannot prepare multisell " + template + " for null player");
		}

		List<L2ItemInstance> uniqueItems = player.getInventory().getUniqueItemsByEnchantLevel(false, false, false);

		double taxRate = calculateTaxRate(npc);
		boolean applyTaxes = calculateApplyTaxes(taxRate);


		List<L2ItemInstance> craftableDuals = new ArrayList<>();
		for (Entry entry : template.getEntries()) {
			List<Ingredient> dualWeaponIngredients = entry.getIngredients().stream().filter(Ingredient::isArmorOrWeapon).collect(Collectors.toList());
			if (dualWeaponIngredients.size() != 2) {
				LOG.warn("Dual Weapon craft {} has incorrect weapon ingredient count {}", entry, dualWeaponIngredients.size());
				continue;
			}


		}
		return craftableDuals;
	}

	public static ListContainer prepareInventoryOnlyMultisell(ListContainer template, L2PcInstance player, L2Npc npc) {
		if (player == null) {
			throw new IllegalStateException("Cannot prepare multisell " + template + " for null player");
		}

		final List<L2ItemInstance> items;
		if (template.getMaintainEnchantment()) {
			items = player.getInventory().getUniqueItemsByEnchantLevel(false, false, false);
		} else {
			items = player.getInventory().getUniqueItems(false, false, false);
		}

		double taxRate = calculateTaxRate(npc);
		boolean applyTaxes = calculateApplyTaxes(taxRate);

		List<Entry> entries = new LinkedList<>();
		Map<Integer, L2ItemInstance> inventoryItems = items.stream()
                .filter(item -> !item.isEquipped() && (item.isArmor() || item.isWeapon()))
                .collect(Collectors.toMap(L2ItemInstance::getId, Functions.identity()));

        for (Entry ent : template.getEntries()) {
            for (Ingredient ing : ent.getIngredients()) {
                Optional.ofNullable(inventoryItems.get(ing.getItemId()))
                        .map(item -> entries.add(Entry.prepareEntry(ent, item, applyTaxes, template.getMaintainEnchantment(), taxRate)));
			}
		}

		return new ListContainer(template.getListId(), applyTaxes, template.getMaintainEnchantment(), template.getUseRate(), entries);
	}

	public static ListContainer prepareFullMultisell(ListContainer template, L2Npc npc) {
		double taxRate = calculateTaxRate(npc);
		boolean applyTaxes = calculateApplyTaxes(taxRate);

		List<Entry> entries = new ArrayList<>(template.getEntries().size());
		for (Entry ent : template.getEntries()) {
			entries.add(Entry.prepareEntry(ent, null, applyTaxes, false, taxRate));
		}

		return new ListContainer(template.getListId(), applyTaxes, template.getMaintainEnchantment(), template.getUseRate(), entries);
	}

	private static boolean calculateApplyTaxes(double taxRate) {
		return taxRate > 0D;
	}

}