package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import planmysem.logic.commands.AddCommand;
import planmysem.logic.commands.ClearCommand;
import planmysem.logic.commands.Command;
import planmysem.logic.commands.DeleteCommand;
import planmysem.logic.commands.EditCommand;
import planmysem.logic.commands.ExitCommand;
import planmysem.logic.commands.ExportCommand;
import planmysem.logic.commands.FindCommand;
import planmysem.logic.commands.HelpCommand;
import planmysem.logic.commands.HistoryCommand;
import planmysem.logic.commands.ImportCommand;
import planmysem.logic.commands.ListCommand;
import planmysem.logic.commands.RedoCommand;
import planmysem.logic.commands.UndoCommand;
import planmysem.logic.commands.ViewCommand;
import planmysem.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class ParserManager {
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
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {
        case AddCommand.COMMAND_WORD:
        case AddCommand.COMMAND_WORD_SHORT:
            return new AddCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
        case EditCommand.COMMAND_WORD_SHORT:
            return new EditCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
        case DeleteCommand.COMMAND_WORD_ALT:
        case DeleteCommand.COMMAND_WORD_SHORT:
            return new DeleteCommandParser().parse(arguments);

        case FindCommand.COMMAND_WORD:
        case FindCommand.COMMAND_WORD_SHORT:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
        case ListCommand.COMMAND_WORD_SHORT:
            return new ListCommandParser().parse(arguments);

        case ViewCommand.COMMAND_WORD:
        case ViewCommand.COMMAND_WORD_SHORT:
            return new ViewCommandParser().parse(arguments);

        case HistoryCommand.COMMAND_WORD:
            return new HistoryCommand();

        case UndoCommand.COMMAND_WORD:
            return new UndoCommand();

        case RedoCommand.COMMAND_WORD:
            return new RedoCommand();

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case ExportCommand.COMMAND_WORD:
            return new ExportCommand();

        case ImportCommand.COMMAND_WORD:
            return new ImportCommand(arguments);

        case HelpCommand.COMMAND_WORD: // Fallthrough

        default:
            return new HelpCommand();
        }
    }
}
