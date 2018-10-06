package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import java.util.Objects;
import java.util.StringJoiner;

public class ScrollsStats {

    private ScrollChanceCountStats normal;
    private ScrollChanceCountStats blessed;

    public ScrollsStats() {
    }

    public ScrollsStats(ScrollChanceCountStats normal, ScrollChanceCountStats blessed) {
        this.normal = normal;
        this.blessed = blessed;
    }

    public ScrollChanceCountStats getNormal() {
        return normal;
    }

    public ScrollChanceCountStats getBlessed() {
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
