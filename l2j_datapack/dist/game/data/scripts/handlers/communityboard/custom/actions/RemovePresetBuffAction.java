package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.util.StringUtil;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.bufflists.sets.AllBuffs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RemovePresetBuffAction implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(RemovePresetBuffAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty() || args.getArgs().size() != 2) {
            LOG.warn("Invalid remove preset buff request from {} with args {}", player, args);
            ProcessResult.failure("Invalid remove preset buff request");
        }

        String presetIdString = args.getArgs().get(0);
        String buffSkillIdString = args.getArgs().get(1);

        if (StringUtil.isBlank(presetIdString)) {
            ProcessResult.failure("Preset id cannot be empty");
        }

        if (StringUtil.isBlank(buffSkillIdString)) {
            ProcessResult.failure("Buff id cannot be empty");
        }

        int presetId = Integer.parseInt(presetIdString);
        int buffSkillId = Integer.parseInt(buffSkillIdString);

        Optional<SkillHolder> requestedBuffOption = new AllBuffs().getBuffs().stream().filter(skillHolder -> skillHolder.getSkillId() == buffSkillId).findFirst();
        if (!requestedBuffOption.isPresent()) {
            LOG.warn("Player {} is trying to remove an invalid buff {}", player, buffSkillId);
            return ProcessResult.success();
        }

        Optional<CommunityBuffList> buffList = DAOFactory.getInstance().getCommunityBuffListDao().findPlayerBuffListById(presetId, player.getObjectId());
        if (!buffList.isPresent()) {
            LOG.warn("Player {} is trying to remove a buff from list that does not belong to him {}", player, presetId);
            return ProcessResult.success();
        }

        DAOFactory.getInstance().getCommunityBuffListDao().removeFromCommunityBuffList(presetId, buffSkillId);

        CommunityBoardHandler.getInstance().handleParseCommand("_bbs_buff update_preset " + buffList.get().getName(), player);
        return ProcessResult.success();
    }
}
