package com.l2jserver.gameserver.network.clientpackets.helper;

import com.l2jserver.gameserver.model.base.AcquireSkillType;

public class SkillLearnData {

    private int id;
    private int level;
    private AcquireSkillType skillType;
    private int subType;

    public SkillLearnData(int id, int level, AcquireSkillType skillType) {
        this.id = id;
        this.level = level;
        this.skillType = skillType;
    }

    public SkillLearnData(int id, int level, AcquireSkillType skillType, int subType) {
        this.id = id;
        this.level = level;
        this.skillType = skillType;
        this.subType = subType;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public AcquireSkillType getSkillType() {
        return skillType;
    }

    public int getSubType() {
        return subType;
    }

    @Override
    public String toString() {
        return "SkillLearnData{" +
            "id=" + id +
            ", level=" + level +
            ", skillType=" + skillType +
            ", subType=" + subType +
            '}';
    }

}
