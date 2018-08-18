package handlers.communityboard.custom.bufflists.sets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ShillenSaint implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(1040, 3), // Shield
            new SkillHolder(1068, 3), // Might
            new SkillHolder(1189, 3), // Resist Wind
            new SkillHolder(1268, 4), // Vampiric Rage
            new SkillHolder(1078, 6), // Concentration
            new SkillHolder(1077, 3), // Focus
            new SkillHolder(1073, 2), // Kiss of Eva
            new SkillHolder(1059, 3), // Empower
            new SkillHolder(1035, 4), // Mental Shield
            new SkillHolder(1204, 2), // Wind Walk
            new SkillHolder(1502, 1), // Improved Critical Attack
            new SkillHolder(1500, 1), // Improved Magic
            new SkillHolder(1303, 2), // Wild Magic
            new SkillHolder(1242, 3), // Death Whisper
            new SkillHolder(1240, 3), // Guidance
            new SkillHolder(1392, 3), // Holy Resistance
            new SkillHolder(1357, 1), // Prophecy of Wind
            new SkillHolder(1460, 1), // Mana Gain
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
