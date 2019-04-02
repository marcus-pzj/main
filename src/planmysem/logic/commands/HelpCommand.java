package planmysem.logic.commands;

import planmysem.logic.CommandHistory;
import planmysem.model.Model;

/**
 * Shows help instructions.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions."
            + "\n\tExample: " + COMMAND_WORD;

    public static final String MESSAGE_ALL_USAGES = AddCommand.MESSAGE_USAGE
            + "\n\n" + EditCommand.MESSAGE_USAGE
            + "\n\n" + DeleteCommand.MESSAGE_USAGE
            + "\n\n" + ListCommand.MESSAGE_USAGE
            + "\n\n" + FindCommand.MESSAGE_USAGE
            + "\n\n" + ViewCommand.MESSAGE_USAGE
            + "\n\n" + HistoryCommand.MESSAGE_USAGE
            + "\n\n" + UndoCommand.MESSAGE_USAGE
            + "\n\n" + RedoCommand.MESSAGE_USAGE
            + "\n\n" + ExportCommand.MESSAGE_USAGE
            + "\n\n" + ImportCommand.MESSAGE_USAGE
            + "\n\n" + ClearCommand.MESSAGE_USAGE
            + "\n\n" + HelpCommand.MESSAGE_USAGE
            + "\n\n" + ExitCommand.MESSAGE_USAGE;

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) {
        return new CommandResult(MESSAGE_ALL_USAGES);
    }
}
