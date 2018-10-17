package handlers.bypasshandlers.npcviewmod;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.drop.*;
import com.l2jserver.gameserver.model.actor.templates.drop.calculators.DynamicDropCalculator;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropTable;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import com.l2jserver.gameserver.model.drops.DropListScope;
import com.l2jserver.gameserver.model.drops.GeneralDropItem;
import com.l2jserver.gameserver.model.drops.GroupedGeneralDropItem;
import com.l2jserver.gameserver.model.drops.IDropItem;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import handlers.bypasshandlers.NpcViewMod;
import handlers.bypasshandlers.npcviewmod.render.ItemGroupView;
import handlers.bypasshandlers.npcviewmod.render.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DynamicDropView implements DropView {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicDropView.class);

    private static final int DROP_LIST_ITEMS_PER_PAGE = 10;

    public Optional<String> render(L2PcInstance activeChar, L2Npc npc, DropListScope dropListScope, int page) {
        String htmlTemplate = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/NpcView/DropList.htm");
        if (htmlTemplate == null) {
            LOG.warn(NpcViewMod.class.getSimpleName() + ": The html file data/html/mods/NpcView/DropList.htm could not be found.");
            return Optional.empty();
        }

        DynamicDropGradeData dynamicDropGradeData = DynamicDropTable.getInstance().getDynamicNpcDropData(npc);

        List<ItemGroupView> equipmentGroupViews = convertEquipmentCategoryToView("Equipment", dynamicDropGradeData.getEquipment());
        List<ItemGroupView> partsGroupViews = convertEquipmentCategoryToView("Parts", dynamicDropGradeData.getParts());
        List<ItemGroupView> recipesGroupViews = convertEquipmentCategoryToView("Recipes", dynamicDropGradeData.getRecipes());
        List<ItemGroupView> resourceGroupViews = convertResourceCategoryToView(dynamicDropGradeData.getResources());
        List<ItemGroupView> scrollsGroupViews = convertScrollsGroupCategoryToView(dynamicDropGradeData);
        List<ItemGroupView> regularDropGroupViews = convertRegularDropList(npc, dropListScope);

        List<ItemGroupView> allGroupViews = new ArrayList<>();
        allGroupViews.addAll(equipmentGroupViews);
        allGroupViews.addAll(partsGroupViews);
        allGroupViews.addAll(recipesGroupViews);
        allGroupViews.addAll(resourceGroupViews);
        allGroupViews.addAll(scrollsGroupViews);
        allGroupViews.addAll(regularDropGroupViews);

        int totalItems = allGroupViews.stream().mapToInt(ItemGroupView::getSize).sum();
        int pages = (int) Math.ceil((double) totalItems / DROP_LIST_ITEMS_PER_PAGE);
        int pageOffset = page * DROP_LIST_ITEMS_PER_PAGE;

        htmlTemplate = htmlTemplate.replaceAll("%name%", npc.getName());
        htmlTemplate = htmlTemplate.replaceAll("%dropListButtons%", DropListButtonsView.render(npc));
        htmlTemplate = htmlTemplate.replaceAll("%pages%", DropListPagesView.render(pages, npc, DropListScope.DEATH));
        htmlTemplate = htmlTemplate.replaceAll("%items%", renderItems(pageOffset, DROP_LIST_ITEMS_PER_PAGE, allGroupViews));

        return Optional.of(htmlTemplate);
    }

    private List<ItemGroupView> convertRegularDropList(L2Npc npc, DropListScope dropListScope) {
        List<IDropItem> dropItems = npc.getTemplate().getDropList(dropListScope);
        if (dropItems == null || dropItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemGroupView> result = new ArrayList<>();
        for (IDropItem dropItem : dropItems) {
            if (dropItem instanceof GeneralDropItem) {
                GeneralDropItem generalDropItem = (GeneralDropItem) dropItem;
                if (DynamicDropCalculator.getInstance().getAllDynamicItemsIds().contains(generalDropItem.getItemId())) {
                    continue;
                }

                List<ItemView> views = Collections.singletonList(convertGeneralDropToView(npc, generalDropItem));
                result.add(new ItemGroupView(
                        "Other",
                        DropStats.empty(),
                        views
                ));
            } else if (dropItem instanceof GroupedGeneralDropItem) {
                GroupedGeneralDropItem generalGroupedDropItem = (GroupedGeneralDropItem) dropItem;

                List<ItemView> views = generalGroupedDropItem.getItems()
                        .stream()
                        .filter(item -> !DynamicDropCalculator.getInstance().getAllDynamicItemsIds().contains(item.getItemId()))
                        .map(generalDropItem -> convertGeneralDropToView(npc, generalDropItem))
                        .collect(Collectors.toList());
                result.add(new ItemGroupView("Other", new DropStats(generalGroupedDropItem.getChance()), views));
            }
        }

        return result;
    }

    private DropStats convertGeneralDropToStats(L2Npc npc, GeneralDropItem generalDropItem) {
        DropCountViewCalculator.MinMax minMax = DropCountViewCalculator.getPreciseMinMax(generalDropItem.getChance(), generalDropItem.getMin(npc), generalDropItem.getMax(npc), generalDropItem.isPreciseCalculated());
        //TODO precision
        Range countRange = new Range((int) minMax.getMin(), (int) minMax.getMax());
        return new DropStats(new Range(1), Math.min(generalDropItem.getChance(), 100), countRange);
    }

    private ItemView convertGeneralDropToView(L2Npc npc, GeneralDropItem generalDropItem) {
        L2Item l2Item = ItemTable.getInstance().getTemplate(generalDropItem.getItemId());
        return new ItemView(l2Item.getId(), l2Item.getName(), l2Item.getIcon(), convertGeneralDropToStats(npc, generalDropItem));
    }

    public String renderItems(int pageOffset, int itemsToRender, List<ItemGroupView> allGroupViews) {
        Iterator<ItemGroupView> itemGroupViewIterator = allGroupViews.iterator();

        StringBuilder sb = new StringBuilder();
        int groupStartPosition = 0;
        int groupEndPosition = 0;
        while (itemGroupViewIterator.hasNext()) {
            ItemGroupView itemGroupView = itemGroupViewIterator.next();
            groupStartPosition = groupEndPosition;
            groupEndPosition += itemGroupView.getSize();
            if (groupStartPosition <= pageOffset && pageOffset < groupEndPosition) {
                int renderRangeStart = pageOffset - groupStartPosition;
                int renderRangeEnd = itemsToRender + renderRangeStart;
                int renderRangeEndIndex = renderRangeEnd - 1;
                if (renderRangeEndIndex < itemGroupView.getSize()) {
                    sb.append(itemGroupView.render(new Range(renderRangeStart, renderRangeEndIndex)));
                } else {
                    sb.append(itemGroupView.render(new Range(renderRangeStart, itemGroupView.getSize() - 1)));
                    int itemsRendered = groupEndPosition - pageOffset;
                    pageOffset += itemsRendered;
                    itemsToRender -= itemsRendered;
                }
            }
        }

        return sb.toString();
    }

    private List<ItemGroupView> convertScrollsGroupCategoryToView(DynamicDropGradeData dynamicDropGradeData) {
        List<ItemGroupView> result = new ArrayList<>();
        result.addAll(convertScrollsCategoryToView("Weapon Scrolls", dynamicDropGradeData.getWeaponScrolls()));
        result.addAll(convertScrollsCategoryToView("Armor Scrolls", dynamicDropGradeData.getArmorScrolls()));
        result.addAll(convertScrollsCategoryToView("Misc Scrolls", dynamicDropGradeData.getMiscScrolls()));
        return result;
    }

    private List<ItemGroupView> convertScrollsCategoryToView(String categoryName, DynamicDropScrollCategory dynamicDropScrollCategory) {
        List<ItemGroupView> result = new ArrayList<>();
        result.add(convertDynamicDropCategory("Normal" + " " + categoryName, dynamicDropScrollCategory.getNormal()));
        result.add(convertDynamicDropCategory("Blessed" + " " + categoryName, dynamicDropScrollCategory.getBlessed()));
        return result;
    }

    private List<ItemGroupView> convertResourceCategoryToView(List<DynamicDropResourcesCategory> resourcesCategories) {
        List<ItemGroupView> result = new ArrayList<>();
        for (DynamicDropResourcesCategory dynamicDropResourcesCategory : resourcesCategories) {
            result.add(convertDynamicDropCategory(dynamicDropResourcesCategory.getResourceGrade().toString(), dynamicDropResourcesCategory.getDynamicDropCategory()));
        }
        return result;
    }

    private List<ItemGroupView> convertEquipmentCategoryToView(String group, DynamicDropEquipmentCategory dynamicDropEquipmentCategory) {
        List<ItemGroupView> result = new ArrayList<>();
        GradeInfo gradeInfo = dynamicDropEquipmentCategory.getGradeInfo();
        result.add(convertDynamicDropCategory(group + " - " + gradeInfo + " Weapons ", dynamicDropEquipmentCategory.getWeapons()));
        result.add(convertDynamicDropCategory(group + " - " + gradeInfo + " Armor", dynamicDropEquipmentCategory.getArmor()));
        result.add(convertDynamicDropCategory(group + " - " + gradeInfo + " Jewels", dynamicDropEquipmentCategory.getJewels()));
        return result;
    }

    private ItemGroupView convertDynamicDropCategory(String categoryName, DynamicDropCategory dynamicDropCategory) {
        List<ItemView> items = convertCategoryToItems(dynamicDropCategory)
                .stream()
                .map(l2Item -> new ItemView(l2Item.getId(), l2Item.getName(), l2Item.getIcon()))
                .collect(Collectors.toList());
        return new ItemGroupView(categoryName, dynamicDropCategory.getStats(), items);
    }

    private List<L2Item> convertCategoryToItems(DynamicDropCategory dynamicDropCategory) {
        return dynamicDropCategory.getIds().stream().map(ItemTable.getInstance()::getTemplate).collect(Collectors.toList());
    }

}

