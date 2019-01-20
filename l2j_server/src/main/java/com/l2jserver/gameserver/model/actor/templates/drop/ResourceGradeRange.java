package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.model.items.craft.ResourceGrade;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ResourceGradeRange {

    LOW(1, 40, ResourceGrade.LOW),
    MID(41, 62, ResourceGrade.MID),
    HIGH(63, 77, ResourceGrade.HIGH),
    HIGHEST(78, 86, ResourceGrade.HIGHEST),
    BASE_MATERIALS(80, 86, ResourceGrade.BASE_CATEGORY_MATERIALS);


    private int lowLevel;
    private int highLevel;
    private ResourceGrade resourceGrade;

    ResourceGradeRange(int lowLevel, int highLevel, ResourceGrade resourceGrade) {
        this.lowLevel = lowLevel;
        this.highLevel = highLevel;
        this.resourceGrade = resourceGrade;
    }

    public int getLowLevel() {
        return lowLevel;
    }

    public int getHighLevel() {
        return highLevel;
    }

    public ResourceGrade getResourceGrade() {
        return resourceGrade;
    }

    public static Set<ResourceGrade> byLevel(int level) {
        return Stream.of(values()).filter(item -> item.getLowLevel() <= level).map(ResourceGradeRange::getResourceGrade).collect(Collectors.toSet());
    }

}
