package com.l2jserver.common.config;

import com.l2jserver.common.Log;
import com.l2jserver.common.config.database.DatabaseProperties;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonConfig {

    public static final Log LOG = new Log("[Common Config]", CommonConfig.class);

    public static final String EOL = System.lineSeparator();

    public static boolean DEBUG;

    public static DatabaseProperties DATABASE_PROPERTIES;

    public static int MMO_SELECTOR_SLEEP_TIME;
    public static int MMO_MAX_SEND_PER_PASS;
    public static int MMO_MAX_READ_PER_PASS;
    public static int MMO_HELPER_BUFFER_COUNT;
    public static boolean MMO_TCP_NODELAY;

    public static boolean L2JMOD_MULTILANG_ENABLE;
    public static String L2JMOD_MULTILANG_DEFAULT;
    public static List<String> L2JMOD_MULTILANG_ALLOWED = new ArrayList<>();

    public static File DATAPACK_ROOT;

    public static void load() {
        // MMO
        String MMO_CONFIG_FILE = "./config/server/MMO.properties";

        final PropertiesParser mmoSettings = new PropertiesParser(MMO_CONFIG_FILE);

        MMO_SELECTOR_SLEEP_TIME = mmoSettings.getInt("SleepTime", 20);
        MMO_MAX_SEND_PER_PASS = mmoSettings.getInt("MaxSendPerPass", 12);
        MMO_MAX_READ_PER_PASS = mmoSettings.getInt("MaxReadPerPass", 12);
        MMO_HELPER_BUFFER_COUNT = mmoSettings.getInt("HelperBufferCount", 20);
        MMO_TCP_NODELAY = mmoSettings.getBoolean("TcpNoDelay", false);

        DATABASE_PROPERTIES = YamlParser.parseDatabaseProperties();

        final PropertiesParser languageSettings = new PropertiesParser("./config/multilang.properties");

        L2JMOD_MULTILANG_DEFAULT = languageSettings.getString("MultiLangDefault", "en");
        L2JMOD_MULTILANG_ENABLE = languageSettings.getBoolean("MultiLangEnable", false);

        L2JMOD_MULTILANG_ALLOWED = Arrays.asList(
                languageSettings.getString("MultiLangAllowed", L2JMOD_MULTILANG_DEFAULT).split(";")
        );

        if (!L2JMOD_MULTILANG_ALLOWED.contains(L2JMOD_MULTILANG_DEFAULT)) {
            LOG.warn("MultiLang[Config.load()]: default language: {} is not in allowed list!", L2JMOD_MULTILANG_DEFAULT);
        }


        final String CONFIGURATION_FILE = "./config/server/Server.properties";

        final PropertiesParser serverSettings = new PropertiesParser(CONFIGURATION_FILE);

        try {
            DATAPACK_ROOT = new File(serverSettings.getString("DatapackRoot", ".").replaceAll("\\\\", "/")).getCanonicalFile();
        } catch (IOException e) {
            LOG.warn("Error setting datapack root!", e);
            DATAPACK_ROOT = new File(".");
        }
    }

}
