package planmysem.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL;
import static planmysem.common.Messages.MESSAGE_INVALID_DATE;
import static planmysem.common.Messages.MESSAGE_INVALID_MULTIPLE_PARAMS;
import static planmysem.common.Messages.MESSAGE_INVALID_TIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import planmysem.commands.AddCommand;
import planmysem.commands.ClearCommand;
import planmysem.commands.Command;
import planmysem.commands.DeleteCommand;
import planmysem.commands.EditCommand;
import planmysem.commands.ExitCommand;
import planmysem.commands.ExportCommand;
import planmysem.commands.FindCommand;
import planmysem.commands.HelpCommand;
import planmysem.commands.IncorrectCommand;
import planmysem.commands.ListCommand;
import planmysem.commands.ViewCommand;
import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;

/**
 * Parses user input.
 */
public class Parser {

    public static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace

    private static final String PARAMETER_NAME = "n";
    private static final String PARAMETER_DATE_OR_DAY = "d";
    private static final String PARAMETER_START_TIME = "st";
    private static final String PARAMETER_END_TIME = "et";
    private static final String PARAMETER_RECURRENCE = "r";
    private static final String PARAMETER_LOCATION = "l";
    private static final String PARAMETER_DESCRIPTION = "des";
    private static final String PARAMETER_TAG = "t";
    private static final String PARAMETER_NEW_NAME = "nn";
    private static final String PARAMETER_NEW_DATE = "nd";
    private static final String PARAMETER_NEW_START_TIME = "nst";
    private static final String PARAMETER_NEW_END_TIME = "net";
    private static final String PARAMETER_NEW_LOCATION = "nl";
    private static final String PARAMETER_NEW_DESCRIPTION = "ndes";
    private static final String PARAMETER_NEW_TAG = "nt";

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {
        case AddCommand.COMMAND_WORD:
        case AddCommand.COMMAND_WORD_SHORT:
            return prepareAdd(arguments);

        case EditCommand.COMMAND_WORD:
        case EditCommand.COMMAND_WORD_SHORT:
            return prepareEdit(arguments);

        case DeleteCommand.COMMAND_WORD:
        case DeleteCommand.COMMAND_WORD_ALT:
        case DeleteCommand.COMMAND_WORD_SHORT:
            return prepareDelete(arguments);

        case FindCommand.COMMAND_WORD:
        case FindCommand.COMMAND_WORD_SHORT:
            return prepareFind(arguments);

        case ListCommand.COMMAND_WORD:
        case ListCommand.COMMAND_WORD_SHORT:
            return prepareList(arguments);

        case ViewCommand.COMMAND_WORD:
        case ViewCommand.COMMAND_WORD_SHORT:
            return prepareView(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case ExportCommand.COMMAND_WORD:
            return new ExportCommand();

        case HelpCommand.COMMAND_WORD: // Fallthrough

        default:
            return new HelpCommand();
        }
    }

    /**
     * Parses arguments in the context of the add command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args) {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);

        // Name is mandatory
        String name = getFirstInSet(arguments.get(PARAMETER_NAME));

        if ((arguments == null || arguments.isEmpty())
                || (name == null || name.isEmpty())
                || arguments.get(PARAMETER_DATE_OR_DAY) == null
                || arguments.get(PARAMETER_START_TIME) == null
                || arguments.get(PARAMETER_END_TIME) == null) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        // Either date or day must be present
        String dateOrDay = getFirstInSet(arguments.get(PARAMETER_DATE_OR_DAY));
        int day = -1;
        LocalDate date = Utils.parseDate(dateOrDay);
        if (date == null) {
            day = Utils.parseDay(dateOrDay);
        }
        if (day == -1 && date == null) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                    AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_DATE));
        }

        // Start time is mandatory
        String stringStartTime = getFirstInSet(arguments.get(PARAMETER_START_TIME));
        LocalTime startTime = Utils.parseTime(stringStartTime);
        if (startTime == null) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                    AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
        }

        // determine if "end time" is a duration or time
        String stringEndTime = getFirstInSet(arguments.get(PARAMETER_END_TIME));
        int duration = Utils.parseInteger(stringEndTime);

        if (duration == -1) {
            LocalTime endTime = Utils.parseTime(stringEndTime);
            if (endTime == null) {
                return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
            }
            duration = Utils.getDuration(startTime, endTime);
        }

        // Description is not mandatory and can be null
        String description = getFirstInSet(arguments.get(PARAMETER_DESCRIPTION));

        // Location is not mandatory and can be null
        String location = getFirstInSet(arguments.get(PARAMETER_LOCATION));

        // Tags is not mandatory
        Set<String> tags = arguments.get(PARAMETER_TAG);
        if (tags != null) {
            for (String tag : tags) {
                if (tag.length() == 0) {
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            AddCommand.MESSAGE_USAGE));
                }
            }
        } else {
            tags = new HashSet<>();
        }

        // Recurrences is not mandatory
        Set<String> recurrences = arguments.get(PARAMETER_RECURRENCE);

        try {
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
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Parses arguments in the context of the edit command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareEdit(String args) {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);
        String stringIndex = getStartingArgument(args);
        int index = Utils.parseInteger(stringIndex);
        Set<String> tags = arguments.get(PARAMETER_TAG);

        if ((index == -1 && tags == null) || (index != -1 && tags != null)) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        String nst = getFirstInSet(arguments.get(PARAMETER_NEW_START_TIME));
        LocalTime startTime = null;
        if (nst != null) {
            startTime = Utils.parseTime(nst);
            if (startTime == null) {
                return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
            }
        }

        // determine if "end time" is a duration or time
        String net = getFirstInSet(arguments.get(PARAMETER_NEW_END_TIME));
        int duration = -1;
        if (net != null) {
            duration = Utils.parseInteger(net);
            if (duration == -1) {
                LocalTime endTime = Utils.parseTime(net);
                if (endTime == null) {
                    return new IncorrectCommand(String.format(
                            MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
                } else {
                    duration = Utils.getDuration(startTime, endTime);
                }
            }
        }

        String name = getFirstInSet(arguments.get(PARAMETER_NEW_NAME));
        String location = getFirstInSet(arguments.get(PARAMETER_NEW_LOCATION));
        String description = getFirstInSet(arguments.get(PARAMETER_NEW_DESCRIPTION));
        Set<String> newTags = arguments.get(PARAMETER_NEW_TAG);

        if (index == -1) {
            try {
                return new EditCommand(name, startTime, duration, location, description, tags, newTags);
            } catch (IllegalValueException ive) {
                return new IncorrectCommand(ive.getMessage());
            }
        } else {
            String nd = getFirstInSet(arguments.get(PARAMETER_NEW_DATE));
            LocalDate date = Utils.parseDate(nd);

            try {
                return new EditCommand(index, name, date, startTime, duration, location, description, newTags);
            } catch (IllegalValueException ive) {
                return new IncorrectCommand(ive.getMessage());
            }
        }
    }

    /**
     * Parses arguments in the context of the delete command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);
        String stringIndex = getStartingArgument(args);
        int index = Utils.parseInteger(stringIndex);
        Set<String> tags = arguments.get(PARAMETER_TAG);

        if ((index == -1 && tags == null) || (index != -1 && tags != null)) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        if (index == -1) {
            try {
                return new DeleteCommand(tags);
            } catch (IllegalValueException ive) {
                return new IncorrectCommand(ive.getMessage());
            }
        } else {
            return new DeleteCommand(index);
        }
    }

    /**
     * Parses arguments in the context of the find person command.
     *
     * @param args partial args string
     * @return the arguments sorted by its relevant options
     */
    private Command prepareFind(String args) {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);
        String name = getFirstInSet(arguments.get(PARAMETER_NAME));
        String tag = getFirstInSet(arguments.get(PARAMETER_TAG));
        if (name == null && tag == null) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        } else if (name != null && tag != null) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_MULTIPLE_PARAMS, FindCommand.MESSAGE_USAGE));

        }
        return new FindCommand(name, tag);
    }

    /**
     * Parses arguments in the context of the list command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareList(String args) {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);
        String name = getFirstInSet(arguments.get(PARAMETER_NAME));
        String tag = getFirstInSet(arguments.get(PARAMETER_TAG));
        if (name == null && tag == null) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        } else if (name != null && tag != null) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_MULTIPLE_PARAMS, ListCommand.MESSAGE_USAGE));
        }
        return new ListCommand(name, tag);
    }

    /**
     * Parses arguments in the context of the view command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareView(String args) {
        if (args == null || args.trim().isEmpty()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
        }

        String[] viewArgs = args.split(" ");
        if ("all".equals(viewArgs[1]) && viewArgs.length == 2) {
            return new ViewCommand(viewArgs[1]);
        } else if ("month".equals(viewArgs[1]) && viewArgs.length == 2) {
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

        return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
    }

    /**
     * Parses arguments in the context of the add slots command.
     *
     * @return hashmap of parameter command with set of parameters.
     */
    private static HashMap<String, Set<String>> getParametersWithArguments(String args) {
        String parameters = args.trim();
        String parameter;
        String option;
        int buf;
        HashMap<String, Set<String>> result = new HashMap<>();
        while (true) {
            buf = parameters.indexOf('/');
            if (buf == -1) {
                break;
            }

            option = parameters.substring(0, buf).trim();
            if (option.contains(" ")) {
                parameters = parameters.substring(option.lastIndexOf(" "));
                continue;
            }

            parameters = parameters.substring(buf + 1);

            if (parameters.indexOf('/') != -1) {
                parameter = parameters.substring(0, parameters.indexOf('/'));
                if (parameter.indexOf(' ') != -1) {
                    parameter = parameter.substring(0, parameter.lastIndexOf(" "));
                }
            } else {
                parameter = parameters;
            }

            if (result.get(option) == null) {
                result.put(option, new HashSet<>(Collections.singletonList(parameter.trim())));
            } else {
                Set<String> ss = result.get(option);
                ss.add(parameter.trim());
                result.replace(option, ss);
            }

            if (parameters.length() == 0) {
                break;
            }
            parameters = parameters.substring(parameter.length());
        }

        return result;
    }

    /**
     * Get the first argument.
     */
    private String getStartingArgument(String args) {
        String result = args;

        // test if firstArgument is present
        if (result.trim().length() == 0) {
            return null;
        } else if (result.indexOf('/') != -1) {
            result = result.substring(0, result.indexOf('/'));
            return result.substring(0, result.lastIndexOf(" ")).trim();
        } else {
            return result.trim();
        }
    }

    /**
     * Get the first string in a set.
     */
    private String getFirstInSet(Set<String> set) {
        if (set == null || set.size() == 0) {
            return null;
        }
        return set.stream().findFirst().get();
    }

    /**
     * Signals that the user input could not be parsed.
     */
    public static class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }
}
