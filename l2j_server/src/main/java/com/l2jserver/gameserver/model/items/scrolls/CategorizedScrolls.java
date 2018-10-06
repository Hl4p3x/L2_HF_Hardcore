package com.l2jserver.gameserver.model.items.scrolls;

import java.util.List;

public class CategorizedScrolls {

    private List<Scroll> weaponScrolls;
    private List<Scroll> armorScrolls;
    private List<MiscScroll> miscScrolls;

    public CategorizedScrolls() {
    }

    public CategorizedScrolls(List<Scroll> weaponScrolls, List<Scroll> armorScrolls, List<MiscScroll> miscScrolls) {
        this.weaponScrolls = weaponScrolls;
        this.armorScrolls = armorScrolls;
        this.miscScrolls = miscScrolls;
    }

    public List<Scroll> getWeaponScrolls() {
        return weaponScrolls;
    }

    public List<Scroll> getArmorScrolls() {
        return armorScrolls;
    }

    public List<MiscScroll> getMiscScrolls() {
        return miscScrolls;
    }

    @Override
    public String toString() {
        return String.format("Categorized Scrolls (%s, %s, %s)", weaponScrolls, armorScrolls, miscScrolls);
    }

}
