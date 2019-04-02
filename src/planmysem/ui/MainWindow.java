package planmysem.ui;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.logic.LogicManager;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.ExitCommand;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;

/**
 * Main Window of the GUI.
 */
public class MainWindow {

    private LogicManager logicManager;
    private Stoppable mainApp;
    @FXML
    private TextArea outputConsole;
    @FXML
    private TextField commandInput;

    public void setLogicManager(LogicManager logicManager) {
        this.logicManager = logicManager;
    }

    public void setMainApp(Stoppable mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * TODO: Add Javadoc comment.
     */
    @FXML
    private void onCommand() {
        try {
            String userCommandText = commandInput.getText();
            CommandResult result = logicManager.execute(userCommandText);
            if (isExitCommand(result)) {
                exitApp();
                return;
            }
            displayResult(result);
            clearCommandInput();
        } catch (Exception e) {
            display(e.getMessage());
            // throw new RuntimeException(e);
        }
    }

    private void exitApp() throws Exception {
        mainApp.stop();
    }

    /**
     * Returns true of the result given is the result of an exit command
     */
    private boolean isExitCommand(CommandResult result) {
        return result.feedbackToUser.equals(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT);
    }

    /**
     * Clears the command input box
     */
    private void clearCommandInput() {
        commandInput.setText("");
    }

    /**
     * Clears the output display area
     */
    public void clearOutputConsole() {
        outputConsole.clear();
    }

    /**
     * Displays the result of a command execution to the user.
     */
    public void displayResult(CommandResult result) {
        clearOutputConsole();
        final Optional<Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> resultDays = result.getRelevantSlots();
        if (resultDays.isPresent()) {
            display(resultDays.get());
        }
        display(result.feedbackToUser);
    }

    /**
     * TODO: Add Javadoc comment.
     */
    public void displayWelcomeMessage(String version, String storageFilePath) {
        String storageFileInfo = String.format(Messages.MESSAGE_USING_STORAGE_FILE, storageFilePath);
        display(Messages.MESSAGE_WELCOME, version, Messages.MESSAGE_PROGRAM_LAUNCH_ARGS_USAGE, storageFileInfo);
    }

    /**
     * Displays the list of slots in the output display area, formatted as an indexed list.
     * Private contact details are hidden.
     */
    private void display(Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slots) {
        display(new Formatter().formatSlots(slots));
    }

    /**
     * Displays the given messages on the output display area, after formatting appropriately.
     */
    private void display(String... messages) {
        clearOutputConsole();
        outputConsole.setText(outputConsole.getText() + new Formatter().format(messages));
    }
}
