package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountStats;

import java.util.Objects;
import java.util.StringJoiner;

public class ScrollsStats {

    private ChanceCountStats normal;
    private ChanceCountStats blessed;

    public ScrollsStats() {
    }

    public ScrollsStats(ChanceCountStats normal, ChanceCountStats blessed) {
        this.normal = normal;
        this.blessed = blessed;
    }

    public ChanceCountStats getNormal() {
        return normal;
    }

    public ChanceCountStats getBlessed() {
        return blessed;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScrollsStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(normal))
                .add(Objects.toString(blessed))
                .toString();
    }

}
