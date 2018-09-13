package com.l2jserver.gameserver.model.items.graded;

public class GradeInfo {

    private Grade grade;
    private GradeCategory category;

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
    public String toString() {
        return String.format("GradeInfo[%s, %s]", grade, category);
    }
}
