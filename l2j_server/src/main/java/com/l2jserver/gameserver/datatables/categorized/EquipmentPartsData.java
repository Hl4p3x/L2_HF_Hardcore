package com.l2jserver.gameserver.datatables.categorized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.l2jserver.gameserver.model.items.parts.ItemPart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EquipmentPartsData {

    private List<ItemPart> itemParts = new ArrayList<>();

    public EquipmentPartsData() {
        load();
    }

    private void load() {
        try {
            File gradedEquipment = new File("data/stats/categorized/craftable_item_parts.json");
            itemParts = new ObjectMapper().readValue(gradedEquipment, new TypeReference<List<ItemPart>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Could not read graded equipment data: " + e.getMessage());
        }
    }

    public List<ItemPart> getItemParts() {
        return itemParts;
    }

    public static EquipmentPartsData getInstance() {
        return EquipmentPartsData.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final EquipmentPartsData _instance = new EquipmentPartsData();
    }


}
