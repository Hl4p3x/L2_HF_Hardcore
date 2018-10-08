package com.l2jserver.gameserver.model.actor.templates.drop.stats.basic;

import com.l2jserver.gameserver.model.actor.templates.drop.Range;

import java.util.Objects;
import java.util.StringJoiner;

public class ChanceCountPair {

    private Double chance;
    private Range count;

    public ChanceCountPair() {
    }

    public ChanceCountPair(Double chance, Range count) {
        this.chance = chance;
        this.count = count;
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
