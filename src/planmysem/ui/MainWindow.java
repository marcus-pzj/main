package planmysem.ui;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import planmysem.commands.CommandResult;
import planmysem.commands.ExitCommand;
import planmysem.common.Messages;
import planmysem.data.semester.ReadOnlyDay;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.logic.Logic;

/**
 * Main Window of the GUI.
 */
public class MainWindow {

    private Logic logic;
    private Stoppable mainApp;
    @FXML
    private TextArea outputConsole;
    @FXML
    private TextField commandInput;

    public void setLogic(Logic logic) {
        this.logic = logic;
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
            CommandResult result = logic.execute(userCommandText);
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
        return result.feedbackToUser.equals(ExitCommand.MESSAGE_EXIT_ACKNOWEDGEMENT);
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
        // TODO: rename function call when AddressBook is fully removed from project
        display(new Formatter().formatSlots(slots));
    }

    /**
     * Displays the given messages on the output display area, after formatting appropriately.
     */
    private void display(String... messages) {
        outputConsole.setText(outputConsole.getText() + new Formatter().format(messages));
    }
}
