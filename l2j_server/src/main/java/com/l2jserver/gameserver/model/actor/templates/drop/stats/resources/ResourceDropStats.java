package com.l2jserver.gameserver.model.actor.templates.drop.stats.resources;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ResourceDropStats {

    private Map<ResourceGrade, DropStats> drop;

    public ResourceDropStats() {
    }

    public ResourceDropStats(Map<ResourceGrade, DropStats> drop) {
        this.drop = drop;
    }

    public Map<ResourceGrade, DropStats> getDrop() {
        return drop;
    }

    public DropStats getDropByResourceGrade(ResourceGrade resourceGrade) {
        return drop.getOrDefault(resourceGrade, DropStats.empty());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResourceDropStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(drop))
                .toString();
    }
}
