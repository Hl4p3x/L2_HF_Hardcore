package com.l2jserver.gameserver.model.actor.templates.drop.stats.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ResourceDropData {

    @JsonProperty("overgrade-multiplier")
    private Double overgradeMultiplier;

    private Map<ResourceGrade, ChanceCountPair> drop;

    public ResourceDropData() {
    }

    public ResourceDropData(Double overgradeMultiplier, Map<ResourceGrade, ChanceCountPair> drop) {
        this.overgradeMultiplier = overgradeMultiplier;
        this.drop = drop;
    }

    public Double getOvergradeMultiplier() {
        return overgradeMultiplier;
    }

    public Map<ResourceGrade, ChanceCountPair> getDrop() {
        return drop;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResourceDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(overgradeMultiplier))
                .add(Objects.toString(drop))
                .toString();
    }
}
