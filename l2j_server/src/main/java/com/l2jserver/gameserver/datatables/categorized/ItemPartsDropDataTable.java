package com.l2jserver.gameserver.datatables.categorized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.gameserver.datatables.categorized.interfaces.EquipmentProvider;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.gameserver.model.items.parts.ItemPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemPartsDropDataTable implements EquipmentProvider<ItemPart> {

    private static final Logger LOG = LoggerFactory.getLogger(ItemPartsDropDataTable.class);

    private Set<Integer> allItemPartIds = new HashSet<>();
    private Map<GradeInfo, List<ItemPart>> weaponPartsMap = new HashMap<>();
    private Map<GradeInfo, List<ItemPart>> armorPartsMap = new HashMap<>();
    private Map<GradeInfo, List<ItemPart>> jewelPartsMap = new HashMap<>();

    public ItemPartsDropDataTable() {
        load();
    }

    public void load() {
        weaponPartsMap = loadCategory("data/stats/categorized/parts/weapon_parts.json");
        armorPartsMap = loadCategory("data/stats/categorized/parts/armor_parts.json");
        jewelPartsMap = loadCategory("data/stats/categorized/parts/jewel_parts.json");

        allItemPartIds = Stream.of(weaponPartsMap, armorPartsMap, jewelPartsMap)
                .flatMap(map -> map.values().stream().flatMap(Collection::stream))
                .map(ItemPart::getPartId)
                .collect(Collectors.toSet());

        LOG.info("Loaded {} item parts", allItemPartIds.size());
    }

    private Map<GradeInfo, List<ItemPart>> loadCategory(String path) {
        try {
            File itemPartsFile = new File(path);
            List<ItemPart> itemParts = new ObjectMapper().readValue(itemPartsFile, new TypeReference<List<ItemPart>>() {
            });


            Multimap<GradeInfo, ItemPart> itemPartMultimap = HashMultimap.create();
            itemParts.forEach(itemPart -> {
                Optional<GradedItem> gradedItemOption = GradedItemsDropDataTable.getInstance().getItemById(itemPart.getItemId());
                if (gradedItemOption.isPresent()) {
                    itemPartMultimap.put(gradedItemOption.get().getGradeInfo(), itemPart);
                } else {
                    LOG.warn("Part {} is missing graded item and cannot added to data table", itemPart);
                }
            });

            Map<GradeInfo, List<ItemPart>> itemPartsMap = new HashMap<>();
            itemPartMultimap.asMap().forEach((key, value) -> {
                itemPartsMap.put(key, new ArrayList<>(value));
            });

            return itemPartsMap;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read parts data from " + path + ": " + e.getMessage());
        }
    }

    public List<ItemPart> getWeaponsByGrade(GradeInfo gradeInfo) {
        return weaponPartsMap.getOrDefault(gradeInfo, new ArrayList<>());
    }

    public List<ItemPart> getArmorByGrade(GradeInfo gradeInfo) {
        return armorPartsMap.getOrDefault(gradeInfo, new ArrayList<>());
    }

    public List<ItemPart> getJewelsByGrade(GradeInfo gradeInfo) {
        return jewelPartsMap.getOrDefault(gradeInfo, new ArrayList<>());
    }

    public Set<Integer> getAllItemPartsIds() {
        return allItemPartIds;
    }

    public static ItemPartsDropDataTable getInstance() {
        return ItemPartsDropDataTable.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final ItemPartsDropDataTable _instance = new ItemPartsDropDataTable();
    }

}
