package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.localization.Strings;
import com.l2jserver.util.StringUtil;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLIntegrityConstraintViolationException;

public class CreatePresetAction implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(CreatePresetAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty()) {
            LOG.warn("Invalid create preset request from {} with args {}", player, args);
            return ProcessResult.failure(Strings.of(player).get("invalid_preset_buff_request"));
        }
        String presetName = args.getArgs().get(0);
        if (StringUtil.isBlank(presetName) || StringUtil.hasWhitespaces(presetName)) {
            ProcessResult.failure(Strings.of(player).get("preset_name_cannot_be_empty_or_contain_whitespaces"));
        }

        CommunityBuffList communityBuffList = new CommunityBuffList(player.getObjectId(), presetName);
        try {
            DAOFactory.getInstance().getCommunityBuffListDao().createCommunityBuffList(communityBuffList);
        } catch (UnableToExecuteStatementException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                return ProcessResult.failure(Strings.of(player).get("you_already_have_a_preset_named_n").replace("$n", presetName));
            } else {
                return ProcessResult.failure(Strings.of(player).get("error_occurred_could_not_retrieve_buff_preset"));
            }
        }
        CommunityBoardHandler.getInstance().handleParseCommand("_bbs_buff update_preset " + presetName, player);
        return ProcessResult.success();
    }

}
