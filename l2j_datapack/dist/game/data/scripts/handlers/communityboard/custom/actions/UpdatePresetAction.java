package handlers.communityboard.custom.actions;

import com.l2jserver.common.util.StringUtil;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.renderers.BuffCategoriesRender;
import handlers.communityboard.custom.renderers.BuffRowRender;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatePresetAction implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(UpdatePresetAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty()) {
            LOG.warn("Invalid update preset request from {} with args {}", player, args);
            ProcessResult.failure(Strings.of(player).get("invalid_update_preset_request"));
        }

        String presetName = args.getArgs().get(0);
        if (StringUtil.isBlank(presetName) || StringUtil.hasWhitespaces(presetName)) {
            ProcessResult.failure(Strings.of(player).get("preset_name_cannot_be_empty_or_contain_whitespaces"));
        }


        Optional<CommunityBuffList> communityBuffListOption = DAOFactory.getInstance()
                    .getCommunityBuffListDao()
                    .findSingleCommunityBuffSet(player.getObjectId(), presetName);
        if (communityBuffListOption.isEmpty()) {
            LOG.warn("Player {} is trying to retrieve preset [{}] that does not exist or does not belong to him", player, presetName);
            return ProcessResult.failure(Strings.of(player).get("error_occurred_could_not_retrieve_buff_preset"));
        }

        CommunityBuffList communityBuffList = communityBuffListOption.get();

        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/buff_preset_editor.html");

        html = html.replace("%buff_categories%", BuffCategoriesRender.renderBuffCategoriesList("list_add_to_preset_buff ", String.valueOf(communityBuffList.getName()), player));
        html = html.replace("%current_preset_buff_count%", String.valueOf(communityBuffList.getSkills().size()));

        List<Skill> presetBuffs = communityBuffList.getSkills().stream().map(SkillHolder::getSkill).collect(Collectors.toList());
        String buffRows = BuffRowRender.render("bypass -h _bbs_buff remove_preset_buff " + communityBuffList.getId() + " %buff_id%", presetBuffs);
        html = html.replace("%current_preset_buff_list%", buffRows);
        html = html.replace("%preset_name%", presetName);

        CommunityBoardHandler.separateAndSend(html, player);
        return ProcessResult.success();
    }

}
