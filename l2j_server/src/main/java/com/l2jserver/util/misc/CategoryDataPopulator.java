package com.l2jserver.util.misc;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.Config;
import com.l2jserver.gameserver.datatables.ItemSlots;
import com.l2jserver.gameserver.datatables.categorized.CategorizedDataTable;
import com.l2jserver.gameserver.datatables.categorized.EquipmentCategories;
import com.l2jserver.gameserver.model.items.L2Armor;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.util.CollectionUtil;
import com.l2jserver.util.file.filter.XMLFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CategoryDataPopulator {

    static class CategorizedItemData {

        private String itemCategory;
        private CrystalType crystalType;

        public CategorizedItemData(String itemCategory, CrystalType crystalType) {
            this.itemCategory = itemCategory;
            this.crystalType = crystalType;
        }

        public String getItemCategory() {
            return itemCategory;
        }

        public CrystalType getCrystalType() {
            return crystalType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CategorizedItemData that = (CategorizedItemData) o;
            return itemCategory.equals(that.itemCategory) &&
                    crystalType == that.crystalType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemCategory, crystalType);
        }

        @Override
        public String toString() {
            return "CategorizedItemData[" + itemCategory + ", " + crystalType + "]";
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Config.DATAPACK_ROOT = new File("l2j_datapack/dist/game");

        List<L2Item> allEquipment = CategorizedDataTable.getInstance().getCategorizedItems().getAllEquipment();

        Multimap<CategorizedItemData, L2Item> categorizedValues = LinkedHashMultimap.create();
        allEquipment.forEach(equipment -> {
            ItemSlots slot = ItemSlots.bySlotNumber(equipment.getBodyPart()).orElse(ItemSlots.NONE);
            String categoryName = slot.getName();
            if (equipment instanceof L2Armor) {
                L2Armor armor = (L2Armor) equipment;
                categoryName += armor.getItemType().name();
            } else if (equipment instanceof L2Weapon) {
                L2Weapon weapon = (L2Weapon) equipment;
                categoryName += weapon.getItemType().name();
            }

            categorizedValues.put(new CategorizedItemData(categoryName, equipment.getCrystalType()), equipment);
        });

        Multimap<Integer, EquipmentCategories> equipmentCategoriesMultimap = HashMultimap.create();

        categorizedValues.asMap().forEach((key, value) -> {
            List<L2Item> values = new ArrayList<>(value);
            values.sort(Comparator.comparingInt(L2Item::getReferencePrice));

            int partsCount = partsNumberByGrade(key.getCrystalType());
            if (key.getCrystalType().equals(CrystalType.A) && (
                    key.getItemCategory().startsWith("lrhand") ||
                            key.getItemCategory().startsWith("lhand") ||
                            key.getItemCategory().startsWith("rhand")
            )) {
                partsCount = 3;
            }

            List<List<L2Item>> partitioned = CollectionUtil.splitList(values, partsCount);


            if (partitioned.size() == 1) {
                System.out.println("Category " + key + " items: " + Joiner.on(", ").join(partitioned.get(0)));
            } else if (partitioned.size() == 2) {
                System.out.println("Category " + key + " low items: " + Joiner.on(", ").join(partitioned.get(0)) + "\n top items: " + Joiner.on(", ").join(partitioned.get(1)));
            } else if (partitioned.size() == 3) {
                System.out.println("Category " + key + " low items: " + Joiner.on(", ").join(partitioned.get(0)) + "\n mid items: " + Joiner.on(", ").join(partitioned.get(1)) + "\ntop items: " + Joiner.on(", ").join(partitioned.get(2)));
            } else {
                System.out.println("Strange size " + partitioned.size());
            }

            if (CrystalType.NONE.equals(key.getCrystalType())) {
                if (partitioned.size() == 1) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.MID_NG));
                } else if (partitioned.size() == 2) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_NG));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_NG));
                } else if (partitioned.size() == 3) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_NG));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.MID_NG));
                    partitioned.get(2).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_NG));
                }
            } else if (CrystalType.D.equals(key.getCrystalType())) {
                if (partitioned.size() == 1) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.MID_D));
                } else if (partitioned.size() == 2) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_D));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_D));
                } else if (partitioned.size() == 3) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_D));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.MID_D));
                    partitioned.get(2).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_D));
                }
            } else if (CrystalType.C.equals(key.getCrystalType())) {
                if (partitioned.size() == 1) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.MID_C));
                } else if (partitioned.size() == 2) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_C));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_C));
                } else if (partitioned.size() == 3) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_C));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.MID_C));
                    partitioned.get(2).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_C));
                }
            } else if (CrystalType.B.equals(key.getCrystalType())) {
                if (partitioned.size() == 1) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_B));
                } else if (partitioned.size() == 2) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_B));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_B));
                }
            } else if (CrystalType.A.equals(key.getCrystalType())) {
                if (partitioned.size() == 1) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_A));
                } else if (partitioned.size() == 2) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_A));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_A));
                } else if (partitioned.size() == 3) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_A));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.MID_A));
                    partitioned.get(2).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_A));
                }
            } else if (CrystalType.S.equals(key.getCrystalType())) {
                if (partitioned.size() == 1) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.S));
                } else if (partitioned.size() == 2) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.S));
                    partitioned.get(1).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_S80));
                }
            } else if (CrystalType.S80.equals(key.getCrystalType())) {
                if (partitioned.size() == 1) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.TOP_S80));
                }
            } else if (CrystalType.S84.equals(key.getCrystalType())) {
                if (partitioned.size() == 1) {
                    partitioned.get(0).forEach(item -> equipmentCategoriesMultimap.put(item.getId(), EquipmentCategories.LOW_S84));
                }
            }
        });

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        File[] itemFiles = new File(Config.DATAPACK_ROOT + "/data/stats/items").listFiles(new XMLFilter());

        for (File itemFile : itemFiles) {
            Document document = factory.newDocumentBuilder().parse(itemFile);
            NodeList itemNodes = document.getElementsByTagName("item");
            int updates = 0;
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Node itemNode = itemNodes.item(i);
                Integer id = Integer.parseInt(itemNode.getAttributes().getNamedItem("id").getNodeValue().trim());

                if (equipmentCategoriesMultimap.containsKey(id)) {
                        Collection<EquipmentCategories> categories = equipmentCategoriesMultimap.get(id);
                        Element categorySetNode = document.createElement("set");
                        categorySetNode.setAttribute("name", "grade_category");
                        categorySetNode.setAttribute("val", Joiner.on(';').join(categories));
                        itemNode.appendChild(categorySetNode);
                        updates++;
                }
            }
            if (updates > 0) {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(document);

                StreamResult outFile = new StreamResult(itemFile);
                transformer.transform(source, outFile);
            }
        }
    }

    private static int partsNumberByGrade(CrystalType crystalType) {
        int partsCount = 3;
        if (crystalType.equals(CrystalType.S)) {
            partsCount = 2;
        } else if (crystalType.equals(CrystalType.B)) {
            partsCount = 2;
        } else if (crystalType.equals(CrystalType.A)) {
            partsCount = 2;
        } else if (crystalType.equals(CrystalType.S80)) {
            partsCount = 1;
        } else if (crystalType.equals(CrystalType.S84)) {
            partsCount = 1;
        }
        return partsCount;
    }

}
