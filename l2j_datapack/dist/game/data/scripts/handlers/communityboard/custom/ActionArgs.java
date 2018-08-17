package handlers.communityboard.custom;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ActionArgs {

    private final String commandPath;
    private final String actionName;
    private final List<String> args;

    public ActionArgs(String commandPath, String actionName, List<String> args) {
        this.commandPath = commandPath;
        this.actionName = actionName;
        this.args = args;
    }

    public static ActionArgs subActionArgs(ActionArgs actionArgs) {
        if (actionArgs.isEmpty()) {
            throw new IllegalStateException("Illegal sub action " + actionArgs);
        }

        List<String> newArgs;
        if (actionArgs.getArgs().size() > 1) {
            newArgs = actionArgs.getArgs().subList(1, actionArgs.getArgs().size());
        } else {
            newArgs = Collections.emptyList();
        }

        return new ActionArgs(actionArgs.getCommandPath() + " " + actionArgs.getActionName(), actionArgs.getArgs().get(0), newArgs);
    }

    public static Optional<ActionArgs> parse(String command) {
        if (command == null || command.trim().length() <= 0) {
            return Optional.empty();
        }

        String[] splinters = command.split(" ");
        if (splinters.length == 2) {
            return Optional.of(new ActionArgs(splinters[0], splinters[1], Collections.emptyList()));
        } else if (splinters.length > 2) {
            List<String> args = Arrays.asList(splinters).subList(2, splinters.length);
            return Optional.of(new ActionArgs(splinters[0], splinters[1], args));
        } else {
            return Optional.empty();
        }
    }

    public String getCommandPath() {
        return commandPath;
    }

    public String getActionName() {
        return actionName;
    }

    public List<String> getArgs() {
        return args;
    }

    public boolean isEmpty() {
        return args == null || args.isEmpty();
    }

    @Override
    public String toString() {
        return "ActionArgs{" +
                "commandPath='" + commandPath + '\'' +
                ", actionName='" + actionName + '\'' +
                ", args=" + args +
                '}';
    }

}
