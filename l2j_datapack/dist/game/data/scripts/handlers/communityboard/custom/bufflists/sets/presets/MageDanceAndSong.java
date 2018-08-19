package handlers.communityboard.custom.bufflists.sets.presets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MageDanceAndSong implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(276, 1), // Dance of Concentration
            new SkillHolder(273, 1), // Dance of the Mystic
            new SkillHolder(915, 1), // Dance of Berserker
            new SkillHolder(365, 1), // Siren's Dance
            new SkillHolder(304, 1), // Song of Vitality
            new SkillHolder(268, 1), // Song of Wind
            new SkillHolder(267, 1), // Song of Warding
            new SkillHolder(264, 1), // Song of Earth
            new SkillHolder(349, 1) // Song of Renewal
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

