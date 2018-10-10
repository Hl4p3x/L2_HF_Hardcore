package com.l2jserver.gameserver.datatables.categorized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GradedItemsDropDataTable {

    private static final Logger LOG = LoggerFactory.getLogger(GradedItemsDropDataTable.class);

    private List<GradedItem> allGradedItems = new ArrayList<>();
    private Map<GradeInfo, List<GradedItem>> allGradedItemsMap = new HashMap<>();
    private Map<Integer, GradedItem> allGradedItemById = new HashMap<>();

    private Map<GradeInfo, List<GradedItem>> gradedWeaponsMap = new HashMap<>();
    private Map<GradeInfo, List<GradedItem>> gradedArmorMap = new HashMap<>();
    private Map<GradeInfo, List<GradedItem>> gradedJewelsMap = new HashMap<>();

    private Set<Integer> allGradedIds = new HashSet<>();


    public GradedItemsDropDataTable() {
        load();
    }

    private void load() {
        List<GradedItem> gradedWeapons = loadGraded("data/stats/categorized/graded_weapon.json");
        gradedWeaponsMap = buildGradeMap(gradedWeapons);

        List<GradedItem> gradedArmor = loadGraded("data/stats/categorized/graded_armor.json");
        gradedArmorMap = buildGradeMap(gradedArmor);

        List<GradedItem> gradedJewels = loadGraded("data/stats/categorized/graded_jewels.json");
        gradedJewelsMap = buildGradeMap(gradedJewels);

        allGradedItems.addAll(gradedWeapons);
        allGradedItems.addAll(gradedArmor);
        allGradedItems.addAll(gradedJewels);

        allGradedItemsMap = buildGradeMap(allGradedItems);

        allGradedItemById = buildIdMap(allGradedItems);

        allGradedIds = CollectionUtil.extract(allGradedItems, GradedItem::getItemId);
        LOG.info("Loaded {} graded items!", allGradedIds.size());
    }

    private Map<Integer, GradedItem> buildIdMap(List<GradedItem> allGradedItems) {
        return allGradedItems.stream().collect(Collectors.toMap(GradedItem::getItemId, Function.identity()));
    }

    private Map<GradeInfo, List<GradedItem>> buildGradeMap(List<GradedItem> gradedItems) {
        Map<GradeInfo, List<GradedItem>> gradedItemsMap = new HashMap<>();
        Multimap<GradeInfo, GradedItem> gradedItemMultimap = HashMultimap.create();
        gradedItems.forEach(item -> gradedItemMultimap.put(item.getGradeInfo(), item));
        gradedItemMultimap.asMap().forEach((key, value) -> {
            gradedItemsMap.put(key, new ArrayList<>(value));
        });
        return gradedItemsMap;
    }

    private List<GradedItem> loadGraded(String filePath) {
        try {
            File gradedEquipment = new File(filePath);
            return new ObjectMapper().readValue(gradedEquipment, new TypeReference<List<GradedItem>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Could not read graded equipment data: " + e.getMessage());
        }
    }

    public Map<GradeInfo, List<GradedItem>> getAllGradedItemsMap() {
        return allGradedItemsMap;
    }

    public Map<GradeInfo, List<GradedItem>> getGradedWeaponsMap() {
        return gradedWeaponsMap;
    }

    public Map<GradeInfo, List<GradedItem>> getGradedArmorMap() {
        return gradedArmorMap;
    }

    public Map<GradeInfo, List<GradedItem>> getGradedJewelsMap() {
        return gradedJewelsMap;
    }

    public Optional<GradedItem> getItemById(int itemId) {
        return Optional.ofNullable(allGradedItemById.get(itemId));
    }

    public Set<Integer> getGradedItemsIds() {
        return allGradedIds;
    }

    public static GradedItemsDropDataTable getInstance() {
        return GradedItemsDropDataTable.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final GradedItemsDropDataTable _instance = new GradedItemsDropDataTable();
    }

}
