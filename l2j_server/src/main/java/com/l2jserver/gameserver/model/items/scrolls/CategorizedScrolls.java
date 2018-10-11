package com.l2jserver.gameserver.model.items.scrolls;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class CategorizedScrolls {

    private List<Scroll> normalWeaponScrolls;
    private List<Scroll> blessedWeaponScrolls;
    private List<Scroll> normalArmorScrolls;
    private List<Scroll> blessedArmorScrolls;
    private List<MiscScroll> normalMiscScrolls;
    private List<MiscScroll> blessedMiscScrolls;

    public CategorizedScrolls() {
    }

    public CategorizedScrolls(List<Scroll> normalWeaponScrolls, List<Scroll> blessedWeaponScrolls,
                              List<Scroll> normalArmorScrolls, List<Scroll> blessedArmorScrolls,
                              List<MiscScroll> normalMiscScrolls, List<MiscScroll> blessedMiscScrolls) {
        this.normalWeaponScrolls = normalWeaponScrolls;
        this.blessedWeaponScrolls = blessedWeaponScrolls;
        this.normalArmorScrolls = normalArmorScrolls;
        this.blessedArmorScrolls = blessedArmorScrolls;
        this.normalMiscScrolls = normalMiscScrolls;
        this.blessedMiscScrolls = blessedMiscScrolls;
    }

    public List<Scroll> getNormalWeaponScrolls() {
        return normalWeaponScrolls;
    }

    public List<Scroll> getBlessedWeaponScrolls() {
        return blessedWeaponScrolls;
    }

    public List<Scroll> getNormalArmorScrolls() {
        return normalArmorScrolls;
    }

    public List<Scroll> getBlessedArmorScrolls() {
        return blessedArmorScrolls;
    }

    public List<MiscScroll> getNormalMiscScrolls() {
        return normalMiscScrolls;
    }

    public List<MiscScroll> getBlessedMiscScrolls() {
        return blessedMiscScrolls;
    }

    private Optional<Scroll> findScrollByGrade(List<Scroll> scrolls, ScrollGrade scrollGrade) {
        return scrolls.stream().filter(scroll -> scroll.getGrade().equals(scrollGrade)).findFirst();
    }

    public Optional<Scroll> findNormalWeaponScroll(ScrollGrade scrollGrade) {
        return findScrollByGrade(normalWeaponScrolls, scrollGrade);
    }

    public Optional<Scroll> findBlessedWeaponScroll(ScrollGrade scrollGrade) {
        return findScrollByGrade(blessedWeaponScrolls, scrollGrade);
    }

    public Optional<Scroll> findNormalArmorScroll(ScrollGrade scrollGrade) {
        return findScrollByGrade(normalArmorScrolls, scrollGrade);
    }

    public Optional<Scroll> findBlessedArmorScroll(ScrollGrade scrollGrade) {
        return findScrollByGrade(blessedArmorScrolls, scrollGrade);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CategorizedScrolls.class.getSimpleName() + "[", "]")
                .add(Objects.toString(normalWeaponScrolls))
                .add(Objects.toString(blessedWeaponScrolls))
                .add(Objects.toString(normalArmorScrolls))
                .add(Objects.toString(blessedArmorScrolls))
                .add(Objects.toString(normalMiscScrolls))
                .add(Objects.toString(blessedMiscScrolls))
                .toString();
    }

}
