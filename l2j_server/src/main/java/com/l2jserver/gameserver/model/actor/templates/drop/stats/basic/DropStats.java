package com.l2jserver.gameserver.model.actor.templates.drop.stats.basic;

import com.l2jserver.gameserver.model.actor.templates.drop.Range;

import java.util.Objects;
import java.util.StringJoiner;

public class DropStats {

    private Range stacks = Range.one();
    private Double chance = 0D;
    private Range count = Range.one();

    public DropStats() {
    }

    public DropStats(Double chance) {
        this.chance = chance;
    }

    public DropStats(Range stacks, Double chance, Range count) {
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

    public static DropStats empty() {
        return new DropStats();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DropStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(stacks))
                .add(Objects.toString(chance))
                .add(Objects.toString(count))
                .toString();
    }

    public boolean isSingleStack() {
        return stacks.equalsTo(1);
    }

    public DropStats applyChanceMod(double chanceMod) {
        return new DropStats(stacks, Math.min(chance * chanceMod, 100D), count);
    }

}
