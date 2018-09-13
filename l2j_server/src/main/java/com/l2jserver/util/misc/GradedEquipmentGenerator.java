package com.l2jserver.util.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.Config;
import com.l2jserver.Server;
import com.l2jserver.gameserver.datatables.categorized.CategorizedDataTable;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.graded.Grade;
import com.l2jserver.gameserver.model.items.graded.GradeCategory;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.util.CollectionUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GradedEquipmentGenerator {

    public static void main(String[] args) throws IOException {
        Map<Grade, Integer> gradeParts = new HashMap<>();
        gradeParts.put(Grade.NG, 3);
        gradeParts.put(Grade.D, 3);
        gradeParts.put(Grade.C, 3);
        gradeParts.put(Grade.B, 2);
        gradeParts.put(Grade.A, 2);
        gradeParts.put(Grade.S, 2);
        gradeParts.put(Grade.S80, 1);
        gradeParts.put(Grade.S84, 1);

        Config.DATAPACK_ROOT = new File("l2j_datapack/dist/game");
        Server.serverMode = Server.MODE_GAMESERVER;
        Config.load();

        List<L2Item> allEquipment = CategorizedDataTable.getInstance().getCategorizedItems().getAllEquipment();

        Comparator<L2Item> crystalCompare = Comparator.comparing(item -> item.getCrystalType().getId());
        Comparator<L2Item> bodyPartCompare = Comparator.comparing(L2Item::getBodyPart);
        Comparator<L2Item> priceCompare = Comparator.comparing(L2Item::getReferencePrice);
        Comparator<L2Item> nameCompare = Comparator.comparing(L2Item::getName);

        List<L2Item> sorted = allEquipment.stream()
                .sorted(
                        crystalCompare.thenComparing(priceCompare).thenComparing(bodyPartCompare).thenComparing(nameCompare)
                )
                .collect(Collectors.toList());
        List<GradedItem> gradedItems = sorted.stream().map(item -> new GradedItem(item.getId(), item.getName(), item.getReferencePrice(), new GradeInfo(Grade.fromCrystalType(item.getCrystalType()), GradeCategory.MID))).collect(Collectors.toList());

        Multimap<Grade, GradedItem> gradedItemsByGrade = LinkedHashMultimap.create();
        gradedItems.forEach(item -> gradedItemsByGrade.put(item.getGradeInfo().getGrade(), item));

        gradedItemsByGrade.asMap().forEach((key, value) -> {
            List<List<GradedItem>> items = CollectionUtil.splitList(new ArrayList<>(value), gradeParts.get(key));
            int grade = 0;
            for (List<GradedItem> gradedItem : items) {
                for (GradedItem it : gradedItem) {
                    it.getGradeInfo().setCategory(GradeCategory.values()[grade]);
                }
                grade += 1;
            }
        });

        File gradedEquipment = new File("data/stats/categorized/graded_equipment.json");
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(gradedEquipment, gradedItems);
    }

}
