package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;

import java.util.Map;

public class RouteAction implements BoardAction {

    private final Map<String, BoardAction> actions;

    public RouteAction(Map<String, BoardAction> actions) {
        this.actions = actions;
    }

    public Map<String, BoardAction> getActions() {
        return actions;
    }

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.getArgs().isEmpty()) {
            return ProcessResult.failure("No action route specified");
        }

        BoardAction boardAction = actions.get(args.getArgs().get(0));
        if (boardAction == null) {
            return ProcessResult.failure("No action named " + args.getActionName() + " is available");
        }

        return boardAction.process(player, ActionArgs.subActionArgs(args));
    }
}
