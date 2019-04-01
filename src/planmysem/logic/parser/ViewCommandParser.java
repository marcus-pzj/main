package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_ILLEGAL_WEEK_VALUE;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_DATE;
import static planmysem.common.Utils.getNearestDayOfWeek;

import java.time.LocalDate;

import planmysem.common.Clock;
import planmysem.common.Utils;
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

        String[] viewArgs = args.trim().split(" ");
        String viewType = viewArgs[0];

        switch (viewType) {
        case "month":
            if (viewArgs.length == 1) {
                return new ViewCommand(viewArgs);
            }

            break;

        case "week":
            if (viewArgs.length == 1) {
                return new ViewCommand(viewArgs);

            } else if (viewArgs.length == 2) {
                viewArgs[1] = viewArgs[1].substring(0, 1).toUpperCase() + viewArgs[1].substring(1).toLowerCase();
                if ("Exam".equals(viewArgs[1])) {
                    viewArgs[1] = "Examination";
                }

                if (!"Details".equals(viewArgs[1]) && checkIllegalWeekValue(viewArgs[1])) {
                    throw new ParseException(String.format(MESSAGE_ILLEGAL_WEEK_VALUE, ViewCommand.MESSAGE_USAGE));
                }
                return new ViewCommand(viewArgs);

            } else if (viewArgs.length == 3 ) {
                viewArgs[1] = viewArgs[1].substring(0, 1).toUpperCase() + viewArgs[1].substring(1).toLowerCase();
                viewArgs[2] = viewArgs[2].substring(0, 1).toUpperCase() + viewArgs[2].substring(1).toLowerCase();
                if ("Exam".equals(viewArgs[1])) {
                    viewArgs[1] = "Examination";
                }
                if ("Exam".equals(viewArgs[2])) {
                    viewArgs[2] = "Examination";
                }
                if ("Details".equals(viewArgs[1])) {
                    String tempArg = viewArgs[1];
                    viewArgs[1] = viewArgs[2];
                    viewArgs[2] = tempArg;
                }

                if (!"Details".equals(viewArgs[2]) || checkIllegalWeekValue(viewArgs[1])) {
                    throw new ParseException(String.format(MESSAGE_ILLEGAL_WEEK_VALUE, ViewCommand.MESSAGE_USAGE));
                }
                return new ViewCommand(viewArgs);
            }

            break;

        case "day":
            if (viewArgs.length == 1) {
                return new ViewCommand(viewArgs);

            } else if (viewArgs.length == 2) {
                LocalDate date = null;
                int day = -1;

                // Parse different formats of given day/date.
                date = Utils.parseDate(viewArgs[1]);
                if (date == null) {
                    day = Utils.parseDay(viewArgs[1]);
                }
                if (day == -1 && date == null) {
                    throw new ParseException(String.format(MESSAGE_INVALID_DATE, ViewCommand.MESSAGE_USAGE));
                }
                if (day != -1) {
                    date = getNearestDayOfWeek(LocalDate.now(Clock.get()), day);
                }

                return new ViewCommand(viewArgs);
            }

            break;

        default:
            break;
        }

        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
    }

    /**
     * Checks if user inputs an illegal week value.
     */
    private Boolean checkIllegalWeekValue(String week) {
        Boolean illegalWeek = true;
        int weekNo = 0;

        if (week.matches("\\d+")) {
            weekNo = Integer.parseInt(week);
        }

        if (weekNo > 0 && weekNo < 14) {
            illegalWeek = false;
        }

        if ("Recess".equals(week) || "Reading".equals(week) || "Examination".equals(week)
                || "Orientation".equals(week)) {
            illegalWeek = false;
        }

        return illegalWeek;
    }
}
