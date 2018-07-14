package com.l2jserver.gameserver.model.actor.instance.helpers;

import com.l2jserver.Config;
import com.l2jserver.gameserver.data.xml.impl.ClassListData;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.AcquireSkillDone;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpAutoLearnSkillsHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SpAutoLearnSkillsHelper.class);

    private static final String LOG_MARKER = "[SP Skill AutoLearn]";

    public static boolean tryAutoLearnNextSkill(L2PcInstance pcInstance) {
        if (!Config.ALT_AUTO_LEARN_SKILLS_ON_SP) {
            return false;
        }

        ClassInfo classInfo = ClassListData.getInstance().getClass(pcInstance.getActiveClass());
        if (classInfo == null) {
            LOG.warn("{} Instance {} is trying to auto learn new skill for unknown class {}", LOG_MARKER, pcInstance,
                pcInstance.getActiveClass());
            return false;
        }

        Comparator<L2SkillLearn> comparatorByLevel = Comparator.comparing(L2SkillLearn::getGetLevel);
        Comparator<L2SkillLearn> comparatorByLevelAndSp = comparatorByLevel.thenComparing(L2SkillLearn::getLevelUpSp);

        List<L2SkillLearn> skillLearns = SkillTreesData.getInstance()
            .getAvailableSkills(pcInstance, classInfo.getClassId(), false, false);

        Optional<L2SkillLearn> affordableSkillLearnsSorted = skillLearns.stream()
            .filter(skill -> skill.getGetLevel() <= pcInstance.getLevel())
            .filter(skill -> skill.getLevelUpSp() < pcInstance.getSp())
            .filter(skill -> skill.getRequiredItems().isEmpty())
            .filter(skill -> skill.getPreReqSkills().isEmpty())
            .min(comparatorByLevelAndSp);

        if (affordableSkillLearnsSorted.isPresent()) {
            L2SkillLearn skillLearn = affordableSkillLearnsSorted.get();

            final Skill skill = SkillData.getInstance().getSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel());
            if (skill == null) {
                LOG.warn("{} Could not find corresponding skill for characters {} skill learn {} ", LOG_MARKER,
                    pcInstance, skillLearn);
                return false;
            }

            int levelUpSp = skillLearn.getLevelUpSp();
            pcInstance.setSp(pcInstance.getSp() - levelUpSp);
            final StatusUpdate su = new StatusUpdate(pcInstance);
            su.addAttribute(StatusUpdate.SP, pcInstance.getSp());
            pcInstance.sendPacket(su);

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.LEARNED_SKILL_S1);
            sm.addSkillName(skill);
            pcInstance.sendPacket(sm);

            pcInstance.sendPacket(new AcquireSkillDone());
            pcInstance.addSkill(skill, true);
            pcInstance.sendSkillList();
            pcInstance.updateShortCuts(skill.getId(), skill.getLevel());

            return true;
        } else {
            LOG.debug("{} There is no new skill to learn for {}", LOG_MARKER, pcInstance);
            return false;
        }
    }

}
