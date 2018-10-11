package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.datatables.categorized.CraftResourcesDropDataTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.ResourceGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropStats;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.craft.CraftResource;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;
import com.l2jserver.util.Rnd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResourcesDropCalculator {

    public List<ItemHolder> calculate(L2Character victim, DynamicDropData dynamicDropData) {
        Set<ResourceGrade> resourceGrades = ResourceGradeRange.byLevel(victim.getLevel());
        if (resourceGrades.isEmpty()) {
            return Lists.newArrayList();
        }

        List<ItemHolder> drop = new ArrayList<>();
        for (ResourceGrade resourceGrade : resourceGrades) {
            ResourceDropStats chanceCountPair = dynamicDropData.getResources().getDrop().get(resourceGrade);
            List<CraftResource> resources = CraftResourcesDropDataTable.getInstance().getResourcesByGrade(resourceGrade);
            List<CraftResource> randomResources = Rnd.getFewRandom(resources, chanceCountPair.getStacks().randomWithin());

            for (CraftResource craftResource : randomResources) {
                if (Rnd.rollAgainst(chanceCountPair.getChance())) {
                    drop.add(new ItemHolder(craftResource.getId(), chanceCountPair.getCount().randomWithin()));
                }
            }
        }

        return drop;
    }

}
