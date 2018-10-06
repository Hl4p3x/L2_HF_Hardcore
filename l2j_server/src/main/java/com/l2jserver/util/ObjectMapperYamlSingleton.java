package com.l2jserver.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ObjectMapperYamlSingleton {

    public static ObjectMapper getInstance() {
        return ObjectMapperYamlSingleton.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final ObjectMapper _instance = new ObjectMapper(new YAMLFactory());
    }

}
