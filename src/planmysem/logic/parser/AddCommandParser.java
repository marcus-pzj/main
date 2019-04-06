package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL;
import static planmysem.common.Messages.MESSAGE_INVALID_DATE_OR_DAY;
import static planmysem.common.Messages.MESSAGE_INVALID_ENDTIME;
import static planmysem.common.Messages.MESSAGE_INVALID_TAG;
import static planmysem.common.Messages.MESSAGE_INVALID_TIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import planmysem.common.Utils;
import planmysem.logic.commands.AddCommand;
import planmysem.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @param args full command args string
     * @return the prepared command
     */
    public AddCommand parse(String args) throws ParseException {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);

        // Name is mandatory
        String name = getFirstInSet(arguments.get(PREFIX_NAME));

        if ((arguments == null || arguments.isEmpty())
                || (name == null || name.isEmpty())
                || arguments.get(PREFIX_DATE_OR_DAY) == null
                || arguments.get(PREFIX_START_TIME) == null
                || arguments.get(PREFIX_END_TIME) == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        // Either date or day must be present
        String dateOrDay = getFirstInSet(arguments.get(PREFIX_DATE_OR_DAY));
        int day = -1;
        LocalDate date = Utils.parseDate(dateOrDay);
        if (date == null) {
            day = Utils.parseDay(dateOrDay);
        }
        if (day == -1 && date == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                    AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_DATE_OR_DAY));
        }

        // Start time is mandatory
        String stringStartTime = getFirstInSet(arguments.get(PREFIX_START_TIME));
        LocalTime startTime = Utils.parseTime(stringStartTime);
        if (startTime == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                    AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
        }

        // determine if "end time" is a duration or time
        String stringEndTime = getFirstInSet(arguments.get(PREFIX_END_TIME));
        int duration = Utils.parseInteger(stringEndTime);

        if (duration == -1) {
            LocalTime endTime = Utils.parseTime(stringEndTime);
            if (endTime == null) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
            }
            duration = Utils.getDuration(startTime, endTime);
            if (duration < 0) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_ENDTIME));
            }
        }

        // Description is not mandatory and can be null
        String description = getFirstInSet(arguments.get(PREFIX_DESCRIPTION));

        // Location is not mandatory and can be null
        String location = getFirstInSet(arguments.get(PREFIX_LOCATION));

        // Tags is not mandatory
        Set<String> tags = arguments.get(PREFIX_TAG);
        if (tags != null) {
            for (String tag : tags) {
                if (tag.length() == 0) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                            AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TAG));
                }
            }
        } else {
            tags = new HashSet<>();
        }

        // Recurrences is not mandatory
        Set<String> recurrences = arguments.get(PREFIX_RECURRENCE);

        if (day != -1) {
            return new AddCommand(
                    day,
                    name,
                    location,
                    description,
                    startTime,
                    duration,
                    tags,
                    recurrences
            );
        } else {
            return new AddCommand(
                    date,
                    name,
                    location,
                    description,
                    startTime,
                    duration,
                    tags,
                    recurrences
            );
        }
    }
}
