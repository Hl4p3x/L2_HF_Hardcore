package handlers.communityboard.custom.bufflists;

import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.Optional;

public interface BuffList extends Buffs {

    Optional<SkillHolder> findBySkillId(int skillId);

}
