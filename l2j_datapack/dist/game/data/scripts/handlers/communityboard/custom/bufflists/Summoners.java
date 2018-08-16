package handlers.communityboard.custom.bufflists;

import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.Arrays;
import java.util.List;

public class Summoners {

    public static final List<SkillHolder> BUFFS = Arrays.asList(
            new SkillHolder(4700, 13), // Gift of Queen
            new SkillHolder(4699, 13), // Blessing of Queen
            new SkillHolder(4702, 13), // Blessing of Seraphim
            new SkillHolder(4703, 13) // Gift of Seraphim
    );

}
