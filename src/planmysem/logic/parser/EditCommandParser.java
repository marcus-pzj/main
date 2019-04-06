package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL;
import static planmysem.common.Messages.MESSAGE_INVALID_TIME;
import static planmysem.common.Messages.MESSAGE_NOTHING_TO_EDIT;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Set;

import planmysem.common.Utils;
import planmysem.logic.commands.EditCommand;
import planmysem.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     *
     * @param args full command args string
     * @return the prepared command
     */
    public EditCommand parse(String args) throws ParseException {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);
        String stringIndex = getStartingArgument(args);
        int index = Utils.parseInteger(stringIndex);
        Set<String> tags = arguments.get(PREFIX_TAG);

        if ((index <= 0 && tags == null) || (index >= 0 && tags != null)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        String nst = getFirstInSet(arguments.get(PREFIX_NEW_START_TIME));
        LocalTime startTime = null;
        if (nst != null) {
            startTime = Utils.parseTime(nst);
            if (startTime == null) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        EditCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
            }
        }

        // determine if "end time" is a duration or time
        String net = getFirstInSet(arguments.get(PREFIX_NEW_END_TIME));
        int duration = -1;
        if (net != null) {
            duration = Utils.parseInteger(net);
            if (duration == -1) {
                LocalTime endTime = Utils.parseTime(net);
                if (endTime == null) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                            EditCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
                } else {
                    duration = Utils.getDuration(startTime, endTime);
                }
            }
        }

        // The following are not mandatory and can be null
        String name = getFirstInSet(arguments.get(PREFIX_NEW_NAME));
        String location = getFirstInSet(arguments.get(PREFIX_NEW_LOCATION));
        String description = getFirstInSet(arguments.get(PREFIX_NEW_DESCRIPTION));
        Set<String> newTags = arguments.get(PREFIX_NEW_TAG);

        // check if no edits
        if ((name == null || name.isEmpty())
                && startTime == null && duration == -1
                && (location == null || location.isEmpty())
                && (description == null || description.isEmpty())
                && newTags == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                    EditCommand.MESSAGE_USAGE, MESSAGE_NOTHING_TO_EDIT));
        }

        if (index == -1) {
            return new EditCommand(name, startTime, duration, location, description, tags, newTags);
        } else {
            String nd = getFirstInSet(arguments.get(PREFIX_NEW_DATE));
            LocalDate date = Utils.parseDate(nd);

            return new EditCommand(index, name, date, startTime, duration, location, description, newTags);
        }
    }
}
