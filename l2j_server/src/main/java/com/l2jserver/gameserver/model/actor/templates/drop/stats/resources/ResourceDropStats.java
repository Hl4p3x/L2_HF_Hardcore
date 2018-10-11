package com.l2jserver.gameserver.model.actor.templates.drop.stats.resources;

import com.l2jserver.gameserver.model.actor.templates.drop.Range;

import java.util.Objects;
import java.util.StringJoiner;

public class ResourceDropStats {

    private Range stacks;
    private Double chance;
    private Range count;

    public ResourceDropStats() {
    }

    public ResourceDropStats(Range stacks, Double chance, Range count) {
        this.stacks = stacks;
        this.chance = chance;
        this.count = count;
    }

    public Range getStacks() {
        return stacks;
    }

    public Double getChance() {
        return chance;
    }

    public Range getCount() {
        return count;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResourceDropStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(stacks))
                .add(Objects.toString(chance))
                .add(Objects.toString(count))
                .toString();
    }

}
