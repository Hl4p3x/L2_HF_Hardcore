package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropCategory;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.util.Rnd;

import java.util.ArrayList;
import java.util.List;

public class DynamicDropHelper {

    public static List<ItemHolder> calculateCategoryDrop(DynamicDropCategory dynamicDropCategory) {
        DropStats dropStats = dynamicDropCategory.getStats();
        List<Integer> randomIds = Rnd.getFewRandom(new ArrayList<>(dynamicDropCategory.getIds()), dropStats.getStacks().randomWithin());

        List<ItemHolder> drop = new ArrayList<>();
        for (Integer id : randomIds) {
            if (Rnd.rollAgainst(dropStats.getChance())) {
                drop.add(new ItemHolder(id, dropStats.getCount().randomWithin()));
            }
        }

        return drop;
    }

}
