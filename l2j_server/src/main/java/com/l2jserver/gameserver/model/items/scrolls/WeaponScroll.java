package com.l2jserver.gameserver.model.items.scrolls;

import com.l2jserver.gameserver.model.interfaces.IIdentifiable;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.graded.Grade;
import com.l2jserver.gameserver.model.items.type.EtcItemType;

public class WeaponScroll implements IIdentifiable {

    private int id;
    private String name;
    private Grade grade;
    private boolean blessed;

    public WeaponScroll() {
    }

    public WeaponScroll(int id, String name, Grade grade, boolean blessed) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.blessed = blessed;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Grade getGrade() {
        return grade;
    }

    public boolean isBlessed() {
        return blessed;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s, %s)", name, id, grade, blessed);
    }

    public static WeaponScroll fromEtc(L2EtcItem etcItem) {
        return new WeaponScroll(
                etcItem.getId(),
                etcItem.getName(),
                Scroll.gradeFromScrollName(etcItem.getName()),
                etcItem.getItemType().equals(EtcItemType.BLESS_SCRL_ENCHANT_WP)
        );
    }

}
