package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.deserializer.ScrollGradeKeyDeserializer;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ScrollDropData {

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<ScrollGrade, ChanceCountPair> normal;

    @JsonDeserialize(keyUsing = ScrollGradeKeyDeserializer.class)
    private Map<ScrollGrade, ChanceCountPair> blessed;

    public ScrollDropData() {
    }

    public ScrollDropData(Map<ScrollGrade, ChanceCountPair> normal, Map<ScrollGrade, ChanceCountPair> blessed) {
        this.normal = normal;
        this.blessed = blessed;
    }

    public Map<ScrollGrade, ChanceCountPair> getNormal() {
        return normal;
    }

    public Map<ScrollGrade, ChanceCountPair> getBlessed() {
        return blessed;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScrollDropData.class.getSimpleName() + "[", "]")
                .add(Objects.toString(normal))
                .add(Objects.toString(blessed))
                .toString();
    }

}
