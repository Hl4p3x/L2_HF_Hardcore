package com.l2jserver.gameserver.engines;

import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.conditions.ConditionTargetUsesWeaponKind;
import com.l2jserver.gameserver.model.conditions.ConditionUsingItemType;
import com.l2jserver.gameserver.model.items.type.ArmorType;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import java.util.StringTokenizer;
import java.util.function.Function;
import org.w3c.dom.Node;

public class ConditionParsingHelper {

    public static Condition parseTargetUsingKind(Node node, Condition condition) {
        return handleUsingKind(node.getNodeValue(), condition, ConditionTargetUsesWeaponKind::new);
    }

    public static Condition parseSelfUsingKind(Node node, Condition condition) {
        return handleUsingKind(node.getNodeValue(), condition, ConditionUsingItemType::new);
    }

    public static Condition handleUsingKind(String tokenString, Condition condition, Function<Integer, Condition> conditionProvider) {
        StringTokenizer st = new StringTokenizer(tokenString, ",");

        int mask = 0;
        while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            for (WeaponType wt : WeaponType.values()) {
                if (wt.name().equals(item)) {
                    mask |= wt.mask();
                    break;
                }
            }
            for (ArmorType at : ArmorType.values()) {
                if (at.name().equals(item)) {
                    mask |= at.mask();
                    break;
                }
            }
        }

        return LogicHelper.joinAnd(condition, conditionProvider.apply(mask));
    }
}
