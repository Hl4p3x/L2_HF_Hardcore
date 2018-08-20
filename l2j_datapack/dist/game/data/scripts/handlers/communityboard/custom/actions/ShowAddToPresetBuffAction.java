package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.bufflists.BuffList;
import handlers.communityboard.custom.renderers.BuffRowRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ShowAddToPresetBuffAction implements BoardAction {

    private final static Logger LOG = LoggerFactory.getLogger(ShowAddToPresetBuffAction.class);

    private final BuffList buffList;

    public ShowAddToPresetBuffAction(BuffList buffList) {
        this.buffList = buffList;
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty()) {
            LOG.warn("Could not ");
            return ProcessResult.failure("Could not show available preset buff list");
        }
        String presetName = args.getArgs().get(0);

        Optional<CommunityBuffList> communityBuffList = DAOFactory.getInstance().getCommunityBuffListDao().findSingleCommunityBuffSet(player.getObjectId(), presetName);
        if (!communityBuffList.isPresent()) {
            LOG.warn("Player {} tried to access list {} that does not belong to him", player, presetName);
            return ProcessResult.failure("Could not show available preset buff list");
        }

        Set<SkillHolder> currentPresetBuffs = new HashSet<>(communityBuffList.get().getSkills());

        List<Skill> buffSkills = buffList
                .getBuffs()
                .stream()
                .filter(skillHolder -> !currentPresetBuffs.contains(skillHolder))
                .map(SkillHolder::getSkill)
                .collect(Collectors.toList());

        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff_add_list.html");

        html = html.replace("%preset_name%", presetName);

        String buffRows = BuffRowRender.render("bypass -h _bbs_buff add_preset_buff " + args.getActionName() + " " + presetName + " %buff_id%", buffSkills);
        html = html.replace("%buff_list%", buffRows);

        CommunityBoardHandler.separateAndSend(html, player);
        return ProcessResult.success();
    }

}
