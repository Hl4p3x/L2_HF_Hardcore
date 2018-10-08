package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import java.util.Objects;
import java.util.StringJoiner;

public class AllDynamicDropData {

    private DynamicDropData mobs;
    private DynamicDropData raid;

    public AllDynamicDropData() {
    }

    public AllDynamicDropData(DynamicDropData mobs, DynamicDropData raid) {
        this.mobs = mobs;
        this.raid = raid;
    }

    public DynamicDropData getMobs() {
        return mobs;
    }

    public DynamicDropData getRaid() {
        return raid;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AllDynamicDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(mobs))
                .add(Objects.toString(raid))
                .toString();
    }

}
