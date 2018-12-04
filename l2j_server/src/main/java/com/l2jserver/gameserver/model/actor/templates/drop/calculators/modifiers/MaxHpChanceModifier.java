package com.l2jserver.gameserver.model.actor.templates.drop.calculators.modifiers;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.EffectScope;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.model.stats.functions.FuncTemplate;

import java.util.Optional;

public class MaxHpChanceModifier {

    public double calculate(L2Character victim) {
        int hpChanceIncreaseCoefficient = 20;
        int maxHpSkillId = 4408;

        double rateMultiplier = 1D;
        if (victim.isRaid()) {
            return rateMultiplier;
        }

        Skill skill = victim.getKnownSkill(maxHpSkillId);
        if (skill == null) {
            return rateMultiplier;
        }

        Optional<FuncTemplate> maxHpTemplateOptional = skill.getEffectByStat(EffectScope.PASSIVE, Stats.MAX_HP);
        if (maxHpTemplateOptional.isPresent()) {
            double maxHpMultiplier = maxHpTemplateOptional.get().getValue();

            if (maxHpMultiplier >= 1D) {
                rateMultiplier = (maxHpMultiplier + hpChanceIncreaseCoefficient) / hpChanceIncreaseCoefficient;
            } else {
                rateMultiplier = maxHpMultiplier;
            }
        }
        return rateMultiplier;
    }

}
