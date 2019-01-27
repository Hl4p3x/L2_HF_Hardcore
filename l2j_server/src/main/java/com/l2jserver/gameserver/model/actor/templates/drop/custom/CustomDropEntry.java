package com.l2jserver.gameserver.model.actor.templates.drop.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropCategory;
import com.l2jserver.gameserver.model.actor.templates.drop.HasDropCategory;
import com.l2jserver.gameserver.model.actor.templates.drop.Range;

import java.util.Objects;
import java.util.StringJoiner;

public class CustomDropEntry implements HasDropCategory {

    private String name = "Other";
    @JsonProperty("level-range")
    private Range levelRange = Range.fromString("1-85");
    private DynamicDropCategory drop = DynamicDropCategory.empty();

    public CustomDropEntry() {
    }

    public CustomDropEntry(String name, Range levelRange, DynamicDropCategory drop) {
        this.name = name;
        this.levelRange = levelRange;
        this.drop = drop;
    }

    public Range getLevelRange() {
        return levelRange;
    }

    public DynamicDropCategory getDrop() {
        return drop;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomDropEntry that = (CustomDropEntry) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(levelRange, that.levelRange) &&
                Objects.equals(drop, that.drop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, levelRange, drop);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CustomDropEntry.class.getSimpleName() + "[", "]")
                .add(name)
                .add(Objects.toString(levelRange))
                .add(Objects.toString(drop))
                .toString();
    }

    public CustomDropEntry applyChanceMod(double chanceMod) {
        return new CustomDropEntry(name, levelRange, drop.applyChanceMod(chanceMod));
    }

}
