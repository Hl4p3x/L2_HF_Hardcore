package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class DynamicDropCategory {

    private Set<Integer> ids;
    private DropStats stats;

    public DynamicDropCategory() {
    }

    public DynamicDropCategory(Set<Integer> ids, DropStats stats) {
        this.ids = ids;
        this.stats = stats;
    }

    public Set<Integer> getIds() {
        return ids;
    }

    public DropStats getStats() {
        return stats;
    }

    public static DynamicDropCategory empty() {
        return new DynamicDropCategory(new HashSet<>(), DropStats.empty());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DynamicDropCategory.class.getSimpleName() + "[", "]")
                .add(Objects.toString(ids))
                .add(Objects.toString(stats))
                .toString();
    }

}
