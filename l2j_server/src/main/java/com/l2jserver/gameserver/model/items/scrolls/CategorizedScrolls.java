package com.l2jserver.gameserver.model.items.scrolls;

import java.util.List;

public class CategorizedScrolls {

    private List<WeaponScroll> weaponScrolls;
    private List<ArmorScroll> armorScrolls;
    private List<MiscScroll> miscScrolls;

    public CategorizedScrolls() {
    }

    public CategorizedScrolls(List<WeaponScroll> weaponScrolls, List<ArmorScroll> armorScrolls, List<MiscScroll> miscScrolls) {
        this.weaponScrolls = weaponScrolls;
        this.armorScrolls = armorScrolls;
        this.miscScrolls = miscScrolls;
    }

    public List<WeaponScroll> getWeaponScrolls() {
        return weaponScrolls;
    }

    public List<ArmorScroll> getArmorScrolls() {
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
