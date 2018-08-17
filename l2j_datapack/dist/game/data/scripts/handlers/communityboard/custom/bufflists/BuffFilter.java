package handlers.communityboard.custom.bufflists;

import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.List;
import java.util.Optional;

public class BuffFilter {

    public static Optional<SkillHolder> findBySkillId(List<SkillHolder> buffs, int skillId) {
        return buffs.stream().filter(skillHolder -> skillHolder.getSkillId() == skillId).findFirst();
    }

}
