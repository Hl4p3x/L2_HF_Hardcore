package custom.helper;

import ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.data.xml.impl.ClassListData;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.skills.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListSkills extends AbstractNpcAI {

    private static final Logger LOG = LoggerFactory.getLogger(ListSkills.class);

    private ListSkills() {
        super(ListSkills.class.getSimpleName(), "custom/helper");

        List<ClassId> classes = Arrays.asList(
                ClassId.hierophant, ClassId.maestro, ClassId.arcanaLord,
                ClassId.elementalMaster, ClassId.spectralMaster,
                ClassId.shillienSaint, ClassId.evaSaint, ClassId.doomcryer,
                ClassId.dominator, ClassId.spectralDancer, ClassId.swordMuse
        );

        classes.forEach(classId -> {
            Collection<L2SkillLearn> classSkills = SkillTreesData.getInstance().getCompleteClassSkillTree(classId).values();

            LOG.info("[SkillInfo] Class {}", ClassListData.getInstance().getClass(classId.getId()).getClassName());
            classSkills.stream().map(L2SkillLearn::getSkillId).distinct().forEach(classSkillId -> {
                SkillData skillData = SkillData.getInstance();
                Skill skill = skillData.getSkill(classSkillId, skillData.getMaxLevel(classSkillId));
                if (skill != null) {
                    LOG.info("[SkillInfo] new Skill({}, {}), // {}", skill.getId(), skill.getLevel(), skill.getName());
                }
            });
        });

    }

    public static void main(String[] args) {
        new ListSkills();
    }

}
