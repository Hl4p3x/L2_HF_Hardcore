package com.l2jserver.gameserver.model.actor.templates.drop.stats.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.actor.templates.drop.Range;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ResourcesStats {

    @JsonProperty("overgrade-multiplier")
    private Double overgradeMultiplier;

    private Map<ResourceGrade, Range> count;
    private Map<ResourceGrade, Double> chance;

    public ResourcesStats() {
    }

    public ResourcesStats(Double overgradeMultiplier, Map<ResourceGrade, Range> count, Map<ResourceGrade, Double> chance) {
        this.overgradeMultiplier = overgradeMultiplier;
        this.count = count;
        this.chance = chance;
    }

    public Double getOvergradeMultiplier() {
        return overgradeMultiplier;
    }

    public Map<ResourceGrade, Range> getCount() {
        return count;
    }

    public Map<ResourceGrade, Double> getChance() {
        return chance;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResourcesStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(overgradeMultiplier))
                .add(Objects.toString(count))
                .add(Objects.toString(chance))
                .toString();
    }

}
