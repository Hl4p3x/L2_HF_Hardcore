package handlers.communityboard.custom.bufflists;

import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ElvenSaint implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(1040, 3), // Shield
            new SkillHolder(1068, 3), // Might
            new SkillHolder(1078, 6), // Concentration
            new SkillHolder(1087, 3), // Agility
            new SkillHolder(1257, 3), // Decrease Weight
            new SkillHolder(1073, 2), // Kiss of Eva
            new SkillHolder(1044, 3), // Regeneration
            new SkillHolder(1043, 1), // Holy Weapon
            new SkillHolder(1035, 4), // Mental Shield
            new SkillHolder(1204, 2), // Wind Walk
            new SkillHolder(1033, 3), // Resist Poison
            new SkillHolder(1303, 2), // Wild Magic
            new SkillHolder(1304, 3), // Advanced Block
            new SkillHolder(1259, 4), // Resist Shock
            new SkillHolder(1243, 6), // Bless Shield
            new SkillHolder(1503, 1), // Improved Shield Defense
            new SkillHolder(1504, 1), // Improved Movement
            new SkillHolder(1393, 3), // Unholy Resistance
            new SkillHolder(1397, 3), // Clarity
            new SkillHolder(1353, 1), // Divine Protection
            new SkillHolder(1460, 1), // Mana Gain
            new SkillHolder(1355, 1), // Prophecy of Water
            new SkillHolder(1354, 1) // Arcane Protection
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
