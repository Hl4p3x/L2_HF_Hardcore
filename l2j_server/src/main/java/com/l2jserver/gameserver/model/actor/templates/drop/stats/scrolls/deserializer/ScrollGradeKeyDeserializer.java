package com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;
import com.l2jserver.util.ObjectMapperYamlSingleton;

import java.io.IOException;

public class ScrollGradeKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return ObjectMapperYamlSingleton.getInstance().readValue(key, ScrollGrade.class);
    }

}
