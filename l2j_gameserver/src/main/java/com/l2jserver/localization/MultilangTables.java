package com.l2jserver.localization;

import com.l2jserver.common.Loadable;
import com.l2jserver.common.config.CommonConfig;
import com.l2jserver.common.localization.Language;
import com.l2jserver.common.util.StringUtil;
import com.l2jserver.common.util.YamlMapper;
import com.l2jserver.common.util.filter.ExtFilter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultilangTables implements Loadable {

    private static final Logger LOG = LoggerFactory.getLogger(MultilangTables.class);

    private static final String FILE_EXTENSION = ".yml";

    private String stringsPath = CommonConfig.DATAPACK_ROOT + "/data/lang/strings";

    private Map<Language, StringsTable> stringTables = new HashMap<>();

    public void load() {
        stringTables = new HashMap<>();

        File stringsDir = new File(stringsPath);
        if (!stringsDir.exists() || !stringsDir.isDirectory()) {
            throw new IllegalStateException("Path for multilang strings '" + stringsPath + "' does not exist or is not a directory");
        }

        File[] files = stringsDir.listFiles(new ExtFilter(FILE_EXTENSION));
        if (files == null) {
            throw new IllegalStateException("Could not list directory '" + stringsPath + "'");
        }

        for (File file : files) {
            if (file.getName().replace(FILE_EXTENSION, StringUtil.EMPTY).length() != 2) {
                LOG.warn("File '" + file.getAbsolutePath() + "' has illegal name, should be a two character language code in '" + FILE_EXTENSION + "' format");
                continue;
            }

            String fileLangCode = file.getName().substring(0, 2);
            Language language = Language.of(fileLangCode);
            Map<String, String> localizationValues = YamlMapper.readAsStringMap(file);
            StringsTable oldValue = stringTables.put(language, new StringsTable(localizationValues));
            if (oldValue != null) {
                LOG.warn("Language {} was overridden from file {}", language, file.getAbsolutePath());
            }
        }

        LOG.info("Loaded {} languages", stringTables.keySet().size());
    }

    public static MultilangTables getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public StringsTable get(Language language) {
        StringsTable stringsTable = stringTables.get(language);
        if (stringsTable == null) {
            StringsTable defaultTable = stringTables.get(Language.defaultLanguage());
            if (defaultTable != null) {
                return defaultTable;
            } else {
                throw new IllegalArgumentException("Language " + language + " is not supported and default language not available");
            }
        } else {
            return stringsTable;
        }
    }

    private static class SingletonHolder {
        private static final MultilangTables INSTANCE = new MultilangTables();
    }

}
