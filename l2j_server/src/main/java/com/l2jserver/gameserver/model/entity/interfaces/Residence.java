package com.l2jserver.gameserver.model.entity.interfaces;

import com.l2jserver.gameserver.model.interfaces.INamable;

public interface Residence extends OwnerByClan, INamable {

    int getResidenceId();

    long getLastOwnershipChangeTime();

}
