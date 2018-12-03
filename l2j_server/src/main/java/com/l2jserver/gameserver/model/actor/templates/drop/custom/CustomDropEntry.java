package com.l2jserver.gameserver.model.actor.templates.drop.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropCategory;
import com.l2jserver.gameserver.model.actor.templates.drop.Range;

import java.util.Objects;
import java.util.StringJoiner;

public class CustomDropEntry {

    @JsonProperty("level-range")
    private Range levelRange = Range.fromString("1-85");
    private DynamicDropCategory drop = DynamicDropCategory.empty();

    public CustomDropEntry() {
    }

    public CustomDropEntry(Range levelRange, DynamicDropCategory drop) {
        this.levelRange = levelRange;
        this.drop = drop;
    }

    public Range getLevelRange() {
        return levelRange;
    }

    public DynamicDropCategory getDrop() {
        return drop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomDropEntry that = (CustomDropEntry) o;
        return Objects.equals(levelRange, that.levelRange) &&
                Objects.equals(drop, that.drop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(levelRange, drop);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CustomDropEntry.class.getSimpleName() + "[", "]")
                .add(Objects.toString(levelRange))
                .add(Objects.toString(drop))
                .toString();
    }
}
