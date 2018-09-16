package com.l2jserver.gameserver.datatables.categorized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.gameserver.model.items.parts.ItemPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ItemPartsData {

    private static final Logger LOG = LoggerFactory.getLogger(ItemPartsData.class);

    private List<ItemPart> itemParts = new ArrayList<>();
    private Map<GradeInfo, Collection<ItemPart>> itemPartsMap = new HashMap<>();

    public ItemPartsData() {
        load();
    }

    private void load() {
        try {
            File gradedEquipment = new File("data/stats/categorized/item_parts.json");
            itemParts = new ObjectMapper().readValue(gradedEquipment, new TypeReference<List<ItemPart>>() {
            });

            Multimap<GradeInfo, ItemPart> itemPartMultimap = HashMultimap.create();
            itemParts.forEach(itemPart -> {
                Optional<GradedItem> gradedItemOption = GradedItemsData.getInstance().getItemById(itemPart.getItemId());
                if (gradedItemOption.isPresent()) {
                    itemPartMultimap.put(gradedItemOption.get().getGradeInfo(), itemPart);
                } else {
                    LOG.warn("Part {} is missing graded item and cannot added to grade cache", itemPart);
                }
            });
            itemPartsMap = itemPartMultimap.asMap();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read graded equipment data: " + e.getMessage());
        }
    }

    public List<ItemPart> getItemParts() {
        return itemParts;
    }

    public Optional<Collection<ItemPart>> getItemPartsByGradeInfo(GradeInfo gradeInfo) {
        return Optional.ofNullable(itemPartsMap.get(gradeInfo));
    }

    public static ItemPartsData getInstance() {
        return ItemPartsData.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final ItemPartsData _instance = new ItemPartsData();
    }

}
