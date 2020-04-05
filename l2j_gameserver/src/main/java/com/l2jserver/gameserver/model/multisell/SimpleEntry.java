package com.l2jserver.gameserver.model.multisell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class SimpleEntry {

    protected List<Ingredient> products = new ArrayList<>();
    protected List<Ingredient> ingredients = new ArrayList<>();

    public SimpleEntry() {
    }

    public List<Ingredient> getProducts() {
        return products;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleEntry.class.getSimpleName() + "[", "]")
                .add(Objects.toString(products))
                .add(Objects.toString(ingredients))
                .toString();
    }
}
