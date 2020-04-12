package com.l2jserver.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.l2jserver.common.config.database.DatabaseProperties;
import com.l2jserver.common.util.YamlMapper;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YamlParser {

    private static final Logger LOG = LoggerFactory.getLogger(YamlParser.class);

    public static DatabaseProperties parseDatabaseProperties() {
        String propertiesFile = "./config/database.yaml";
        try {
            boolean fileExists = new File(propertiesFile).exists();
            if (!fileExists) {
                LOG.warn("Configuration file " + propertiesFile + " does not exists, returning default values");
                return new DatabaseProperties();
            }

            return YamlMapper.getInstance().readValue(new File(propertiesFile), DatabaseProperties.class);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to parse 'database.yaml'");
            throw new IllegalStateException(e);
        } catch (IOException e) {
            LOG.error("Failed to read 'database.yaml'");
            throw new IllegalStateException(e);
        }
    }

}
