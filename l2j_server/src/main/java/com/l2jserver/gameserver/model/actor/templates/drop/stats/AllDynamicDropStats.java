package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import java.util.Objects;
import java.util.StringJoiner;

public class AllDynamicDropStats {

    private DynamicDropStats mobs;
    private DynamicDropStats raid;

    public AllDynamicDropStats() {
    }

    public AllDynamicDropStats(DynamicDropStats mobs, DynamicDropStats raid) {
        this.mobs = mobs;
        this.raid = raid;
    }

    public DynamicDropStats getMobs() {
        return mobs;
    }

    public DynamicDropStats getRaid() {
        return raid;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AllDynamicDropStats.class.getSimpleName() + "[", "]")
                .add(Objects.toString(mobs))
                .add(Objects.toString(raid))
                .toString();
    }
    
}
