package com.l2jserver.gameserver.model.items.scrolls;

import com.l2jserver.gameserver.model.interfaces.IIdentifiable;
import com.l2jserver.gameserver.model.items.L2Item;

public class MiscScroll implements IIdentifiable {

    private int id;
    private String name;
    private MiscScrollType type;
    private boolean blessed;

    public MiscScroll() {
    }

    public MiscScroll(int id, String name, MiscScrollType type, boolean blessed) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.blessed = blessed;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MiscScrollType getType() {
        return type;
    }

    public boolean isBlessed() {
        return blessed;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s, %s)", name, id, type, blessed);
    }

    public static MiscScroll fromItem(L2Item item) {
        return new MiscScroll(item.getId(), item.getName(), MiscScrollType.fromName(item.getName()), item.getName().contains("Blessed"));
    }

}
