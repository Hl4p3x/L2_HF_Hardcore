package com.l2jserver.gameserver.model.items.graded.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import java.io.IOException;

public class GradeInfoKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return GradeInfo.fromString(key);
    }

}
