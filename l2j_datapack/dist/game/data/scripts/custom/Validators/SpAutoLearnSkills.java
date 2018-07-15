package custom.Validators;

import ai.npc.AbstractNpcAI;
import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.helpers.SpAutoLearnSkillsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpAutoLearnSkills extends AbstractNpcAI {

    private static final Logger LOG = LoggerFactory.getLogger(SpAutoLearnSkills.class);

    public SpAutoLearnSkills() {
        super(SpAutoLearnSkills.class.getSimpleName(), "custom");
        setOnEnterWorld(Config.ALT_AUTO_LEARN_SKILLS_ON_SP);
        if (Config.ALT_AUTO_LEARN_SKILLS_ON_SP) {
            LOG.info("SP AutoLearn Skills loaded");
        }
    }

    @Override
    public String onEnterWorld(L2PcInstance player) {
        LOG.debug("Player {} on enter world SP learn", player);
        SpAutoLearnSkillsHelper.tryAutoLearnNextSkill(player);
        return super.onEnterWorld(player);
    }

    public static void main(String[] args) {
        new SpAutoLearnSkills();
    }

}
