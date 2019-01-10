package handlers.communityboard.custom.actions;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.localization.Strings;
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
            return ProcessResult.failure(Strings.of(player).get("no_action_route_specified"));
        }

        String actionName = args.getArgs().get(0);
        BoardAction boardAction = actions.get(actionName);
        if (boardAction == null) {
            return ProcessResult.failure(Strings.of(player).get("no_action_named_n_is_available").replace("$n", actionName));
        }

        return boardAction.process(player, ActionArgs.subActionArgs(args));
    }
}
