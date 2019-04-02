package planmysem.logic.Commands;

import static planmysem.logic.Commands.CommandTestUtil.assertCommandSuccess;
import static planmysem.logic.commands.ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT;

import org.junit.Test;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.ExitCommand;
import planmysem.model.Model;
import planmysem.model.ModelManager;

public class ExitCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_exit_success() {
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT);
        assertCommandSuccess(new ExitCommand(), model, commandHistory, expectedCommandResult, expectedModel);
    }
}
