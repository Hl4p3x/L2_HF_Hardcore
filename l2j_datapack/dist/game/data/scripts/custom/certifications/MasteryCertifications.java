package custom.certifications;

import ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.AcquireSkillType;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.AcquireSkillList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MasteryCertifications extends AbstractNpcAI {

    private static final Logger LOG = LoggerFactory.getLogger(MasteryCertifications.class);

    private static final int[] NPCS =
            {
                    30026, 30031, 30037, 30066, 30070, 30109, 30115, 30120, 30154, 30174,
                    30175, 30176, 30187, 30191, 30195, 30288, 30289, 30290, 30297, 30358,
                    30373, 30462, 30474, 30498, 30499, 30500, 30503, 30504, 30505, 30508,
                    30511, 30512, 30513, 30520, 30525, 30565, 30594, 30595, 30676, 30677,
                    30681, 30685, 30687, 30689, 30694, 30699, 30704, 30845, 30847, 30849,
                    30854, 30857, 30862, 30865, 30894, 30897, 30900, 30905, 30910, 30913,
                    31269, 31272, 31276, 31279, 31285, 31288, 31314, 31317, 31321, 31324,
                    31326, 31328, 31331, 31334, 31336, 31755, 31958, 31961, 31965, 31968,
                    31974, 31977, 31996, 32092, 32093, 32094, 32095, 32096, 32097, 32098,
                    32145, 32146, 32147, 32150, 32153, 32154, 32157, 32158, 32160, 32171,
                    32193, 32199, 32202, 32213, 32214, 32221, 32222, 32229, 32230, 32233,
                    32234
            };


    public MasteryCertifications() {
        super(MasteryCertifications.class.getSimpleName(), "Mastery Certifications");
        addStartNpc(NPCS);
        addTalkId(NPCS);
        addAcquireSkillId(NPCS);
    }

    @Override
    public String onTalk(L2Npc npc, L2PcInstance player) {
        return HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/scripts/custom/certifications/mastery_certification.html");
    }

    @Override
    public String onAcquireSkill(L2Npc npc, L2PcInstance player, Skill skill, AcquireSkillType type) {
        listCertifications(player);
        return null;
    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
        switch (event) {
            case "certification_list":
                listCertifications(player);
                break;
            case "certification_drop_all_from_current_class":
                dropAllCertificationsFromCurrentClass(player);
                return HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/scripts/custom/certifications/mastery_certification_removed.html");
            case "certification_info":
                return HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/scripts/custom/certifications/mastery_certification.html");
        }
        return null;
    }

    public void dropAllCertificationsFromCurrentClass(L2PcInstance player) {
        SkillTreesData.getInstance().getClassMasterySkillIds().forEach(skillId -> {
            Skill skill = player.getKnownSkill(skillId);
            if (skill != null) {
                player.removeSkill(skill, true);
            }
        });
        player.sendSkillList();
    }

    public void listCertifications(L2PcInstance player) {
        final List<L2SkillLearn> subClassSkills = SkillTreesData.getInstance().getAvailableMasterClassSkills(player);
        final AcquireSkillList asl = new AcquireSkillList(AcquireSkillType.MASTERY);
        int count = 0;

        for (L2SkillLearn s : subClassSkills) {
            if (SkillData.getInstance().getSkill(s.getSkillId(), s.getSkillLevel()) != null) {
                count++;
                asl.addSkill(s.getSkillId(), s.getSkillLevel(), s.getSkillLevel(), 0, 0);
            }
        }
        if (count > 0) {
            player.sendPacket(asl);
        } else {
            player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
        }
    }

    public static void main(String[] args) {
        new MasteryCertifications();
    }

}
