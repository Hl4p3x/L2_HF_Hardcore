package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import java.util.Objects;
import java.util.StringJoiner;

public class NewAllDynamicDropData {

    private NewDynamicDropData mobs;
    private NewDynamicDropData raid;

    public NewAllDynamicDropData() {
    }

    public NewAllDynamicDropData(NewDynamicDropData mobs, NewDynamicDropData raid) {
        this.mobs = mobs;
        this.raid = raid;
    }

    public NewDynamicDropData getMobs() {
        return mobs;
    }

    public NewDynamicDropData getRaid() {
        return raid;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NewAllDynamicDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(mobs))
                .add(Objects.toString(raid))
                .toString();
    }

}
