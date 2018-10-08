package com.l2jserver.gameserver.model.items.graded;

import com.fasterxml.jackson.annotation.JsonValue;

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

    public static GradeInfo unset() {
        return new GradeInfo(Grade.UNSET, GradeCategory.UNSET);
    }

    public static GradeInfo fromString(String text) {
        String[] split = text.split("-");
        if (split.length == 2) {
            return new GradeInfo(Grade.fromString(split[0]), GradeCategory.fromString(split[1]));
        } else if (split.length == 1) {
            return new GradeInfo(Grade.fromString(split[0]), GradeCategory.ALL);
        } else {
            return unset();
        }
    }

    public void setCategory(GradeCategory category) {
        this.category = category;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
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

    @JsonValue
    public String asString() {
        if (category == GradeCategory.ALL || category == GradeCategory.UNSET) {
            return grade.name().toLowerCase();
        } else {
            return grade.name().toLowerCase() + '-' + category.name().toLowerCase();
        }
    }

}
