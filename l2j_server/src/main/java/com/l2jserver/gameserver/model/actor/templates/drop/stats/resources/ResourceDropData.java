package com.l2jserver.gameserver.model.actor.templates.drop.stats.resources;

import com.l2jserver.gameserver.model.items.craft.ResourceGrade;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ResourceDropData {

    private Map<ResourceGrade, ResourceDropStats> drop;

    public ResourceDropData() {
    }

    public ResourceDropData(Map<ResourceGrade, ResourceDropStats> drop) {
        this.drop = drop;
    }

    public Map<ResourceGrade, ResourceDropStats> getDrop() {
        return drop;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResourceDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(drop))
                .toString();
    }
}
