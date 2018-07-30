package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import com.l2jserver.gameserver.model.items.L2Armor;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.ArmorType;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompositeUsingTest {
    @Test
    void testMixedUsing() {
        int weaponMask = 0;
        weaponMask |= WeaponType.DAGGER.mask();
        weaponMask |= WeaponType.DUALDAGGER.mask();

        ConditionUsingItemType weaponCondition = new ConditionUsingItemType(weaponMask);
        ConditionUsingItemType armorCondition = new ConditionUsingItemType(ArmorType.LIGHT.mask());
        ConditionUsingItemType heavyArmorCondition = new ConditionUsingItemType(ArmorType.HEAVY.mask());

        ConditionLogicAnd both = new ConditionLogicAnd();
        both.add(weaponCondition);
        both.add(armorCondition);

        L2Character effector = mock(L2Character.class);
        when(effector.isPlayer()).thenReturn(true);

        L2Armor chestArmorItem = mock(L2Armor.class);
        when(chestArmorItem.getItemType()).thenReturn(ArmorType.LIGHT);
        when(chestArmorItem.getBodyPart()).thenReturn(L2Item.SLOT_CHEST);

        L2ItemInstance chestArmor = mock(L2ItemInstance.class);
        when(chestArmor.getItem()).thenReturn(chestArmorItem);

        L2Armor legArmorItem = mock(L2Armor.class);
        when(legArmorItem.getItemType()).thenReturn(ArmorType.LIGHT);

        L2ItemInstance legArmor = mock(L2ItemInstance.class);
        when(legArmor.getItem()).thenReturn(legArmorItem);

        Inventory inventory = mock(PcInventory.class);
        when(inventory.getPaperdollItem(Inventory.PAPERDOLL_CHEST)).thenReturn(chestArmor);
        when(inventory.getPaperdollItem(Inventory.PAPERDOLL_LEGS)).thenReturn(legArmor);

        when(inventory.getWearedMask()).thenReturn(weaponMask);

        when(effector.getInventory()).thenReturn(inventory);

        assertTrue(weaponCondition.test(effector, null, null, null));
        assertTrue(armorCondition.test(effector, null, null, null));
        assertTrue(both.test(effector, null, null, null));

        both.add(heavyArmorCondition);
        assertFalse(both.test(effector, null, null, null));
    }
}