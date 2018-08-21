package handlers.communityboard.custom.bufflists.sets.presets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FighterDanceAndSong implements BuffList {
    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(310, 1), // Dance of the Vampire
            new SkillHolder(275, 1), // Dance of Fury
            new SkillHolder(274, 1), // Dance of Fire
            new SkillHolder(271, 1), // Dance of the Warrior
            new SkillHolder(915, 1), // Dance of Berserker
            new SkillHolder(304, 1), // Song of Vitality
            new SkillHolder(268, 1), // Song of Wind
            new SkillHolder(269, 1), // Song of Hunter
            new SkillHolder(267, 1), // Song of Warding
            new SkillHolder(264, 1), // Song of Earth
            new SkillHolder(349, 1), // Song of Renewal
            new SkillHolder(364, 1)  // Song of Champion
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
