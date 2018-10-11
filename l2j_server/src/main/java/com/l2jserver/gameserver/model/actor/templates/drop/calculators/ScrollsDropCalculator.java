package com.l2jserver.gameserver.model.actor.templates.drop.calculators;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.datatables.categorized.ScrollDropDataTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.templates.drop.ScrollGradeRange;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.MiscScrollStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.scrolls.CategorizedScrolls;
import com.l2jserver.gameserver.model.items.scrolls.MiscScroll;
import com.l2jserver.gameserver.model.items.scrolls.Scroll;
import com.l2jserver.util.Rnd;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScrollsDropCalculator {

    public List<ItemHolder> calculate(L2Character victim, DynamicDropData dynamicDropData) {
        Optional<ScrollGrade> grade = ScrollGradeRange.byLevel(victim.getLevel());
        if (!grade.isPresent()) {
            return Lists.newArrayList();
        }

        List<ItemHolder> drop = new ArrayList<>();

        CategorizedScrolls categorizedScrolls = ScrollDropDataTable.getInstance().getCategorizedScrolls();

        drop.addAll(calculateScrollDropData(
                categorizedScrolls.getNormalWeaponScrolls(), categorizedScrolls.getBlessedWeaponScrolls(),
                dynamicDropData.getScrolls().getWeapon().get(grade.get()))
        );
        drop.addAll(calculateScrollDropData(
                categorizedScrolls.getNormalArmorScrolls(), categorizedScrolls.getBlessedArmorScrolls(),
                dynamicDropData.getScrolls().getArmor().get(grade.get()))
        );
        drop.addAll(calculateMiscScrolls(categorizedScrolls, dynamicDropData.getScrolls().getMisc()));

        return drop;
    }

    private List<ItemHolder> calculateScrollDropData(List<Scroll> normalScrolls, List<Scroll> blessedScrolls, ScrollDropData scrollDropData) {
        List<ItemHolder> drop = new ArrayList<>();
        calculateSingleScroll(normalScrolls, scrollDropData.getNormal()).ifPresent(drop::add);
        calculateSingleScroll(blessedScrolls, scrollDropData.getBlessed()).ifPresent(drop::add);
        return drop;
    }

    private List<ItemHolder> calculateMiscScrolls(CategorizedScrolls categorizedScrolls, MiscScrollStats miscScrollStats) {
        List<ItemHolder> drop = new ArrayList<>();
        Optional<ItemHolder> normalScroll = calculateSingleMiscScroll(categorizedScrolls.getNormalMiscScrolls(), miscScrollStats.getNormal());
        normalScroll.ifPresent(drop::add);

        Optional<ItemHolder> blessedScroll = calculateSingleMiscScroll(categorizedScrolls.getBlessedMiscScrolls(), miscScrollStats.getBlessed());
        blessedScroll.ifPresent(drop::add);
        return drop;
    }

    private Optional<ItemHolder> calculateSingleMiscScroll(List<MiscScroll> scrolls, ChanceCountPair chanceCountPair) {
        Optional<MiscScroll> randomScrollOption = Rnd.getOneRandom(scrolls);
        if (!randomScrollOption.isPresent()) {
            return Optional.empty();
        }

        if (Rnd.rollAgainst(chanceCountPair.getChance())) {
            return Optional.of(new ItemHolder(randomScrollOption.get().getId(), chanceCountPair.getCount().randomWithin()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<ItemHolder> calculateSingleScroll(List<Scroll> scrolls, ChanceCountPair chanceCountPair) {
        Optional<Scroll> randomScrollOption = Rnd.getOneRandom(scrolls);
        if (!randomScrollOption.isPresent()) {
            return Optional.empty();
        }

        if (Rnd.rollAgainst(chanceCountPair.getChance())) {
            return Optional.of(new ItemHolder(randomScrollOption.get().getId(), chanceCountPair.getCount().randomWithin()));
        } else {
            return Optional.empty();
        }
    }


}
