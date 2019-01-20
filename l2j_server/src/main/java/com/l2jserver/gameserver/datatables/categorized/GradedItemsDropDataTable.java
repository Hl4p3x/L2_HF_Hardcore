package com.l2jserver.gameserver.datatables.categorized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.gameserver.datatables.categorized.interfaces.EquipmentProvider;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.GradeInfoHelper;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GradedItemsDropDataTable implements EquipmentProvider<GradedItem> {

    private static final Logger LOG = LoggerFactory.getLogger(GradedItemsDropDataTable.class);

    private Map<GradeInfo, List<GradedItem>> allGradedItemsMap = new HashMap<>();
    private Multimap<Integer, GradedItem> allGradedItemById = ArrayListMultimap.create();

    private Map<GradeInfo, List<GradedItem>> gradedWeaponsMap = new HashMap<>();
    private Map<GradeInfo, List<GradedItem>> gradedArmorMap = new HashMap<>();
    private Map<GradeInfo, List<GradedItem>> gradedJewelsMap = new HashMap<>();

    private Set<Integer> allGradedIds = new HashSet<>();


    public GradedItemsDropDataTable() {
        load();
    }

    public void load() {
        List<GradedItem> gradedWeapons = loadGraded("data/stats/categorized/graded_weapon.json");
        gradedWeaponsMap = buildGradeMap(gradedWeapons);

        List<GradedItem> gradedArmor = loadGraded("data/stats/categorized/graded_armor.json");
        gradedArmorMap = buildGradeMap(gradedArmor);

        List<GradedItem> gradedJewels = loadGraded("data/stats/categorized/graded_jewels.json");
        gradedJewelsMap = buildGradeMap(gradedJewels);

        List<GradedItem> allGradedItems = new ArrayList<>();
        allGradedItems.addAll(gradedWeapons);
        allGradedItems.addAll(gradedArmor);
        allGradedItems.addAll(gradedJewels);

        allGradedItemsMap = buildGradeMap(allGradedItems);

        allGradedItemById = buildIdMap(allGradedItems);

        allGradedIds = CollectionUtil.extract(allGradedItems, GradedItem::getItemId);
        LOG.info("Loaded {} graded items!", allGradedIds.size());
    }

    private Multimap<Integer, GradedItem> buildIdMap(List<GradedItem> allGradedItems) {
        allGradedItemById = ArrayListMultimap.create();
        allGradedItems.forEach(item -> allGradedItemById.put(item.getItemId(), item));
        return allGradedItemById;
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

    public List<GradedItem> getWeaponsByGrade(GradeInfo gradeInfo) {
        return GradeInfoHelper.findAllByGradeInfo(gradeInfo, gradedWeaponsMap);
    }

    public List<GradedItem> getArmorByGrade(GradeInfo gradeInfo) {
        return GradeInfoHelper.findAllByGradeInfo(gradeInfo, gradedArmorMap);
    }

    public List<GradedItem> getJewelsByGrade(GradeInfo gradeInfo) {
        return GradeInfoHelper.findAllByGradeInfo(gradeInfo, gradedJewelsMap);
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

    public Collection<GradedItem> getItemById(int itemId) {
        return allGradedItemById.get(itemId);
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
