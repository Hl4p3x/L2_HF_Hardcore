package com.l2jserver.gameserver.model.items.craft;

import com.l2jserver.common.interfaces.IIdentifiable;
import java.util.Objects;

public class CraftResource implements IIdentifiable {

    private int id;
    private String name;
    private double price;
    private ResourceGrade resourceGrade;

    public CraftResource() {
    }

    public CraftResource(int id, String name, double price, ResourceGrade resourceGrade) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.resourceGrade = resourceGrade;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public ResourceGrade getResourceGrade() {
        return resourceGrade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CraftResource that = (CraftResource) o;
        return id == that.id &&
                Double.compare(that.price, price) == 0 &&
                Objects.equals(name, that.name) &&
                resourceGrade == that.resourceGrade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, resourceGrade);
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s) ", name, id, resourceGrade);
    }
}
