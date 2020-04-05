package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.NpcDropData;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class AllDynamicDropData {

    private DynamicDropData mobs;
    private DynamicDropData raid;
    private List<NpcDropData> npcs;

    public AllDynamicDropData() {
    }

    public AllDynamicDropData(DynamicDropData mobs, DynamicDropData raid, List<NpcDropData> npcs) {
        this.mobs = mobs;
        this.raid = raid;
        this.npcs = npcs;
    }

    public DynamicDropData getMobs() {
        return mobs;
    }

    public DynamicDropData getRaid() {
        return raid;
    }

    public List<NpcDropData> getNpcs() {
        return npcs;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AllDynamicDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(mobs))
                .add(Objects.toString(raid))
                .add(Objects.toString(npcs))
                .toString();
    }

}
