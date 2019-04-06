package planmysem.logic.Commands;

import static planmysem.logic.Commands.CommandTestUtil.assertCommandSuccess;
import static planmysem.logic.commands.HelpCommand.MESSAGE_ALL_USAGES;

import org.junit.Test;

import planmysem.logic.CommandHistory;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.HelpCommand;
import planmysem.model.Model;
import planmysem.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_ALL_USAGES);
        assertCommandSuccess(new HelpCommand(), model, commandHistory, expectedCommandResult, expectedModel);
    }
}
