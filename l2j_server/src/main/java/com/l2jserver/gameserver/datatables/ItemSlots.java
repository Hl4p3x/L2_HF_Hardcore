package com.l2jserver.gameserver.datatables;

import com.l2jserver.gameserver.model.items.L2Item;

import java.util.Optional;
import java.util.stream.Stream;

public enum ItemSlots {

    SHIRT("shirt", L2Item.SLOT_UNDERWEAR),
    LEFT_BRACELET("lbracelet", L2Item.SLOT_L_BRACELET),
    RIGHT_BRACELET("rbracelet", L2Item.SLOT_R_BRACELET),
    TALISMAN("talisman", L2Item.SLOT_DECO),
    CHEST("chest", L2Item.SLOT_CHEST),
    FULL_ARMOR("fullarmor", L2Item.SLOT_FULL_ARMOR),
    HEAD("head", L2Item.SLOT_HEAD),
    HAIR("hair", L2Item.SLOT_HAIR),
    HAIR_ALL("hairall", L2Item.SLOT_HAIRALL),
    UNDERWEAR("underwear", L2Item.SLOT_UNDERWEAR),
    BACK("back", L2Item.SLOT_BACK),
    NECK("neck", L2Item.SLOT_NECK),
    LEGS("legs", L2Item.SLOT_LEGS),
    FEET("feet", L2Item.SLOT_FEET),
    GLOVES("gloves", L2Item.SLOT_GLOVES),
    CHEST_LEGS("chest,legs", L2Item.SLOT_CHEST | L2Item.SLOT_LEGS),
    BELT("belt", L2Item.SLOT_BELT),
    RIGHT_HAND("rhand", L2Item.SLOT_R_HAND),
    LEFT_HAND("lhand", L2Item.SLOT_L_HAND),
    BOTH_HANDS("lrhand", L2Item.SLOT_LR_HAND),
    BOTH_EARS("rear;lear", L2Item.SLOT_R_EAR | L2Item.SLOT_L_EAR),
    BOTH_FINGERS("rfinger;lfinger", L2Item.SLOT_R_FINGER | L2Item.SLOT_L_FINGER),
    WOLF("wolf", L2Item.SLOT_WOLF),
    GREAT_WOLF("greatwolf", L2Item.SLOT_GREATWOLF),
    HATCHLING("hatchling", L2Item.SLOT_HATCHLING),
    STRIDER("strider", L2Item.SLOT_STRIDER),
    BABY_PET("babypet", L2Item.SLOT_BABYPET),
    NONE("none", L2Item.SLOT_NONE),

    // retail compatibility
    ONE_PIECE("onepiece", L2Item.SLOT_FULL_ARMOR),
    HAIR_2("hair2", L2Item.SLOT_HAIR2),
    DHAIR("dhair", L2Item.SLOT_HAIRALL),
    ALL_DRESS("alldress", L2Item.SLOT_ALLDRESS),
    DECO("deco1", L2Item.SLOT_DECO),
    WAIST("waist", L2Item.SLOT_BELT);

    private final String name;
    private final Integer slot;

    ItemSlots(String name, int slot) {
        this.name = name;
        this.slot = slot;
    }

    public String getName() {
        return name;
    }

    public Integer getSlot() {
        return slot;
    }

    public static Optional<ItemSlots> bySlotNumber(int slot) {
        return Stream.of(values()).filter(itemSlot -> itemSlot.getSlot().equals(slot)).findFirst();
    }

}
