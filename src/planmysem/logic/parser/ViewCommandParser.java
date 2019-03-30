package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import planmysem.logic.commands.ViewCommand;
import planmysem.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewCommand object
 */
public class ViewCommandParser implements Parser<ViewCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewCommand
     * and returns an ViewCommand object for execution.
     *
     * @param args full command args string
     * @return the prepared command
     */
    public ViewCommand parse(String args) throws ParseException {
        if (args == null || args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
        }

        String[] viewArgs = args.split(" ");
        if ("all".equals(viewArgs[1]) && viewArgs.length == 2) {
            return new ViewCommand(viewArgs[1]);
        } else if ("month".equals(viewArgs[1]) && viewArgs.length == 2) {
            return new ViewCommand(viewArgs[1]);
        } else if ("week".equals(viewArgs[1]) && viewArgs.length == 2) {
            return new ViewCommand(viewArgs[1]);
        } else if ("day".equals(viewArgs[1]) && viewArgs.length == 2) {
            return new ViewCommand(viewArgs[1]);
        } else if ("month".equals(viewArgs[1]) && viewArgs.length == 3) {
            //TODO: ensure month arguments
            return new ViewCommand(viewArgs[1] + " " + viewArgs[2]);
        } else if ("week".equals(viewArgs[1]) && viewArgs.length == 3) {
            //TODO: ensure week arguments
            return new ViewCommand(viewArgs[1] + " " + viewArgs[2]);
        } else if ("day".equals(viewArgs[1]) && viewArgs.length == 3) {
            //TODO: ensure day arguments
            return new ViewCommand(viewArgs[1] + " " + viewArgs[2]);
        }

        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
    }
}
