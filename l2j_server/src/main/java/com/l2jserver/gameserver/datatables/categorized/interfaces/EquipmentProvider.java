package com.l2jserver.gameserver.datatables.categorized.interfaces;

import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.interfaces.HasItemId;

import java.util.List;

public interface EquipmentProvider<T extends HasItemId> {

    List<T> getWeaponsByGrade(GradeInfo gradeInfo);

    List<T> getArmorByGrade(GradeInfo gradeInfo);

    List<T> getJewelsByGrade(GradeInfo gradeInfo);

}
