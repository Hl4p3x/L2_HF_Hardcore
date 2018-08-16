package handlers.communityboard.custom.bufflists;

import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.Arrays;
import java.util.List;

public class SwordMuse implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(308, 1), // Song of Storm Guard
            new SkillHolder(305, 1), // Song of Vengeance
            new SkillHolder(306, 1), // Song of Flame Guard
            new SkillHolder(304, 1), // Song of Vitality
            new SkillHolder(270, 1), // Song of Invocation
            new SkillHolder(268, 1), // Song of Wind
            new SkillHolder(269, 1), // Song of Hunter
            new SkillHolder(265, 1), // Song of Life
            new SkillHolder(267, 1), // Song of Warding
            new SkillHolder(266, 1), // Song of Water
            new SkillHolder(264, 1), // Song of Earth
            new SkillHolder(914, 1), // Song of Purification
            new SkillHolder(363, 1), // Song of Meditation
            new SkillHolder(764, 1), // Song of Wind Storm
            new SkillHolder(529, 1), // Song of Elemental
            new SkillHolder(349, 1), // Song of Renewal
            new SkillHolder(364, 1)  // Song of Champion
    );

    @Override
    public List<SkillHolder> getBuffs() {
        return buffs;
    }

}
