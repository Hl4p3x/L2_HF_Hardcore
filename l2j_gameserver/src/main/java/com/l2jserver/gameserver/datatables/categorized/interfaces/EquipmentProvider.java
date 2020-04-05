package com.l2jserver.gameserver.datatables.categorized.interfaces;

import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import java.util.List;

public interface EquipmentProvider<T> {

    List<T> getWeaponsByGrade(GradeInfo gradeInfo);

    List<T> getArmorByGrade(GradeInfo gradeInfo);

    List<T> getJewelsByGrade(GradeInfo gradeInfo);

}
