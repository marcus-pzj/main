package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.HashMap;
import java.util.Set;

import planmysem.logic.commands.ExportCommand;
import planmysem.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ExportCommand object
 */
public class ExportCommandParser implements Parser<ExportCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ExportCommand
     * and returns an ExportCommand object for execution.
     *
     * @param args full command args string
     * @return the prepared command
     */
    public ExportCommand parse(String args) throws ParseException {
        String trimArg = args.trim();
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);
        String fileName = getFirstInSet(arguments.get(PREFIX_FILE_NAME));

        if (fileName == null || "".equals(fileName)) {
            if ("".equals(trimArg)) {
                return new ExportCommand("PlanMySem");
            } else {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
            }
        } else {
            return new ExportCommand(fileName);
        }
    }
}
