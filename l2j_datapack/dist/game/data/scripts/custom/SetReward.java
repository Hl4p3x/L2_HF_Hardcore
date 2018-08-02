package custom;

import com.l2jserver.gameserver.data.xml.impl.ArmorSetsData;
import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.L2ArmorSet;
import com.l2jserver.gameserver.model.L2RecipeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetReward {

    private static final Logger LOG = LoggerFactory.getLogger(SetReward.class);

    private final int chestId;
    private final String setName;
    private final List<Reward> rewards;

    public SetReward(int chestId) {
        this.chestId = chestId;
        L2ArmorSet armorSet = ArmorSetsData.getInstance().getSet(chestId);
        if (armorSet == null) {
            LOG.warn("Could not find set reward for chest {}", chestId);
            rewards = Collections.emptyList();
            setName = "Unknown";
        } else {
            L2ArmorSet set = ArmorSetsData.getInstance().getSet(chestId);
            setName = set.getSetName();
            rewards = loadFromSet(set);
        }
    }

    private List<Reward> idsToSingularReward(List<Integer> ids) {
        return ids.stream().map(Reward::new).collect(Collectors.toList());
    }

    private List<Reward> loadFromSet(L2ArmorSet armorSet) {
        List<Reward> setReward = new ArrayList<>();
        setReward.add(new Reward(armorSet.getChestId()));
        setReward.addAll(idsToSingularReward(armorSet.getHead()));
        setReward.addAll(idsToSingularReward(armorSet.getGloves()));
        setReward.addAll(idsToSingularReward(armorSet.getLegs()));
        setReward.addAll(idsToSingularReward(armorSet.getFeet()));
        setReward.addAll(idsToSingularReward(armorSet.getShield()));

        return setReward.stream()
                .filter(reward -> {
                    L2RecipeList recipeList = RecipeData.getInstance().getRecipeByItemId(reward.getItemId());
                    if (recipeList == null) {
                        LOG.debug("Skipping uncraftable Item {}", ItemTable.getInstance().getTemplate(reward.getItemId()));
                        return false;
                    }

                    boolean result = RecipeData.getInstance().getRecipeByItemId(reward.getItemId()).getRareItemId() != reward.getItemId();
                    if (!result) {
                        LOG.debug("Skipping rate item {}", ItemTable.getInstance().getTemplate(reward.getItemId()));
                    }
                    return result;
                })
                .collect(Collectors.toList());
    }

    public List<Reward> getSetRewards() {
        if (rewards.isEmpty()) {
            LOG.warn("Trying to use an empty set reward {}", chestId);
        }
        return rewards;
    }

    @Override
    public String toString() {
        return String.format("Set %s [%s]", setName, chestId);
    }
}
