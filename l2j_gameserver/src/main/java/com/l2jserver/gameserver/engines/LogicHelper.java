package com.l2jserver.gameserver.engines;

import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.conditions.ConditionLogicAnd;

public class LogicHelper {

    public static Condition joinAnd(Condition cond, Condition c) {
        if (cond == null) {
            return c;
        }
        if (cond instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd) cond).add(c);
            return cond;
        }
        ConditionLogicAnd and = new ConditionLogicAnd();
        and.add(cond);
        and.add(c);
        return and;
    }

}
