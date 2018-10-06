package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;

import java.util.Objects;
import java.util.StringJoiner;

public class MiscScrollStats {

    private ChanceCountPair normal;
    private ChanceCountPair blessed;

    public MiscScrollStats() {
    }

    public MiscScrollStats(ChanceCountPair normal, ChanceCountPair blessed) {
        this.normal = normal;
        this.blessed = blessed;
    }

    public ChanceCountPair getNormal() {
        return normal;
    }

    public ChanceCountPair getBlessed() {
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
