package com.l2jserver.gameserver.model.actor;

import com.l2jserver.Config;
import com.l2jserver.common.util.Rnd;
import com.l2jserver.gameserver.model.holders.RangeChanceHolder;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import java.util.List;

public class RandomEnchantmentHelper {

    public static void applyRandomDropEnchant(L2ItemInstance item) {
        if (canApplyRandomDropEnchant(item.getItem())) {
            item.setEnchantLevel(rollRandomDropEnchant());
        }
    }

    public static boolean canApplyRandomDropEnchant(L2Item item) {
        return Config.ALT_DROP_ENCHANTED && (item.isWeapon() || item.isArmor());
    }

    public static int rollRandomDropEnchant() {
        double random = Rnd.randomHundredth();

        List<RangeChanceHolder> chances = Config.ALT_DROP_ENCHANTED_CHANCES;
        for (RangeChanceHolder chance : chances) {
            if (chance.getChance() >= random) {
                return Rnd.get(chance.getMin(), chance.getMax());
            }
        }
        return 0;
    }

}
