package com.l2jserver.gameserver.model.items.graded.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.l2jserver.gameserver.model.items.graded.Grade;
import com.l2jserver.util.ObjectMapperYamlSingleton;

import java.io.IOException;

public class GradeInfoKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return ObjectMapperYamlSingleton.getInstance().readValue(key, Grade.class);
    }

}
