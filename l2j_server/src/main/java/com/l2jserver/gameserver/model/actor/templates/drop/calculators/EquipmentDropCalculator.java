package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropEquipmentCategory;
import com.l2jserver.gameserver.model.actor.templates.drop.DynamicDropGradeData;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipmentDropCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentDropCalculator.class);

    public List<ItemHolder> calculate(DynamicDropGradeData dynamicDropGradeData) {
        List<ItemHolder> drop = new ArrayList<>();
        return drop;
    }

    private List<ItemHolder> calculateEquipmentCategoryDrop(DynamicDropEquipmentCategory dynamicDropEquipmentCategory) {
        Optional<L2RecipeList> part = Rnd.getOneRandom(itemsByGradeInfo);
        if (part.isPresent() && Rnd.rollAgainst(dropStats.getChance())) {
            return Optional.of(new ItemHolder(part.get().getRecipeId(), dropStats.getCount().randomWithin()));
        } else {
            return Optional.empty();
        }
    }

}
