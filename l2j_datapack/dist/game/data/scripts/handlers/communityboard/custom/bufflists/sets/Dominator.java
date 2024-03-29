package handlers.communityboard.custom.bufflists.sets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Dominator implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(1005, 3), // Blessings of Pa'agrio
            new SkillHolder(1003, 3), // Pa'agrian Gift
            new SkillHolder(1004, 3), // The Wisdom of Pa'agrio
            new SkillHolder(1008, 3), // The Glory of Pa'agrio
            new SkillHolder(1282, 2), // Pa'agrian Haste
            new SkillHolder(1261, 2), // The Rage of Pa'agrio
            new SkillHolder(1260, 3), // The Tact of Pa'agrio
            new SkillHolder(1250, 3), // Under the Protection of Pa'agrio
            new SkillHolder(1249, 3), // The Vision of Pa'agrio
            new SkillHolder(1563, 2), // Fury of Pa'agrio
            new SkillHolder(1537, 1), // Critical of Pa'agrio
            new SkillHolder(1538, 1), // Condition of Pa'agrio
            new SkillHolder(1536, 1), // Combat of Pa'agrio
            new SkillHolder(1364, 1), // Eye of Pa'agrio
            new SkillHolder(1365, 1), // Soul of Pa'agrio
            new SkillHolder(1414, 1), // Victory of Pa'agrio
            new SkillHolder(1415, 1)  // Pa'agrio's Emblem
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
