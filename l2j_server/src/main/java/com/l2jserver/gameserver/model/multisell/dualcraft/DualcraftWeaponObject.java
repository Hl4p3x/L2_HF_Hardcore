package com.l2jserver.gameserver.model.multisell.dualcraft;

import com.l2jserver.gameserver.model.items.interfaces.EnchantableItemObject;
import com.l2jserver.gameserver.model.items.interfaces.HasEnchantLevel;

import java.util.Objects;
import java.util.StringJoiner;

public class DualcraftWeaponObject implements HasEnchantLevel {

    private EnchantableItemObject leftWeaponObject;
    private EnchantableItemObject rightWeaponObject;
    private int dualTemplateId;
    private int dualEnchantLevel;

    public DualcraftWeaponObject(EnchantableItemObject leftWeaponObject, EnchantableItemObject rightWeaponObject, int dualTemplateId, int dualEnchantLevel) {
        this.leftWeaponObject = leftWeaponObject;
        this.rightWeaponObject = rightWeaponObject;
        this.dualTemplateId = dualTemplateId;
        this.dualEnchantLevel = dualEnchantLevel;
    }

    public EnchantableItemObject getLeftWeaponObject() {
        return leftWeaponObject;
    }

    public EnchantableItemObject getRightWeaponObject() {
        return rightWeaponObject;
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
        return leftWeaponObject.equals(that.leftWeaponObject) &&
                rightWeaponObject.equals(that.rightWeaponObject) &&
                dualEnchantLevel == that.dualEnchantLevel &&
                dualTemplateId == that.dualTemplateId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftWeaponObject, rightWeaponObject, dualEnchantLevel, dualTemplateId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DualcraftWeaponObject.class.getSimpleName() + "[", "]")
                .add(Objects.toString(leftWeaponObject))
                .add(Objects.toString(rightWeaponObject))
                .add(Objects.toString(dualTemplateId))
                .add(Objects.toString(dualEnchantLevel))
                .toString();
    }

}
