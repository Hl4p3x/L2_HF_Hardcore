package com.l2jserver.gameserver.model.actor;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.holders.RangeChanceHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.util.Rnd;

import java.util.Comparator;
import java.util.List;

public class RandomEnchantmentHelper {

    public static void applyRandomDropEnchant(L2ItemInstance item) {
        if (Config.ALT_DROP_ENCHANTED && (item.isWeapon() || item.isArmor())) {
            double random = Rnd.getDouble(100);

            List<RangeChanceHolder> chances = Config.ALT_DROP_ENCHANTED_CHANCES;
            chances.sort(Comparator.comparingDouble(RangeChanceHolder::getChance));

            for (RangeChanceHolder chance : chances) {
                if (chance.getChance() >= random) {
                    item.setEnchantLevel(Rnd.get(chance.getMin(), chance.getMax()));
                    break;
                }
            }
        }
    }

}
