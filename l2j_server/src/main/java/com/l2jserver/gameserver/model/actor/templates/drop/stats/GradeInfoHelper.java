package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.l2jserver.gameserver.model.items.graded.GradeCategory;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;

import java.util.Map;
import java.util.Optional;

public class GradeInfoHelper {

    public static <T> Optional<T> findByGradeInfo(GradeInfo gradeInfo, Map<GradeInfo, T> items) {
        Optional<T> optional = Optional.ofNullable(items.get(gradeInfo));
        if (optional.isPresent()) {
            return optional;
        }

        return Optional.ofNullable(items.get(new GradeInfo(gradeInfo.getGrade(), GradeCategory.ALL)));
    }

}
