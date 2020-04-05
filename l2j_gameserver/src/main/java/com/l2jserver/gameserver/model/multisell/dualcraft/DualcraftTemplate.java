package com.l2jserver.gameserver.model.multisell.dualcraft;

import java.util.Objects;
import java.util.StringJoiner;

public class DualcraftTemplate {

    private int leftWeaponId;
    private int rightWeaponId;
    private int dualWeaponId;

    public DualcraftTemplate(int leftWeaponId, int rightWeaponId, int dualWeaponId) {
        this.leftWeaponId = leftWeaponId;
        this.rightWeaponId = rightWeaponId;
        this.dualWeaponId = dualWeaponId;
    }

    public int getLeftWeaponId() {
        return leftWeaponId;
    }

    public int getRightWeaponId() {
        return rightWeaponId;
    }

    public int getDualWeaponId() {
        return dualWeaponId;
    }

    public boolean isSameWeaponDual() {
        return leftWeaponId == rightWeaponId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DualcraftTemplate.class.getSimpleName() + "[", "]")
                .add(Objects.toString(leftWeaponId))
                .add(Objects.toString(rightWeaponId))
                .add(Objects.toString(dualWeaponId))
                .toString();
    }

}
