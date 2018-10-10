package com.l2jserver.gameserver.datatables.categorized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Functions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GradedItemsDropDataTable {

    private static final Logger LOG = LoggerFactory.getLogger(GradedItemsDropDataTable.class);

    private List<GradedItem> gradedItems = new ArrayList<>();
    private Map<Integer, GradedItem> gradedItemsById = new HashMap<>();
    private Map<GradeInfo, List<GradedItem>> gradedItemsMap = new HashMap<>();

    public GradedItemsDropDataTable() {
        load();
    }

    private void load() {
        try {
            File gradedEquipment = new File("data/stats/categorized/graded_items.json");
            gradedItems = new ObjectMapper().readValue(gradedEquipment, new TypeReference<List<GradedItem>>() {
            });

            Multimap<GradeInfo, GradedItem> gradedItemMultimap = HashMultimap.create();
            gradedItems.forEach(item -> gradedItemMultimap.put(item.getGradeInfo(), item));
            gradedItemMultimap.asMap().forEach((key, value) -> {
                gradedItemsMap.put(key, new ArrayList<>(value));
            });
            gradedItemsById = gradedItems.stream().collect(Collectors.toMap(GradedItem::getItemId, Functions.identity()));
            LOG.info("Loaded {} graded items!", gradedItems.size());
        } catch (IOException e) {
            throw new IllegalStateException("Could not read graded equipment data: " + e.getMessage());
        }
    }

    public List<GradedItem> getGradedItems() {
        return gradedItems;
    }

    public Map<GradeInfo, List<GradedItem>> getGradedItemsMap() {
        return Optional.ofNullable(gradedItemsMap).orElse(Collections.emptyMap());
    }

    public static GradedItemsDropDataTable getInstance() {
        return GradedItemsDropDataTable.SingletonHolder._instance;
    }

    public Optional<GradedItem> getItemById(int itemId) {
        return Optional.ofNullable(gradedItemsById.get(itemId));
    }

    public Set<Integer> getGradedItemsIds() {
        return gradedItemsById.keySet();
    }

    private static class SingletonHolder {
        protected static final GradedItemsDropDataTable _instance = new GradedItemsDropDataTable();
    }

}
