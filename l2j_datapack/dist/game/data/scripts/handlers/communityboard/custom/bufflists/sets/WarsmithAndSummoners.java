package handlers.communityboard.custom.bufflists.sets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WarsmithAndSummoners implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(828, 1), // Case Harden
            new SkillHolder(829, 1), // Hard Tanning
            new SkillHolder(830, 1), // Embroider
            new SkillHolder(825, 1), // Sharp Edge
            new SkillHolder(827, 1), // Restring
            new SkillHolder(826, 1), // Spike
            new SkillHolder(4700, 13), // Gift of Queen
            new SkillHolder(4699, 13), // Blessing of Queen
            new SkillHolder(4702, 13), // Blessing of Seraphim
            new SkillHolder(4703, 13) // Gift of Seraphim
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
