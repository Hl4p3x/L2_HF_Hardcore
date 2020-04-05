package com.l2jserver.gameserver.model.actor.templates.drop.stats.basic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropCategory;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class NpcDropData {

    @JsonProperty("npc_ids")
    private List<Integer> npcIds;
    @JsonProperty("drop_categories")
    private List<DynamicDropCategory> categories;

    public NpcDropData() {
    }

    public NpcDropData(List<Integer> npcIds, List<DynamicDropCategory> categories) {
        this.npcIds = npcIds;
        this.categories = categories;
    }

    public List<Integer> getNpcIds() {
        return npcIds;
    }

    public List<DynamicDropCategory> getCategories() {
        return categories;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NpcDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(npcIds))
                .add(Objects.toString(categories))
                .toString();
    }

}
