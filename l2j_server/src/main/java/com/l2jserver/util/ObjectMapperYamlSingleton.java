package com.l2jserver.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ObjectMapperYamlSingleton {

    public static Map<String, String> readAsStringMap(File file) {
        try {
            return getInstance().readValue(file, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + file, e);
        }
    }

    public static ObjectMapper getInstance() {
        return ObjectMapperYamlSingleton.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final ObjectMapper _instance = new ObjectMapper(new YAMLFactory());
    }

}
