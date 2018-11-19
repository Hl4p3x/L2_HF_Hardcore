package com.l2jserver.gameserver.model.multisell.dualcraft;

import com.l2jserver.gameserver.model.items.interfaces.HasEnchantLevel;

import java.util.Objects;
import java.util.StringJoiner;

public class DualcraftWeaponObject implements HasEnchantLevel {

    private int leftWeaponObjectId;
    private int rightWeaponObjectId;
    private int dualTemplateId;
    private int dualEnchantLevel;

    public DualcraftWeaponObject(int leftWeaponObjectId, int rightWeaponObjectId, int dualTemplateId, int dualEnchantLevel) {
        this.leftWeaponObjectId = leftWeaponObjectId;
        this.rightWeaponObjectId = rightWeaponObjectId;
        this.dualTemplateId = dualTemplateId;
        this.dualEnchantLevel = dualEnchantLevel;
    }

    public int getLeftWeaponObjectId() {
        return leftWeaponObjectId;
    }

    public int getRightWeaponObjectId() {
        return rightWeaponObjectId;
    }

    @Override
    public int getEnchantLevel() {
        return dualEnchantLevel;
    }

    public int getDualTemplateId() {
        return dualTemplateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DualcraftWeaponObject that = (DualcraftWeaponObject) o;
        return leftWeaponObjectId == that.leftWeaponObjectId &&
                rightWeaponObjectId == that.rightWeaponObjectId &&
                dualEnchantLevel == that.dualEnchantLevel &&
                dualTemplateId == that.dualTemplateId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftWeaponObjectId, rightWeaponObjectId, dualEnchantLevel, dualTemplateId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DualcraftWeaponObject.class.getSimpleName() + "[", "]")
                .add(Objects.toString(leftWeaponObjectId))
                .add(Objects.toString(rightWeaponObjectId))
                .add(Objects.toString(dualTemplateId))
                .add(Objects.toString(dualEnchantLevel))
                .toString();
    }

}
