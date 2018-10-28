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

import java.util.ArrayList;
import java.util.List;

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

	public static Entry prepareEntry(Entry template, L2ItemInstance item, boolean applyTaxes, boolean maintainEnchantment, double taxRate) {
		int entryId = template.getEntryId() * 100000;
		if (maintainEnchantment && (item != null)) {
			entryId += item.getEnchantLevel();
		}

		ItemInfo info = null;
		long adenaAmount = 0;
		long taxAmount = 0;

		List<Ingredient> ingredients = new ArrayList<>(template.getIngredients().size());
		for (Ingredient ing : template.getIngredients()) {
			if (ing.getItemId() == ADENA_ID) {
				// Tax ingredients added only if taxes enabled
				if (ing.isTaxIngredient() && applyTaxes) {
					// if taxes are to be applied, modify/add the adena count based on the template adena/ancient adena count
					taxAmount += Math.round(ing.getItemCount() * taxRate);
				} else {
					adenaAmount += ing.getItemCount();
				}
			} else if (maintainEnchantment && item != null && ing.isArmorOrWeapon()) {
				info = new ItemInfo(item);
				final Ingredient newIngredient = ing.getCopy();
				newIngredient.setItemInfo(info);
				ingredients.add(newIngredient);
			} else {
				ingredients.add(ing.getCopy());
			}
		}

		// now add the adena, if any.
		adenaAmount += taxAmount; // do not forget tax
		if (adenaAmount > 0) {
			ingredients.add(new Ingredient(ADENA_ID, adenaAmount, false, false));
		}

		boolean stackable = true;
		// now copy products
		List<Ingredient> products = new ArrayList<>(template.getProducts().size());
		for (Ingredient ing : template.getProducts()) {
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
			products.add(newProduct);
		}

		return new Entry(entryId, stackable, products, ingredients, taxAmount);
	}
}