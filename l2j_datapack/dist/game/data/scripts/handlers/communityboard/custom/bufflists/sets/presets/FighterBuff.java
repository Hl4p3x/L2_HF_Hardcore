package handlers.communityboard.custom.bufflists.sets.presets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FighterBuff implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(1519, 1), // Chant of Blood Awakening
            new SkillHolder(1363, 1), // Chant of Victory
            new SkillHolder(1035, 4), // Mental Shield
            new SkillHolder(1259, 4), // Resist Shock
            new SkillHolder(1504, 1), // Improved Movement
            new SkillHolder(1397, 3), // Clarity
            new SkillHolder(1354, 1), // Arcane Protection
            new SkillHolder(1062, 2), // Berserker Spirit
            new SkillHolder(1499, 1), // Improved Combat
            new SkillHolder(1501, 1), // Improved Condition
            new SkillHolder(1388, 3), // Greater Might
            new SkillHolder(1542, 1), // Counter Critical
            new SkillHolder(1502, 1), // Improved Critical Attack
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
