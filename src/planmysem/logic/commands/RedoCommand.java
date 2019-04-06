//@@author marcus-pzj
package planmysem.logic.commands;

import static java.util.Objects.requireNonNull;

import planmysem.logic.CommandHistory;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.model.Model;

/**
 * Reverts the {@code model}'s planner to its previously undone state.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";
    public static final String COMMAND_WORD_SHORT = "r";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Reverses the most recent undo command."
            + "\n\tExample: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Redo success!";
    public static final String MESSAGE_FAILURE = "No more commands to redo!";

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (!model.canRedo()) {
            throw new CommandException(MESSAGE_FAILURE);
        }

        model.redo();
        model.clearLastShownList();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
