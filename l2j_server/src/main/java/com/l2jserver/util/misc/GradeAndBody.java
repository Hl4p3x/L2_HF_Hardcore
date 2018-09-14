package com.l2jserver.util.misc;

import com.l2jserver.gameserver.model.items.graded.Grade;

import java.util.Objects;

public class GradeAndBody {

    private Grade grade;
    private int bodySlot;

    public GradeAndBody(Grade grade, int bodySlot) {
        this.grade = grade;
        this.bodySlot = bodySlot;
    }

    public int getBodySlot() {
        return bodySlot;
    }

    public Grade getGrade() {
        return grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeAndBody that = (GradeAndBody) o;
        return bodySlot == that.bodySlot &&
                grade == that.grade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bodySlot, grade);
    }

    @Override
    public String toString() {
        return String.format("GradeAndBody[%s, %s]", grade, bodySlot);
    }
}
