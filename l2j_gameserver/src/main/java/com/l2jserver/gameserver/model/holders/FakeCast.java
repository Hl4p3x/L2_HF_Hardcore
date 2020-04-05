package com.l2jserver.gameserver.model.holders;

import java.util.Objects;
import java.util.StringJoiner;

public class FakeCast {

    private SkillHolder skillHolder;
    private int castTime;
    private Runnable onCastEnd;

    public FakeCast(SkillHolder skillHolder, int castTime, Runnable onCastEnd) {
        this.skillHolder = skillHolder;
        this.castTime = castTime;
        this.onCastEnd = onCastEnd;
    }

    public SkillHolder getSkillHolder() {
        return skillHolder;
    }

    public int getCastTime() {
        return castTime;
    }

    public Runnable getOnCastEnd() {
        return onCastEnd;
    }

    public void setOnCastEnd(Runnable onCastEnd) {
        this.onCastEnd = onCastEnd;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FakeCast.class.getSimpleName() + "[", "]")
                .add(Objects.toString(skillHolder))
                .toString();
    }

}
