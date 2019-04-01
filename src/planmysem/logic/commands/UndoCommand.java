//@@author marcus-pzj
package planmysem.logic.commands;

import static java.util.Objects.requireNonNull;

import planmysem.logic.CommandHistory;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.model.Model;

/**
 * Reverts the {@code model}'s planner to its previous state.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Undo success!";
    public static final String MESSAGE_FAILURE = "No more commands to undo!";

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (!model.canUndo()) {
            throw new CommandException(MESSAGE_FAILURE);
        }

        model.undo();
        model.clearLastShownList();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
