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

import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.interfaces.EnchantableItemObject;
import com.l2jserver.gameserver.model.multisell.dualcraft.DualcraftWeaponObject;
import com.l2jserver.util.CollectionUtil;
import com.l2jserver.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.l2jserver.gameserver.model.itemcontainer.Inventory.ADENA_ID;

/**
 * @author DS
 */
public class Entry
{
	protected int entryId;
	protected boolean stackable = true;

	protected List<Ingredient> products = new ArrayList<>();
	protected List<Ingredient> ingredients = new ArrayList<>();

	private long taxAmount = 0;

	public Entry(int entryId) {
		this.entryId = entryId;
	}

	public Entry(int entryId, List<Ingredient> products, List<Ingredient> ingredients) {
		this.entryId = entryId;
		this.products = products;
		this.ingredients = ingredients;
	}

	public Entry(int entryId, boolean stackable, List<Ingredient> products, List<Ingredient> ingredients, long taxAmount) {
		this.entryId = entryId;
		this.stackable = stackable;
		this.products = products;
		this.ingredients = ingredients;
		this.taxAmount = taxAmount;
	}

	public final void setEntryId(int id)
	{
		entryId = id;
	}
	
	public final int getEntryId()
	{
		return entryId;
	}
	
	public final void addProduct(Ingredient product)
	{
		products.add(product);
		
		if (!product.isStackable())
		{
			stackable = false;
		}
	}
	
	public final List<Ingredient> getProducts()
	{
		return products;
	}
	
	public final void addIngredient(Ingredient ingredient)
	{
		ingredients.add(ingredient);
	}
	
	public final List<Ingredient> getIngredients()
	{
		return ingredients;
	}
	
	public final boolean isStackable()
	{
		return stackable;
	}


	public final long getTaxAmount() {
		return taxAmount;
	}

    private static long calculateTaxAmount(List<Ingredient> ingredients, double taxRate) {
		long taxAmount = 0;

		for (Ingredient ing : ingredients) {
			if (ing.getItemId() == ADENA_ID) {
                taxAmount += Math.round(ing.getItemCount() * (taxRate / 100));
			}
		}

        return taxAmount;
	}

	private static Ingredient findAndSetEnchant(Ingredient ingredient, EnchantableItemObject enchantableItemObject) {
		Ingredient newIngredient = ingredient.getCopy();
		if (newIngredient.getItemId() == enchantableItemObject.getItemId()) {
			newIngredient.setItemInfo(new ItemInfo(enchantableItemObject.getEnchantLevel()));
		}
		return newIngredient;
	}

	public static Entry dualcraftEntry(Entry template, DualcraftWeaponObject dualcraftWeaponObject, boolean applyTaxes, double taxRate) {
		int entryId = template.getEntryId() * 100000;
		entryId += dualcraftWeaponObject.getEnchantLevel();

		Pair<List<Ingredient>, List<Ingredient>> ingredients = CollectionUtil.splitBy(template.ingredients, (Ingredient item) -> item.getItemId() == dualcraftWeaponObject.getLeftWeaponObject().getItemId() ||
				item.getItemId() == dualcraftWeaponObject.getRightWeaponObject().getItemId());

		List<Ingredient> dualWeaponIngredients = ingredients.getLeft();
		if (dualWeaponIngredients.size() != 2) {
			throw new IllegalStateException("Dualcraft has to have exactly two weapon ingredients");
		}

		List<Ingredient> newDualWeaponIngredients = new ArrayList<>();
		newDualWeaponIngredients.add(findAndSetEnchant(dualWeaponIngredients.get(0), dualcraftWeaponObject.getLeftWeaponObject()));
		newDualWeaponIngredients.add(findAndSetEnchant(dualWeaponIngredients.get(1), dualcraftWeaponObject.getRightWeaponObject()));

		List<Ingredient> newIngredients = new ArrayList<>(newDualWeaponIngredients);
		for (Ingredient ing : ingredients.getRight()) {
			Ingredient ingredientCopy = ing.getCopy();
			newIngredients.add(ingredientCopy);
		}

		long taxAmount = 0L;
		if (applyTaxes) {
            taxAmount = calculateTaxAmount(newIngredients, taxRate);
            Optional<Ingredient> itemPrice = newIngredients.stream().filter(ingredient -> ingredient.getItemId() == ADENA_ID).findFirst();
            if (itemPrice.isPresent()) {
                itemPrice.get().setItemCount(itemPrice.get().getItemCount() + taxAmount);
			}
		}

		Ingredient product = template.getProducts().stream()
				.filter(templateProduct -> templateProduct.getItemId() == dualcraftWeaponObject.getDualTemplateId()).findFirst().orElseThrow(() -> new IllegalStateException("Could not find matching product item " + dualcraftWeaponObject.getDualTemplateId()));
		Ingredient newProduct = product.getCopy();
		newProduct.setItemInfo(new ItemInfo(dualcraftWeaponObject.getEnchantLevel()));

		List<Ingredient> newProducts = Collections.singletonList(newProduct);

		return new Entry(entryId, false, newProducts, newIngredients, taxAmount);
	}

	public static Entry prepareEntry(int originalEntryId, List<Ingredient> ingredients, List<Ingredient> products, L2ItemInstance item, boolean applyTaxes, boolean maintainEnchantment, double taxRate) {
		int entryId = originalEntryId * 100000;
		if (maintainEnchantment && (item != null)) {
			entryId += item.getEnchantLevel();
		}

		ItemInfo info = null;

		List<Ingredient> newIngredients = new ArrayList<>(ingredients.size());
		for (Ingredient ing : ingredients) {
			if (maintainEnchantment && item != null && ing.isArmorOrWeapon() && item.getId() == ing.getItemId()) {
				info = new ItemInfo(item);
				final Ingredient newIngredient = ing.getCopy();
				newIngredient.setItemInfo(info);
				newIngredients.add(newIngredient);
			} else {
				newIngredients.add(ing.getCopy());
			}
		}

		long taxAmount = 0L;
		if (applyTaxes) {
			taxAmount = calculateTaxAmount(newIngredients, taxRate);
			Optional<Ingredient> itemPrice = newIngredients.stream().filter(ingredient -> ingredient.getItemId() == ADENA_ID).findFirst();
			if (itemPrice.isPresent()) {
				itemPrice.get().setItemCount(itemPrice.get().getItemCount() + taxAmount);
			}
		}

		boolean stackable = true;
		// now copy products
		List<Ingredient> newProducts = new ArrayList<>(products.size());
		for (Ingredient ing : products) {
			if (!ing.isStackable()) {
				stackable = false;
			}

			final Ingredient newProduct = ing.getCopy();
			if (maintainEnchantment && ing.isArmorOrWeapon()) {
				newProduct.setItemInfo(info);
			} else if (ing.isArmorOrWeapon() && (ing.getTemplate().getDefaultEnchantLevel() > 0)) {
				info = new ItemInfo(ing.getTemplate().getDefaultEnchantLevel());
				newProduct.setItemInfo(info);
			}
			newProducts.add(newProduct);
		}

		return new Entry(entryId, stackable, newProducts, newIngredients, taxAmount);

	}

	public static Entry prepareEntry(Entry template, L2ItemInstance item, boolean applyTaxes, boolean maintainEnchantment, double taxRate) {
		return prepareEntry(template.getEntryId(), template.getIngredients(), template.getProducts(), item, applyTaxes, maintainEnchantment, taxRate);
	}
}