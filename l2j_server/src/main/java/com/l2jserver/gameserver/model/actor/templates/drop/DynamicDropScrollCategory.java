package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;

import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropScrollCategory {

    private ScrollGrade scrollGrade;
    private DynamicDropCategory normal;
    private DynamicDropCategory blessed;

    public DynamicDropScrollCategory() {
    }

    public DynamicDropScrollCategory(ScrollGrade scrollGrade, DynamicDropCategory normal, DynamicDropCategory blessed) {
        this.scrollGrade = scrollGrade;
        this.normal = normal;
        this.blessed = blessed;
    }

    public static DynamicDropScrollCategory empty() {
        return new DynamicDropScrollCategory(ScrollGrade.UNSET, DynamicDropCategory.empty(), DynamicDropCategory.empty());
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
                .add(Objects.toString(scrollGrade))
                .add(Objects.toString(normal))
                .add(Objects.toString(blessed))
                .toString();
    }

    public DynamicDropScrollCategory applyChanceMod(double chanceMod) {
        return new DynamicDropScrollCategory(scrollGrade, normal.applyChanceMod(chanceMod), blessed.applyChanceMod(chanceMod));
    }

}
