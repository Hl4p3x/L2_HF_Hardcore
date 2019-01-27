package com.l2jserver.gameserver.model.actor.templates.drop.stats.basic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropCategory;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class NpcDropData {

    @JsonProperty("npc_id")
    private int npcId;
    @JsonProperty("drop_categories")
    private List<DynamicDropCategory> categories;

    public NpcDropData() {
    }

    public NpcDropData(int npcId, List<DynamicDropCategory> categories) {
        this.npcId = npcId;
        this.categories = categories;
    }

    public int getNpcId() {
        return npcId;
    }

    public List<DynamicDropCategory> getCategories() {
        return categories;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NpcDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(npcId))
                .add(Objects.toString(categories))
                .toString();
    }

}
