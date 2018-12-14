package com.l2jserver.localization;

public class Strings {

    public static StringsTable lang(Language language) {
        return MultilangTables.getInstance().get(language);
    }

}
