//@@author marcus-pzj
package planmysem.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;

import planmysem.logic.CommandHistory;
import planmysem.model.Model;

/**
 * Lists all the commands entered by user from the start of app launch.
 */
public class HistoryCommand extends Command {

    public static final String COMMAND_WORD = "history";
    public static final String COMMAND_WORD_SHORT = "h";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all the commands that you have entered in reverse chronological order."
            + "\n\tExample: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Entered commands (from most recent to earliest):\n%1$s";
    public static final String MESSAGE_NO_HISTORY = "You have not yet entered any commands.";


    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) {
        requireNonNull(commandHistory);
        ArrayList<String> previousCommands = new ArrayList<>(commandHistory.getHistory());

        if (previousCommands.isEmpty()) {
            return new CommandResult(MESSAGE_NO_HISTORY);
        }

        Collections.reverse(previousCommands);
        return new CommandResult(String.format(MESSAGE_SUCCESS, String.join("\n", previousCommands)));
    }

}

