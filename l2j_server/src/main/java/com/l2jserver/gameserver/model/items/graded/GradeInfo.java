package com.l2jserver.gameserver.model.items.graded;

import java.util.Objects;

public class GradeInfo {

    private Grade grade;
    private GradeCategory category;

    public GradeInfo() {
    }

    public GradeInfo(Grade grade, GradeCategory category) {
        this.grade = grade;
        this.category = category;
    }

    public void setCategory(GradeCategory category) {
        this.category = category;
    }

    public Grade getGrade() {
        return grade;
    }

    public GradeCategory getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeInfo gradeInfo = (GradeInfo) o;
        return grade == gradeInfo.grade &&
                category == gradeInfo.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(grade, category);
    }

    @Override
    public String toString() {
        return String.format("GradeInfo[%s, %s]", grade, category);
    }
}
