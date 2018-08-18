package handlers.communityboard.custom.bufflists.sets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Hierophant implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(1040, 3), // Shield
            new SkillHolder(1068, 3), // Might
            new SkillHolder(1191, 3), // Resist Fire
            new SkillHolder(1078, 6), // Concentration
            new SkillHolder(1085, 3), // Acumen
            new SkillHolder(1077, 3), // Focus
            new SkillHolder(1073, 2), // Kiss of Eva
            new SkillHolder(1062, 2), // Berserker Spirit
            new SkillHolder(1044, 3), // Regeneration
            new SkillHolder(1043, 1), // Holy Weapon
            new SkillHolder(1035, 4), // Mental Shield
            new SkillHolder(1204, 2), // Wind Walk
            new SkillHolder(1189, 3), // Resist Wind
            new SkillHolder(1182, 3), // Resist Aqua
            new SkillHolder(1499, 1), // Improved Combat
            new SkillHolder(1501, 1), // Improved Condition
            new SkillHolder(1086, 2), // Haste
            new SkillHolder(1242, 3), // Death Whisper
            new SkillHolder(1243, 6), // Bless Shield
            new SkillHolder(1240, 3), // Guidance
            new SkillHolder(1392, 3), // Holy Resistance
            new SkillHolder(1393, 3), // Unholy Resistance
            new SkillHolder(1388, 3), // Greater Might
            new SkillHolder(1048, 6), // Blessed Soul
            new SkillHolder(1389, 3), // Greater Shield
            new SkillHolder(1548, 3), // Resist Earth
            new SkillHolder(1036, 2), // Magic Barrier
            new SkillHolder(1045, 6), // Blessed Body
            new SkillHolder(1033, 3), // Resist Poison
            new SkillHolder(1032, 3), // Invigor
            new SkillHolder(329, 1), // Health
            new SkillHolder(1356, 1), // Prophecy of Fire
            new SkillHolder(1542, 1), // Counter Critical
            new SkillHolder(1352, 1) // Elemental Protection
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
