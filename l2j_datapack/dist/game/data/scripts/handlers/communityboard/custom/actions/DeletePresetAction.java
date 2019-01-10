package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletePresetAction implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(DeletePresetAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty() || args.getArgs().size() != 1) {
            LOG.warn("{} is trying to delete buff preset with invalid args number {}", player, args.getArgs().size());
            return ProcessResult.failure(Strings.of(player).get("invalid_preset_buff_request"));
        }

        try {
            DAOFactory.getInstance().getCommunityBuffListDao().removeCommunityBuffList(player.getObjectId(), args.getArgs().get(0));
        } catch (RuntimeException e) {
            LOG.warn(player + " could not delete his preset " + args.getArgs().get(0), e);
            ProcessResult.failure(Strings.of(player).get("could_not_delete_preset_n").replace("$n", args.getArgs().get(0)));
        }

        // Redirect to home buff list, to update it
        CommunityBoardHandler.getInstance().handleParseCommand("_bbshome", player);
        return ProcessResult.success();
    }

}
