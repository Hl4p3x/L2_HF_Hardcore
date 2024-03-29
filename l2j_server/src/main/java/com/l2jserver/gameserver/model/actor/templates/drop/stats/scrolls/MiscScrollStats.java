package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;

import java.util.Objects;
import java.util.StringJoiner;

public class MiscScrollStats {

    private DropStats normal;
    private DropStats blessed;

    public MiscScrollStats() {
    }

    public MiscScrollStats(DropStats normal, DropStats blessed) {
        this.normal = normal;
        this.blessed = blessed;
    }

    public DropStats getNormal() {
        return normal;
    }

    public DropStats getBlessed() {
        return blessed;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MiscScrollStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(normal))
                .add(Objects.toString(blessed))
                .toString();
    }

}
