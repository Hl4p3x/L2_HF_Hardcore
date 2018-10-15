package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.datatables.categorized.ScrollDropDataTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.ScrollGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.MiscScrollStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollDropStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.scrolls.CategorizedScrolls;
import com.l2jserver.gameserver.model.items.scrolls.MiscScroll;
import com.l2jserver.gameserver.model.items.scrolls.Scroll;
import com.l2jserver.util.Rnd;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ScrollsDropCalculator {

    public List<ItemHolder> calculate(L2Character victim, DynamicDropData dynamicDropData) {
        Optional<ScrollGrade> gradeOptional = ScrollGradeRange.byLevel(victim.getLevel());
        if (!gradeOptional.isPresent()) {
            return Lists.newArrayList();
        }

        CategorizedScrolls categorizedScrolls = ScrollDropDataTable.getInstance().getCategorizedScrolls();

        ScrollDropStats weaponScrollDropStats = dynamicDropData.getScrolls().getWeapon().get(gradeOptional.get());
        ScrollDropStats armorScrollDropStats = dynamicDropData.getScrolls().getArmor().get(gradeOptional.get());

        List<ItemHolder> drop = new ArrayList<>();

        calculateScrollDrop(
                categorizedScrolls::findNormalWeaponScroll,
                gradeOptional.get(),
                weaponScrollDropStats.getNormal()
        ).ifPresent(drop::add);

        calculateScrollDrop(
                categorizedScrolls::findBlessedWeaponScroll,
                gradeOptional.get(),
                weaponScrollDropStats.getBlessed()
        ).ifPresent(drop::add);

        calculateScrollDrop(
                categorizedScrolls::findNormalArmorScroll,
                gradeOptional.get(),
                armorScrollDropStats.getNormal()
        ).ifPresent(drop::add);

        calculateScrollDrop(
                categorizedScrolls::findBlessedArmorScroll,
                gradeOptional.get(),
                armorScrollDropStats.getBlessed()
        ).ifPresent(drop::add);

        drop.addAll(calculateMiscScrolls(categorizedScrolls, dynamicDropData.getScrolls().getMisc()));
        return drop;
    }

    private Optional<ItemHolder> calculateScrollDrop(Function<ScrollGrade, Optional<Scroll>> findScroll, ScrollGrade scrollGrade, DropStats dropStats) {
        return findScroll.apply(scrollGrade).flatMap(scroll -> calculateSingleScrollDrop(scroll, dropStats));
    }

    private Optional<ItemHolder> calculateSingleScrollDrop(Scroll scroll, DropStats dropStats) {
        if (Rnd.rollAgainst(dropStats.getChance())) {
            return Optional.of(new ItemHolder(scroll.getId(), dropStats.getCount().randomWithin()));
        } else {
            return Optional.empty();
        }
    }

    private List<ItemHolder> calculateMiscScrolls(CategorizedScrolls categorizedScrolls, MiscScrollStats miscScrollStats) {
        List<ItemHolder> drop = new ArrayList<>();
        Optional<ItemHolder> normalScroll = calculateSingleMiscScroll(categorizedScrolls.getNormalMiscScrolls(), miscScrollStats.getNormal());
        normalScroll.ifPresent(drop::add);

        Optional<ItemHolder> blessedScroll = calculateSingleMiscScroll(categorizedScrolls.getBlessedMiscScrolls(), miscScrollStats.getBlessed());
        blessedScroll.ifPresent(drop::add);
        return drop;
    }

    private Optional<ItemHolder> calculateSingleMiscScroll(List<MiscScroll> scrolls, DropStats DropStats) {
        Optional<MiscScroll> randomScrollOption = Rnd.getOneRandom(scrolls);
        if (!randomScrollOption.isPresent()) {
            return Optional.empty();
        }

        if (Rnd.rollAgainst(DropStats.getChance())) {
            return Optional.of(new ItemHolder(randomScrollOption.get().getId(), DropStats.getCount().randomWithin()));
        } else {
            return Optional.empty();
        }
    }

}
