package com.l2jserver.gameserver.model.actor.templates.drop;

import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropScrollCategory {

    private DynamicDropCategory normal;
    private DynamicDropCategory blessed;

    public DynamicDropScrollCategory() {
    }

    public DynamicDropScrollCategory(DynamicDropCategory normal, DynamicDropCategory blessed) {
        this.normal = normal;
        this.blessed = blessed;
    }

    public DynamicDropCategory getNormal() {
        return normal;
    }

    public DynamicDropCategory getBlessed() {
        return blessed;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DynamicDropScrollCategory.class.getSimpleName() + "[", "]")
                .add(Objects.toString(normal))
                .add(Objects.toString(blessed))
                .toString();
    }

}
