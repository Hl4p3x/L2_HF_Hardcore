package handlers.communityboard.custom.actions;

import com.l2jserver.Config;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.localization.Strings;
import com.l2jserver.util.StringUtil;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.bufflists.sets.AllBuffs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AddPresetBuffAction implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(AddPresetBuffAction.class);
    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty() || args.getArgs().size() != 3) {
            LOG.warn("Invalid add preset buff request from {} with args {}", player, args);
            return ProcessResult.failure(Strings.of(player).get("invalid_request"));
        }

        String categoryName = args.getArgs().get(0);
        String presetName = args.getArgs().get(1);
        String buffSkillIdString = args.getArgs().get(2);

        if (StringUtil.isBlank(categoryName)) {
            ProcessResult.failure(Strings.of(player).get("category_name_cannot_be_empty"));
        }

        if (StringUtil.isBlank(presetName)) {
            ProcessResult.failure(Strings.of(player).get("preset_name_cannot_be_empty"));
        }

        if (StringUtil.isBlank(buffSkillIdString)) {
            ProcessResult.failure(Strings.of(player).get("buff_id_cannot_be_empty"));
        }

        int buffSkillId = Integer.parseInt(buffSkillIdString);

        Optional<SkillHolder> requestedBuffOption = new AllBuffs().getBuffs().stream().filter(skillHolder -> skillHolder.getSkillId() == buffSkillId).findFirst();
        if (requestedBuffOption.isEmpty()) {
            LOG.warn("Player {} is trying to add an invalid buff {}", player, buffSkillId);
            return ProcessResult.success();
        }

        Optional<CommunityBuffList> buffList = DAOFactory.getInstance().getCommunityBuffListDao().findSingleCommunityBuffSet(player.getObjectId(), presetName);
        if (buffList.isEmpty()) {
            LOG.warn("Player {} is trying to add a buff to list that does not exist or does not belong to him {}", player, presetName);
            return ProcessResult.success();
        }

        if (buffList.get().getSkills().size() >= Config.MAX_CUSTOM_PRESET_BUFFS) {
            return ProcessResult.failure(Strings.of(player).get("you_have_reached_maximum_number_of_buffs_in_a_preset") + "!");
        }

        DAOFactory.getInstance().getCommunityBuffListDao().addToCommunityBuffList(buffList.get().getId(), requestedBuffOption.get());

        CommunityBoardHandler.getInstance().handleParseCommand("_bbs_buff list_add_to_preset_buff " + categoryName + " " + buffList.get().getName(), player);
        return ProcessResult.success();
    }
}
