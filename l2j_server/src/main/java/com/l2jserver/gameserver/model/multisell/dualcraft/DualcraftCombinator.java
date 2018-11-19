package com.l2jserver.gameserver.model.multisell.dualcraft;

import com.l2jserver.gameserver.model.items.interfaces.EnchantableItemObject;

import java.util.*;
import java.util.stream.Collectors;

public class DualcraftCombinator {

    private List<EnchantableItemObject> findByIdSortedByEnchant(int weaponId, List<EnchantableItemObject> items) {
        return items.stream()
                .filter(item -> item.getItemId() == weaponId)
                .sorted(Comparator.comparing(EnchantableItemObject::getEnchantLevel).reversed())
                .collect(Collectors.toList());
    }

    private List<DualcraftWeaponObject> processSameWeaponDuals(DualcraftTemplate dualcraftTemplate, List<EnchantableItemObject> items) {
        List<EnchantableItemObject> dualcraftItems = findByIdSortedByEnchant(dualcraftTemplate.getLeftWeaponId(), items);

        List<DualcraftWeaponObject> dualcraftWeaponObjects = new ArrayList<>();
        Set<Integer> uniqueEnchantLevels = new LinkedHashSet<>();
        Iterator<EnchantableItemObject> iterator = dualcraftItems.iterator();
        while (iterator.hasNext()) {
            EnchantableItemObject current = iterator.next();
            if (iterator.hasNext()) {
                EnchantableItemObject next = iterator.next();
                int combinationEnchantLevel = Math.min(current.getEnchantLevel(), next.getEnchantLevel());
                if (!uniqueEnchantLevels.contains(combinationEnchantLevel)) {
                    uniqueEnchantLevels.add(combinationEnchantLevel);
                    dualcraftWeaponObjects.add(new DualcraftWeaponObject(current.getObjectId(), next.getObjectId(), dualcraftTemplate.getDualWeaponId(), combinationEnchantLevel));
                }
            }
        }

        return dualcraftWeaponObjects;
    }

    private List<DualcraftWeaponObject> processDifferentWeaponDuals(DualcraftTemplate dualcraftTemplate, List<EnchantableItemObject> items) {
        List<EnchantableItemObject> leftHandItems = findByIdSortedByEnchant(dualcraftTemplate.getLeftWeaponId(), items);
        List<EnchantableItemObject> rightHandItems = findByIdSortedByEnchant(dualcraftTemplate.getRightWeaponId(), items);

        Set<Integer> leftEnchantLevels = leftHandItems.stream().map(EnchantableItemObject::getEnchantLevel).collect(Collectors.toSet());
        Set<Integer> rightEnchantLevels = rightHandItems.stream().map(EnchantableItemObject::getEnchantLevel).collect(Collectors.toSet());
        Set<Integer> allEnchantLevels = new HashSet<>();
        allEnchantLevels.addAll(leftEnchantLevels);
        allEnchantLevels.addAll(rightEnchantLevels);

        List<DualcraftWeaponObject> dualcraftWeaponObjects = new ArrayList<>();
        for (Integer enchantLevel : allEnchantLevels) {
            Optional<EnchantableItemObject> leftWeaponOptional = leftHandItems.stream().filter(item -> enchantLevel <= item.getEnchantLevel()).min(Comparator.comparing(EnchantableItemObject::getEnchantLevel));
            Optional<EnchantableItemObject> rightWeaponOptional = rightHandItems.stream().filter(item -> enchantLevel <= item.getEnchantLevel()).min(Comparator.comparing(EnchantableItemObject::getEnchantLevel));
            if (leftWeaponOptional.isPresent() && rightWeaponOptional.isPresent()) {
                dualcraftWeaponObjects.add(new DualcraftWeaponObject(leftWeaponOptional.get().getObjectId(), rightWeaponOptional.get().getObjectId(), dualcraftTemplate.getDualWeaponId(), enchantLevel));
            }
        }

        return dualcraftWeaponObjects;
    }

    public List<DualcraftWeaponObject> findPossibleEnchantmentLevels(DualcraftTemplate dualcraftTemplate, List<EnchantableItemObject> items) {
        List<DualcraftWeaponObject> result;
        if (dualcraftTemplate.isSameWeaponDual()) {
            result = processSameWeaponDuals(dualcraftTemplate, items);
        } else {
            result = processDifferentWeaponDuals(dualcraftTemplate, items);
        }
        return result.stream().sorted(Comparator.comparing(DualcraftWeaponObject::getEnchantLevel).reversed()).collect(Collectors.toList());
    }

}
