package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.l2jserver.util.ObjectMapperYamlSingleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DynamicDropTable {

    private AllDynamicDropData allDynamicDropData;

    public DynamicDropTable() {
        load();
    }

    private void load() {
        final String path = "./config/DynamicDropRates.yml";
        try {
            InputStream configStream = new FileInputStream(new File(path));

            allDynamicDropData = ObjectMapperYamlSingleton.getInstance().readValue(
                    configStream,
                    AllDynamicDropData.class
            );
        } catch (IOException e) {
            throw new IllegalStateException("Could not load Dynamic Drop Rates configuration from " + path);
        }
    }

    public AllDynamicDropData getAllDynamicDropData() {
        return allDynamicDropData;
    }

    public static DynamicDropTable getInstance() {
        return DynamicDropTable.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final DynamicDropTable _instance = new DynamicDropTable();
    }

}
