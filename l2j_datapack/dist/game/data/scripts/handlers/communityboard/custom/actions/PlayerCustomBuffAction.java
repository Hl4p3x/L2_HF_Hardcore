package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PlayerCustomBuffAction implements BoardAction {

    private final static Logger LOG = LoggerFactory.getLogger(PlayerCustomBuffAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty() || args.getArgs().size() != 2) {
            LOG.warn("{} is trying to buff custom preset with invalid args number {}", player, args.getArgs().size());
            return ProcessResult.failure("Invalid buff preset request");
        }

        String presetName = args.getArgs().get(0);

        Optional<CommunityBuffList> communityBuffList = DAOFactory.getInstance().getCommunityBuffListDao().findSingleCommunityBuffSet(player.getObjectId(), presetName);
        if (!communityBuffList.isPresent()) {
            LOG.warn("Player {} is trying to buff a preset {} that does not belong to him", player, presetName);
            return ProcessResult.failure("Could not buff your custom preset");
        }

        PresetBuffAction presetBuffAction = new PresetBuffAction(() -> communityBuffList.get().getSkills());
        return presetBuffAction.process(player, ActionArgs.subActionArgs(args));
    }

}
