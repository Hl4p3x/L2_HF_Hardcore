package com.l2jserver.gameserver.model.items.scrolls;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.ScrollGrade;
import com.l2jserver.gameserver.model.interfaces.IIdentifiable;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.type.EtcItemType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scroll implements IIdentifiable {

    private static final Pattern GRADE_REGEX = Pattern.compile("\\((.+?)-Grade\\)");

    private int id;
    private String name;
    private ScrollGrade grade;
    private boolean blessed;

    public Scroll() {
    }

    public Scroll(int id, String name, ScrollGrade grade, boolean blessed) {
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

    public ScrollGrade getGrade() {
        return grade;
    }

    public boolean isBlessed() {
        return blessed;
    }

    public boolean isNotBlessed() {
        return !isBlessed();
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s, %s)", name, id, grade, blessed);
    }

    public static Scroll weaponScrollFromEtc(L2EtcItem etcItem) {
        return new Scroll(
                etcItem.getId(),
                etcItem.getName(),
                Scroll.gradeFromScrollName(etcItem.getName()),
                etcItem.getItemType().equals(EtcItemType.BLESS_SCRL_ENCHANT_WP)
        );
    }

    public static Scroll armorScrollFromEtc(L2EtcItem etcItem) {
        return new Scroll(
                etcItem.getId(),
                etcItem.getName(),
                Scroll.gradeFromScrollName(etcItem.getName()),
                etcItem.getItemType().equals(EtcItemType.BLESS_SCRL_ENCHANT_AM)
        );
    }

    public static ScrollGrade gradeFromScrollName(String scrollName) {
        Matcher matcher = GRADE_REGEX.matcher(scrollName);
        if (matcher.find()) {
            return ScrollGrade.fromString(matcher.group(1));
        } else {
            return ScrollGrade.UNSET;
        }
    }

}
