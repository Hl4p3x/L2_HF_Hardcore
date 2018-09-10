package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.datatables.categorized.CategorizedDataTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.List;
import java.util.Set;

public class DynamicDropCalculator {

    public List<ItemHolder> calculate(L2Character victim, L2Character killer) {
        return null;
    }

    public Set<Integer> getAllDynamicItemsIds() {
        return CategorizedDataTable.getInstance().getCategorizedItems().getAllIds();
    }

    public static DynamicDropCalculator getInstance() {
        return DynamicDropCalculator.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final DynamicDropCalculator INSTANCE = new DynamicDropCalculator();
    }

}
