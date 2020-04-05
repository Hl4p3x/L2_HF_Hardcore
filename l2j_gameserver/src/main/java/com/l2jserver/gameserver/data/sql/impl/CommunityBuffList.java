package com.l2jserver.gameserver.data.sql.impl;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import java.util.Collections;
import java.util.List;

public class CommunityBuffList {

    private Integer id;
    private Integer ownerId;
    private String name;
    private List<SkillHolder> skills = Collections.emptyList();

    public CommunityBuffList(Integer ownerId, String name) {
        this.ownerId = ownerId;
        this.name = name;
    }

    public CommunityBuffList(Integer id, Integer ownerId, String name) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
    }

    public CommunityBuffList(Integer id, Integer ownerId, String name, List<SkillHolder> skills) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.skills = skills;
    }

    public Integer getId() {
        return id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public List<SkillHolder> getSkills() {
        return skills;
    }

    @Override
    public String toString() {
        return "CommunityBuffList " + name + "[" + ownerId + "]";
    }

}
