package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.util.StringUtil;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.renderers.BuffCategoriesRender;
import handlers.communityboard.custom.renderers.BuffRowRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpdatePresetAction implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(UpdatePresetAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty()) {
            LOG.warn("Invalid update preset request from {} with args {}", player, args);
            ProcessResult.failure("Invalid update preset request");
        }

        String presetName = args.getArgs().get(0);
        if (StringUtil.isBlank(presetName) || StringUtil.hasWhitespaces(presetName)) {
            ProcessResult.failure("Preset name cannot be empty or contain whitespaces");
        }


        Optional<CommunityBuffList> communityBuffListOption = DAOFactory.getInstance()
                    .getCommunityBuffListDao()
                    .findSingleCommunityBuffSet(player.getObjectId(), presetName);
        if (!communityBuffListOption.isPresent()) {
            LOG.warn("Player {} is trying to retrieve preset [{}] that does not belong to him", player, presetName);
            return ProcessResult.failure("Error occurred, could not retrieve buff preset");
        }

        CommunityBuffList communityBuffList = communityBuffListOption.get();

        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff_preset_editor.html");

        html = html.replace("%buff_categories%", BuffCategoriesRender.renderBuffCategoriesList("list_add_to_preset_buff ", String.valueOf(communityBuffList.getName()), player));
        html = html.replace("%current_preset_buff_count%", String.valueOf(communityBuffList.getSkills().size()));

        List<Skill> presetBuffs = communityBuffList.getSkills().stream().map(SkillHolder::getSkill).collect(Collectors.toList());
        String buffRows = BuffRowRender.render("bypass -h _bbs_buff remove_preset_buff " + communityBuffList.getId() + " %buff_id%", presetBuffs);
        html = html.replace("%current_preset_buff_list%", buffRows);

        CommunityBoardHandler.separateAndSend(html, player);
        return ProcessResult.success();
    }

}
