package com.l2jserver.gameserver.model.actor;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.util.Rnd;

public class RandomEnchantmentHelper {

    public static void applyRandomDropEnchant(L2ItemInstance item) {
        if (Config.ALT_DROP_ENCHANTED && Config.ALT_DROP_ENCHANTED_CHANCE > Rnd.get(100)) {
            item.setEnchantLevel(Rnd.get(Config.ALT_DROP_ENCHANTED_MIN, Config.ALT_DROP_ENCHANTED_MAX));
        }
    }

}
