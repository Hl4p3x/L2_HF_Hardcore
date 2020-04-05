package com.l2jserver.localization;

import com.l2jserver.common.localization.Language;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class Strings {

    public static StringsTable lang(Language language) {
        return MultilangTables.getInstance().get(language);
    }

    public static StringsTable of(L2PcInstance player) {
        return MultilangTables.getInstance().get(player.getLang());
    }

}
