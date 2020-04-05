package handlers.communityboard.custom.actions;

import com.l2jserver.common.util.StringUtil;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.bufflists.sets.AllBuffs;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemovePresetBuffAction implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(RemovePresetBuffAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty() || args.getArgs().size() != 2) {
            LOG.warn("Invalid remove preset buff request from {} with args {}", player, args);
            ProcessResult.failure(Strings.of(player).get("invalid_remove_preset_buff_request"));
        }

        String presetIdString = args.getArgs().get(0);
        String buffSkillIdString = args.getArgs().get(1);

        if (StringUtil.isBlank(presetIdString)) {
            ProcessResult.failure(Strings.of(player).get("preset_id_cannot_be_empty"));
        }

        if (StringUtil.isBlank(buffSkillIdString)) {
            ProcessResult.failure(Strings.of(player).get("buff_id_cannot_be_empty"));
        }

        int presetId = Integer.parseInt(presetIdString);
        int buffSkillId = Integer.parseInt(buffSkillIdString);

        Optional<SkillHolder> requestedBuffOption = new AllBuffs().getBuffs().stream().filter(skillHolder -> skillHolder.getSkillId() == buffSkillId).findFirst();
        if (requestedBuffOption.isEmpty()) {
            LOG.warn("Player {} is trying to remove an invalid buff {}", player, buffSkillId);
            return ProcessResult.success();
        }

        Optional<CommunityBuffList> buffList = DAOFactory.getInstance().getCommunityBuffListDao().findPlayerBuffListById(presetId, player.getObjectId());
        if (buffList.isEmpty()) {
            LOG.warn("Player {} is trying to remove a buff from list that does not belong to him {}", player, presetId);
            return ProcessResult.success();
        }

        DAOFactory.getInstance().getCommunityBuffListDao().removeFromCommunityBuffList(presetId, buffSkillId);

        CommunityBoardHandler.getInstance().handleParseCommand("_bbs_buff update_preset " + buffList.get().getName(), player);
        return ProcessResult.success();
    }
}
