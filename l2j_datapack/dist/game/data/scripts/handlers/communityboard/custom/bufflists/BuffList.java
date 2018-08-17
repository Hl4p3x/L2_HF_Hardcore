package handlers.communityboard.custom.bufflists;

import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.List;
import java.util.Optional;

public interface BuffList {

    List<SkillHolder> getBuffs();

    Optional<SkillHolder> findBySkillId(int skillId);

}
