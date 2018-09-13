package com.l2jserver.gameserver.datatables.categorized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GradedEquipmentDataTable {

    private List<GradedItem> gradedItems = new ArrayList<>();

    public GradedEquipmentDataTable() {
        load();
    }

    private void load() {
        try {
            File gradedEquipment = new File("data/stats/categorized/graded_equipment.json");
            gradedItems = new ObjectMapper().readValue(gradedEquipment, new TypeReference<List<GradedItem>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Could not read graded equipment data: " + e.getMessage());
        }
    }

    public List<GradedItem> getGradedItems() {
        return gradedItems;
    }

    public Map<GradeInfo, Collection<GradedItem>> getGradedItemsMap() {
        Multimap<GradeInfo, GradedItem> result = LinkedHashMultimap.create();
        getGradedItems().forEach(item -> result.put(item.getGradeInfo(), item));
        return result.asMap();
    }

    public static GradedEquipmentDataTable getInstance() {
        return GradedEquipmentDataTable.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final GradedEquipmentDataTable _instance = new GradedEquipmentDataTable();
    }

}
