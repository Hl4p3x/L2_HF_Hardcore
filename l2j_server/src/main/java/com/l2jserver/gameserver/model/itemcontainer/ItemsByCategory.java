package com.l2jserver.gameserver.model.itemcontainer;

import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

import java.util.Collections;
import java.util.List;

public class ItemsByCategory {

    private List<L2ItemInstance> weapons;
    private List<L2ItemInstance> armor;
    private List<L2ItemInstance> bag;

    public ItemsByCategory(List<L2ItemInstance> weapons, List<L2ItemInstance> armor, List<L2ItemInstance> bag) {
        this.weapons = weapons;
        this.armor = armor;
        this.bag = bag;
    }

    public List<L2ItemInstance> getWeapons() {
        return weapons;
    }

    public List<L2ItemInstance> getArmor() {
        return armor;
    }

    public List<L2ItemInstance> getBag() {
        return bag;
    }

    public void shuffleAll() {
        Collections.shuffle(weapons);
        Collections.shuffle(armor);
        Collections.shuffle(bag);
    }

    @Override
    public String toString() {
        return String.format("ItemsByCategory[%s weapons, %s armor, %s bag]", weapons.size(), armor.size(), bag.size());
    }
}
