package custom.clan.territory;

import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.categorized.CraftResourcesDropDataTable;
import com.l2jserver.gameserver.model.items.craft.CraftResource;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;

import java.util.*;
import java.util.stream.Collectors;

public class TerritoryOwningRewardsGenerator {

    public static List<String> ignoreList = Arrays.asList("Gemstone", "Soul Ore", "Spirit Ore", "Craftsman Mold", "Maestro Holder", "Blacksmith's Frame");

    public static List<List<String>> crossJoinResources(String template, List<ResourceGrade> resourceGrades) {
        CraftResourcesDropDataTable craftResourcesDropDataTable = CraftResourcesDropDataTable.getInstance();

        List<Iterator<CraftResource>> craftResourcesIterators = resourceGrades.stream()
                .map(resourceGrade -> craftResourcesDropDataTable.getResourceMap().get(resourceGrade))
                .peek(category -> category.removeIf(craftResource -> ignoreList.contains(craftResource.getName())))
                .map(category -> category.stream().sorted(Comparator.comparing(CraftResource::getPrice).reversed()).iterator())
                .collect(Collectors.toList());

        List<List<String>> allItemHolders = new ArrayList<>();

        while (craftResourcesIterators.stream().allMatch(Iterator::hasNext)) {
            List<String> itemHolders = new ArrayList<>();
            craftResourcesIterators.forEach(iterator -> {
                CraftResource craftResource = iterator.next();
                itemHolders.add(String.format(template, craftResource.getId(), ItemTable.getInstance().getTemplate(craftResource.getId()).getName(), 1000));
            });
            allItemHolders.add(itemHolders);
        }
        return allItemHolders;
    }

    public static void main(String[] args) {
        String template = "\t- id: %s # %s\n\t\tcount: %s";
        crossJoinResources(template, Arrays.asList(ResourceGrade.HIGHEST, ResourceGrade.HIGH, ResourceGrade.MID, ResourceGrade.LOW)).forEach(category -> {
            category.forEach(System.out::println);
            System.out.println();
        });
    }

}
