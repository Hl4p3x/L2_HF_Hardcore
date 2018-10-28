package com.l2jserver.gameserver.model.items.helper;

import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

import java.util.List;
import java.util.stream.Collectors;

public class L2WeaponHelper {

    public static List<L2Weapon> itemsAsWeapons(List<L2Item> items) {
        return items.stream().filter(item -> item instanceof L2Weapon).map(item -> (L2Weapon) item).collect(Collectors.toList());
    }

    public static List<L2Weapon> itemsInstancesAsWeapons(List<L2ItemInstance> items) {
        return items.stream().filter(item -> item.getItem() instanceof L2Weapon).map(item -> (L2Weapon) item.getItem()).collect(Collectors.toList());
    }

}
