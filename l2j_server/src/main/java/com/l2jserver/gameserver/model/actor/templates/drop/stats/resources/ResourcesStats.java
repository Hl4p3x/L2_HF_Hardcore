package com.l2jserver.gameserver.model.actor.templates.drop.stats.resources;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.CountStats;

import java.util.Objects;
import java.util.StringJoiner;

public class ResourcesStats {

    private Double overgradeMultiplier;
    private CountStats count;
    private ChanceStats chances;

    public ResourcesStats() {
    }

    public ResourcesStats(Double overgradeMultiplier, CountStats count, ChanceStats chances) {
        this.overgradeMultiplier = overgradeMultiplier;
        this.count = count;
        this.chances = chances;
    }

    public Double getOvergradeMultiplier() {
        return overgradeMultiplier;
    }

    public CountStats getCount() {
        return count;
    }

    public ChanceStats getChances() {
        return chances;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResourcesStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(overgradeMultiplier))
                .add(Objects.toString(count))
                .add(Objects.toString(chances))
                .toString();
    }

}
