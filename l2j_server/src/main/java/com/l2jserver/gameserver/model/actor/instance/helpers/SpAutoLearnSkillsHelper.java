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
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SpAutoLearnSkillsHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SpAutoLearnSkillsHelper.class);

    private static final String LOG_MARKER = "[SP Skill AutoLearn]";
    private static final boolean LEVEL_UP_ON_LEARN = false;

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
        Comparator<L2SkillLearn> comparatorByLevelAndSkillLevel = comparatorByLevel
            .thenComparing(L2SkillLearn::getSkillLevel);

        List<L2SkillLearn> skillLearns = SkillTreesData.getInstance()
            .getAllAvailableNonFsSkillsWithAllLevels(pcInstance, classInfo.getClassId());

        Iterator<L2SkillLearn> affordableSkillLearnsSorted = skillLearns.stream()
            .filter(skill -> skill.getGetLevel() <= pcInstance.getLevel())
            .filter(skill -> skill.getRequiredItems().isEmpty())
            .filter(skill -> skill.getPreReqSkills().isEmpty())
            .sorted(comparatorByLevelAndSkillLevel)
            .iterator();

        if (!affordableSkillLearnsSorted.hasNext()) {
            LOG.debug("{} There is no new skill to learn for {}", LOG_MARKER, pcInstance);
            return false;
        }

        List<Skill> addedSkills = new ArrayList<>();
        while (affordableSkillLearnsSorted.hasNext()) {
            L2SkillLearn skillLearn = affordableSkillLearnsSorted.next();
            if (skillLearn.getLevelUpSp() >= pcInstance.getSp()) {
                break;
            }

            final Skill skill = SkillData.getInstance().getSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel());
            if (skill == null) {
                LOG.warn("{} Could not find corresponding skill for characters {} skill learn {} ", LOG_MARKER,
                    pcInstance, skillLearn);
                return false;
            }

            int levelUpSp = skillLearn.getLevelUpSp();
            pcInstance.setSp(pcInstance.getSp() - levelUpSp);
            pcInstance.addSkill(skill, true);
            addedSkills.add(skill);
        }

        addedSkills.forEach(skill -> {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.LEARNED_SKILL_S1);
            sm.addSkillName(skill);
            pcInstance.sendPacket(sm);
            pcInstance.updateShortCuts(skill.getId(), skill.getLevel());
        });

        final StatusUpdate su = new StatusUpdate(pcInstance);
        su.addAttribute(StatusUpdate.SP, pcInstance.getSp());
        pcInstance.sendPacket(su);

        pcInstance.sendPacket(new AcquireSkillDone());
        pcInstance.sendSkillList();

        if (LEVEL_UP_ON_LEARN && !addedSkills.isEmpty()) {
            pcInstance.broadcastPacket(new SocialAction(pcInstance.getObjectId(), SocialAction.LEVEL_UP));
        }

        return true;
    }

}
