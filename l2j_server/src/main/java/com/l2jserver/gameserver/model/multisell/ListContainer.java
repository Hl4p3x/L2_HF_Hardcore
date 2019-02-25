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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.interfaces.EnchantableItemObject;
import com.l2jserver.gameserver.model.multisell.dualcraft.DualcraftCombinator;
import com.l2jserver.gameserver.model.multisell.dualcraft.DualcraftTemplate;
import com.l2jserver.gameserver.model.multisell.dualcraft.DualcraftWeaponObject;
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
	private boolean isRemoveTransmogrification;
	private boolean isBestowTransmogrification;
	
	public ListContainer(int listId)
	{
		this.listId = listId;
	}

    public ListContainer(int listId, boolean applyTaxes, boolean maintainEnchantment, double useRate, List<Entry> entries, Set<Integer> npcsAllowed, boolean dualcraft) {
        this.listId = listId;
        this.applyTaxes = applyTaxes;
        this.maintainEnchantment = maintainEnchantment;
        this.useRate = useRate;
        this.entries = entries;
        this.npcsAllowed = npcsAllowed != null ? npcsAllowed : new HashSet<>();
        this.dualcraft = dualcraft;
    }

	public ListContainer(int listId, boolean applyTaxes, boolean maintainEnchantment, double useRate, List<Entry> entries) {
		this.listId = listId;
		this.applyTaxes = applyTaxes;
		this.maintainEnchantment = maintainEnchantment;
		this.useRate = useRate;
		this.entries = entries;
	}

	public boolean isRemoveTransmogrification() {
		return isRemoveTransmogrification;
	}

	public boolean isBestowTransmogrification() {
		return isBestowTransmogrification;
	}

	public void setRemoveTransmogrification(boolean removeTransmogrification) {
		isRemoveTransmogrification = removeTransmogrification;
	}

	public void setBestowTransmogrification(boolean bestowTransmogrification) {
		isBestowTransmogrification = bestowTransmogrification;
	}

	public static ListContainer prepareTransmogrificationRemove(int multisellId, L2Npc npc, L2PcInstance player, boolean maintainEnchantment) {
		List<L2ItemInstance> displayableEquipment = player.getInventory().getAllUnequippedDisplayables();
		List<L2ItemInstance> customDisplayables = displayableEquipment.stream().filter(L2ItemInstance::isCustomDisplayId).collect(Collectors.toList());

		double taxRate = calculateTaxRate(npc);
		boolean applyTaxes = calculateApplyTaxes(taxRate);

		List<Entry> entries = new ArrayList<>();
		for (L2ItemInstance itemInstance : customDisplayables) {
			Ingredient ingredient = Ingredient.from(itemInstance.getId(), 1, false, false);
			ingredient.setItemInfo(new ItemInfo(itemInstance));
			List<Ingredient> ingredients = List.of(ingredient);

			Ingredient product = Ingredient.from(itemInstance.getId(), 1, false, false);
			product.setItemInfo(new ItemInfo(itemInstance));
			List<Ingredient> products = List.of(product);

			entries.add(Entry.prepareEntry(itemInstance.getObjectId(), ingredients, products, itemInstance, applyTaxes, maintainEnchantment, taxRate));
		}
		ListContainer result = new ListContainer(multisellId, applyTaxes, maintainEnchantment, taxRate, entries, Set.of(npc.getId()), false);
		result.setRemoveTransmogrification(true);
		return result;
	}

	public static ListContainer prepareTransmogrificationBestow(int multisellId, L2Npc npc, L2PcInstance player, List<ItemHolder> price, boolean maintainEnchantment) {
		List<L2ItemInstance> displayableEquipment = player.getInventory().getAllUnequippedDisplayables();
		List<L2ItemInstance> customizableDisplayables = displayableEquipment.stream().filter(L2ItemInstance::isNotCustomDisplayId).collect(Collectors.toList());
		Multimap<Integer, L2ItemInstance> displayablesByBodyPart = Multimaps.index(customizableDisplayables, itemInstance -> Objects.requireNonNull(itemInstance).getItem().getBodyPart());

		double taxRate = calculateTaxRate(npc);
		boolean applyTaxes = calculateApplyTaxes(taxRate);

		List<Ingredient> priceIngredients = price.stream().map(holder -> Ingredient.from(holder.getId(), holder.getCount(), true, false)).collect(Collectors.toList());


		List<Entry> entries = new ArrayList<>();
		int entryIdCounter = 1;
		for (L2ItemInstance itemInstance : customizableDisplayables) {
			Collection<L2ItemInstance> bodypartDisplayableDonors = displayablesByBodyPart.get(itemInstance.getItem().getBodyPart());
			if (itemInstance.isWeapon()) {
				bodypartDisplayableDonors = bodypartDisplayableDonors.stream().filter(donor -> itemInstance.getWeaponItem().getItemType().equals(donor.getWeaponItem().getItemType())).collect(Collectors.toList());
			}

			for (L2ItemInstance donorDisplayable : bodypartDisplayableDonors) {
				if (donorDisplayable.getId() == itemInstance.getId()) {
					continue;
				}

				List<Ingredient> ingredients = new ArrayList<>();
				Ingredient mainIngredient = Ingredient.from(itemInstance.getId(), 1, false, false);
				mainIngredient.setItemInfo(new ItemInfo(itemInstance));
				ingredients.add(mainIngredient);

				Ingredient donorIngredient = Ingredient.from(donorDisplayable.getId(), 1, false, false);
				donorIngredient.setItemInfo(new ItemInfo(donorDisplayable));
				ingredients.add(donorIngredient);
				ingredients.addAll(priceIngredients);

				List<Ingredient> products = List.of(Ingredient.from(itemInstance.getId(), 1, false, false));

				entries.add(Entry.prepareEntry(entryIdCounter, ingredients, products, itemInstance, applyTaxes, maintainEnchantment, taxRate));
				entryIdCounter += 1;
			}
		}
		ListContainer result = new ListContainer(multisellId, applyTaxes, maintainEnchantment, taxRate, entries, Set.of(npc.getId()), false);
		result.setBestowTransmogrification(true);
		return result;
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

    public Set<Integer> getNpcsAllowed() {
        return npcsAllowed;
    }

	public boolean isNpcOnly() {
		return !npcsAllowed.isEmpty();
	}

	private static double calculateTaxRate(L2Npc npc) {
		if (npc != null &&
				npc.getIsInTown() &&
                (npc.getCastle() != null && npc.getCastle().getOwnerClanId() > 0)) {
			return npc.getCastle().getTaxRate();
		} else {
			return 0D;
		}
	}

	public static ListContainer prepareDualcraftMultisell(ListContainer template, L2PcInstance player, L2Npc npc) {
		if (player == null) {
			throw new IllegalStateException("Cannot prepare multisell " + template + " for null player");
		}

		DualcraftCombinator dualcraftCombinator = new DualcraftCombinator();

		List<L2ItemInstance> uniqueItemInstances = player.getInventory().getAllInventoryWeapons();
		Set<Integer> inventoryItemTemplateIds = uniqueItemInstances.stream().map(L2ItemInstance::getId).collect(Collectors.toSet());

		List<EnchantableItemObject> uniqueItemInstancesWrapped = new ArrayList<>(uniqueItemInstances);
		double taxRate = calculateTaxRate(npc);
		boolean applyTaxes = calculateApplyTaxes(taxRate);

		List<Entry> entries = new LinkedList<>();
		for (Entry entry : template.getEntries()) {
			List<Ingredient> dualWeaponIngredients = entry.getIngredients().stream().filter(Ingredient::isArmorOrWeapon).collect(Collectors.toList());
			if (dualWeaponIngredients.size() != 2) {
				LOG.warn("Dual Weapon Craft {} has incorrect weapon ingredient count {}", entry, dualWeaponIngredients.size());
				continue;
			}

			if (entry.getProducts().size() != 1) {
				LOG.warn("Dual Weapon Craft {} has incorrect product count {}", entry.getProducts().size());
				continue;
			}

			int leftWeaponTemplateId = dualWeaponIngredients.get(0).getItemId();
			int rightWeaponTemplateId = dualWeaponIngredients.get(1).getItemId();

			if (!inventoryItemTemplateIds.contains(leftWeaponTemplateId) || !inventoryItemTemplateIds.contains(rightWeaponTemplateId)) {
				LOG.debug("Skipping Dual Weapon Craft {} because player does not have relative ingredients", entry);
				continue;
			}

			DualcraftTemplate dualcraftTemplate = new DualcraftTemplate(leftWeaponTemplateId, rightWeaponTemplateId, entry.getProducts().get(0).getItemId());
			List<DualcraftWeaponObject> dualcraftWeaponObjects = dualcraftCombinator.findPossibleEnchantmentLevels(dualcraftTemplate, uniqueItemInstancesWrapped);

			for (DualcraftWeaponObject dualcraftWeaponObject : dualcraftWeaponObjects) {
				entries.add(Entry.dualcraftEntry(entry, dualcraftWeaponObject, applyTaxes, taxRate));
			}
		}

        return new ListContainer(template.getListId(), applyTaxes, template.getMaintainEnchantment(), template.getUseRate(), entries, template.getNpcsAllowed(), template.isDualcraft());
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
		// Item ID is not unique

        Multimap<Integer, L2ItemInstance> inventoryItems = Multimaps.index(items.stream()
                .filter(item -> !item.isEquipped() && (item.isArmor() || item.isWeapon()))
                .collect(Collectors.toList()), L2ItemInstance::getId);

        for (Entry ent : template.getEntries()) {
            for (Ingredient ing : ent.getIngredients()) {
                inventoryItems.get(ing.getItemId()).stream().findFirst()
                        .map(item -> entries.add(Entry.prepareEntry(ent, item, applyTaxes, template.getMaintainEnchantment(), taxRate)));
			}
		}

        return new ListContainer(template.getListId(), applyTaxes, template.getMaintainEnchantment(), template.getUseRate(), entries, template.getNpcsAllowed(), template.isDualcraft());
	}

	public static ListContainer prepareFullMultisell(ListContainer template, L2Npc npc) {
		double taxRate = calculateTaxRate(npc);
		boolean applyTaxes = calculateApplyTaxes(taxRate);

		List<Entry> entries = new ArrayList<>(template.getEntries().size());
		for (Entry ent : template.getEntries()) {
			entries.add(Entry.prepareEntry(ent, null, applyTaxes, false, taxRate));
		}

        return new ListContainer(template.getListId(), applyTaxes, template.getMaintainEnchantment(), template.getUseRate(), entries, template.getNpcsAllowed(), template.isDualcraft());
	}

	private static boolean calculateApplyTaxes(double taxRate) {
		return taxRate > 0D;
	}

	@JsonCreator
	public static ListContainer from(@JsonProperty("list_id") int listId, @JsonProperty("apply_taxes") boolean applyTaxes,
                                     @JsonProperty("maintain_enchantment") boolean maintainEnchantment,
                                     @JsonProperty("use_rate") double useRate,
                                     @JsonProperty("boolean dualcraft") boolean dualcraft,
                                     @JsonProperty("npcs") Set<Integer> npcsAllowed,
                                     @JsonProperty("items") List<SimpleEntry> items) {
		int entryId = 1;
		List<Entry> entries = new ArrayList<>();
		for (SimpleEntry item : items) {
			entries.add(new Entry(entryId, item.getProducts(), item.getIngredients()));
			entryId += 1;
		}

		return new ListContainer(listId, applyTaxes, maintainEnchantment, useRate, entries, npcsAllowed, dualcraft);
	}

}