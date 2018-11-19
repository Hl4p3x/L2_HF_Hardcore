package com.l2jserver.gameserver.model.multisell.dualcraft;

import com.l2jserver.gameserver.model.items.interfaces.EnchantableItemObject;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class DualcraftCombinatorTest {

    DualcraftCombinator dualcraftCombinator = new DualcraftCombinator();

    @Test
    void findSingleEqualLevelPair() {
        int testEnchantmentLevel = 5;
        int testWeaponId = 1;
        int testDualsId = 100;
        int testFirstWeaponObjectId = 1;
        int testSecondWeaponObjectId = 2;
        List<DualcraftWeaponObject> result = dualcraftCombinator.findPossibleEnchantmentLevels(
                new DualcraftTemplate(testWeaponId, testWeaponId, testDualsId),
                Arrays.asList(
                        new TestEnchantableItem(testFirstWeaponObjectId, testWeaponId, testEnchantmentLevel),
                        new TestEnchantableItem(testSecondWeaponObjectId, testWeaponId, testEnchantmentLevel)
                )
        );
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(Collections.singletonList(new DualcraftWeaponObject(testFirstWeaponObjectId, testSecondWeaponObjectId, testDualsId, testEnchantmentLevel)));
    }

    @Test
    void findSingleDifferentLevelPair() {
        int testWeaponId = 1;
        int testDualsId = 100;
        int leftWeaponEnchantLevel = 4;
        int rightWeaponEnchantLevel = 5;
        int expectedEnchantmentLevel = 4;
        int testFirstWeaponObjectId = 1;
        int testSecondWeaponObjectId = 2;
        List<DualcraftWeaponObject> result = dualcraftCombinator.findPossibleEnchantmentLevels(
                new DualcraftTemplate(testWeaponId, testWeaponId, testDualsId),
                Arrays.asList(
                        new TestEnchantableItem(testFirstWeaponObjectId, testWeaponId, leftWeaponEnchantLevel),
                        new TestEnchantableItem(testSecondWeaponObjectId, testWeaponId, rightWeaponEnchantLevel)
                )
        );

        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(Collections.singletonList(new DualcraftWeaponObject(testSecondWeaponObjectId, testFirstWeaponObjectId, testDualsId, expectedEnchantmentLevel)));
    }

    @Test
    void findMultipleEqualLevels() {
        int testEnchantmentLevel = 5;
        int testLeftWeaponId = 1;
        int testRightWeaponId = 2;
        int testDualWeaponId = 300;
        List<DualcraftWeaponObject> result = dualcraftCombinator.findPossibleEnchantmentLevels(
                new DualcraftTemplate(testLeftWeaponId, testRightWeaponId, testDualWeaponId),
                Arrays.asList(
                        new TestEnchantableItem(1, testLeftWeaponId, testEnchantmentLevel),
                        new TestEnchantableItem(2, testRightWeaponId, testEnchantmentLevel),
                        new TestEnchantableItem(3, testRightWeaponId, testEnchantmentLevel)
                )
        );

        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(Collections.singletonList(new DualcraftWeaponObject(1, 2, testDualWeaponId, testEnchantmentLevel)));
    }

    @Test
    void findMultipleDifferentLevels() {
        int testLeftWeaponId = 1;
        int testRightWeaponId = 2;
        int testDualWeaponId = 300;
        List<DualcraftWeaponObject> result = dualcraftCombinator.findPossibleEnchantmentLevels(
                new DualcraftTemplate(testLeftWeaponId, testRightWeaponId, testDualWeaponId),
                Arrays.asList(
                        new TestEnchantableItem(1, testLeftWeaponId, 8),
                        new TestEnchantableItem(2, testLeftWeaponId, 5),
                        new TestEnchantableItem(3, testLeftWeaponId, 3),
                        new TestEnchantableItem(4, testLeftWeaponId, 1),

                        new TestEnchantableItem(5, testRightWeaponId, 8),
                        new TestEnchantableItem(6, testRightWeaponId, 6),
                        new TestEnchantableItem(7, testRightWeaponId, 2)
                )
        );

        assertThat(result).hasSize(6);
        assertThat(result).isEqualTo(
                Arrays.asList(
                        new DualcraftWeaponObject(1, 5, 300, 8),
                        new DualcraftWeaponObject(1, 6, 300, 6),
                        new DualcraftWeaponObject(2, 6, 300, 5),
                        new DualcraftWeaponObject(3, 6, 300, 3),
                        new DualcraftWeaponObject(3, 7, 300, 2),
                        new DualcraftWeaponObject(4, 7, 300, 1)
                )
        );
    }

    static class TestEnchantableItem implements EnchantableItemObject {

        private int objectId;
        private int itemId;
        private int enchantLevel;

        public TestEnchantableItem(int objectId, int itemId, int enchantLevel) {
            this.objectId = objectId;
            this.itemId = itemId;
            this.enchantLevel = enchantLevel;
        }

        @Override
        public int getObjectId() {
            return objectId;
        }

        @Override
        public int getItemId() {
            return itemId;
        }

        @Override
        public int getEnchantLevel() {
            return enchantLevel;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestEnchantableItem that = (TestEnchantableItem) o;
            return objectId == that.objectId &&
                    itemId == that.itemId &&
                    enchantLevel == that.enchantLevel;
        }

        @Override
        public int hashCode() {
            return Objects.hash(objectId, itemId, enchantLevel);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", TestEnchantableItem.class.getSimpleName() + "[", "]")
                    .add(Objects.toString(objectId))
                    .add(Objects.toString(itemId))
                    .add(Objects.toString(enchantLevel))
                    .toString();
        }

    }

}