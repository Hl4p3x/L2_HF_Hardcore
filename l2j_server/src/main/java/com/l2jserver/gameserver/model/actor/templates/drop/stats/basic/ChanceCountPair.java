package com.l2jserver.gameserver.model.actor.templates.drop.stats.basic;

import com.l2jserver.gameserver.model.actor.templates.drop.Range;

import java.util.Objects;
import java.util.StringJoiner;

public class ChanceCountPair {

    private Range count;
    private Double chance;

    public ChanceCountPair() {
    }

    public ChanceCountPair(Range count, Double chance) {
        this.count = count;
        this.chance = chance;
    }

    public Range getCount() {
        return count;
    }

    public Double getChance() {
        return chance;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChanceCountPair.class.getSimpleName() + "[", "]")
                .add(Objects.toString(count))
                .add(Objects.toString(chance))
                .toString();
    }

}
