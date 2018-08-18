package handlers.communityboard.custom.bufflists.sets.presets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MageBuff implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(1413, 1), // Magnus' Chant
            new SkillHolder(1078, 6), // Concentration
            new SkillHolder(1035, 4), // Mental Shield
            new SkillHolder(1303, 2), // Wild Magic
            new SkillHolder(1259, 4), // Resist Shock
            new SkillHolder(1503, 1), // Improved Shield Defense
            new SkillHolder(1504, 1), // Improved Movement
            new SkillHolder(1397, 3), // Clarity
            new SkillHolder(1460, 1), // Mana Gain
            new SkillHolder(1354, 1), // Arcane Protection
            new SkillHolder(1085, 3), // Acumen
            new SkillHolder(1062, 2), // Berserker Spirit
            new SkillHolder(1499, 1), // Improved Combat
            new SkillHolder(1501, 1), // Improved Condition
            new SkillHolder(1389, 3), // Greater Shield
            new SkillHolder(1542, 1), // Counter Critical
            new SkillHolder(1500, 1) // Improved Magic
    );

    @Override
    public List<SkillHolder> getBuffs() {
        return buffs;
    }

    @Override
    public Optional<SkillHolder> findBySkillId(int skillId) {
        return BuffFilter.findBySkillId(buffs, skillId);
    }
}
