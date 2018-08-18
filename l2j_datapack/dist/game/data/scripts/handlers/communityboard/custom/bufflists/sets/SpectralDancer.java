package handlers.communityboard.custom.bufflists.sets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SpectralDancer implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(311, 1), // Dance of Protection
            new SkillHolder(310, 1), // Dance of the Vampire
            new SkillHolder(309, 1), // Dance of Earth Guard
            new SkillHolder(307, 1), // Dance of Aqua Guard
            new SkillHolder(276, 1), // Dance of Concentration
            new SkillHolder(277, 1), // Dance of Light
            new SkillHolder(273, 1), // Dance of the Mystic
            new SkillHolder(275, 1), // Dance of Fury
            new SkillHolder(274, 1), // Dance of Fire
            new SkillHolder(271, 1), // Dance of the Warrior
            new SkillHolder(272, 1), // Dance of Inspiration
            new SkillHolder(530, 1), // Dance of Alignment
            new SkillHolder(915, 1), // Dance of Berserker
            new SkillHolder(365, 1) // Siren's Dance
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
