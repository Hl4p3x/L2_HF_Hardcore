package com.l2jserver.gameserver.model.actor.templates.drop.stats.basic;

import java.util.Objects;
import java.util.StringJoiner;

public class ChanceCountStats {

    private CountStats counts;
    private ChanceStats chances;

    public ChanceCountStats() {
    }

    public ChanceCountStats(CountStats counts, ChanceStats chances) {
        this.counts = counts;
        this.chances = chances;
    }

    public CountStats getCounts() {
        return counts;
    }

    public ChanceStats getChances() {
        return chances;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChanceCountStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(counts))
                .add(Objects.toString(chances))
                .toString();
    }

}
