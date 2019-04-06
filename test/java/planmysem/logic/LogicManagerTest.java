package planmysem.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static planmysem.common.Messages.MESSAGE_INVALID_SLOT_DISPLAYED_INDEX;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import planmysem.common.Clock;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.HistoryCommand;
import planmysem.logic.commands.ListCommand;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.logic.parser.exceptions.ParseException;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;
import planmysem.storage.StorageFile;
import planmysem.testutil.SlotBuilder;


public class LogicManagerTest {
    private static final String testFileName = "testSaveFile.txt";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private StorageFile storageFile;
    private Model model;
    private Logic logic;

    @Before
    public void setUp() throws Exception {
        Clock.set("2019-01-14T10:00:00Z");
        storageFile = new StorageFile(temporaryFolder.getRoot().getPath() + "\\" + testFileName);
        logic = new LogicManager(storageFile);
        model = new ModelManager();
    }


    @Test
    public void execute_throwsStorageOperationException() throws CommandException, ParseException {
        // delete save file
        File file = new File(temporaryFolder.getRoot().getPath() + "\\" + testFileName);
        file.setReadOnly();

        Slot slot = new SlotBuilder().slotOne();
        String cmd = SlotBuilder.generateAddCommand(slot, 2, "");

        thrown.expect(CommandException.class);
        logic.execute(cmd);
    }

    @Test
    public void getStorageFilePath() {
        assertEquals(logic.getStorageFilePath(), storageFile.getPath());
    }

    @Test
    public void getLastShownSlots() throws CommandException, ParseException {
        Slot slot = new SlotBuilder().slotOne();
        String cmd = SlotBuilder.generateAddCommand(slot, 2, "");
        logic.execute(cmd);
        logic.execute("list n/CS2113T Tutorial");

        List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> lastShownSlots
                = new ArrayList<>();
        Day day = new Day(DayOfWeek.TUESDAY, "Week 1");
        day.addSlot(slot);
        lastShownSlots.add(new Pair<>(LocalDate.of(2019, 1, 15),
                new Pair<>(day, new SlotBuilder().slotOne())));

        assertEquals(logic.getLastShownSlots(), lastShownSlots);
    }

    @Test
    public void getHistory() throws Exception {
        ObservableList<String> expectedHistory =
                FXCollections.observableArrayList();

        Slot slot = new SlotBuilder().slotOne();
        String cmd = SlotBuilder.generateAddCommand(slot, 2, "");
        logic.execute(cmd);
        expectedHistory.add(cmd);

        logic.execute("list n/CS2113T Tutorial");
        expectedHistory.add("list n/CS2113T Tutorial");

        logic.execute("view week");
        expectedHistory.add("view week");

        logic.execute("d 1");
        expectedHistory.add("d 1");

        assertEquals(logic.getHistory(), expectedHistory);
        assertEquals(logic.getHistory().hashCode(), expectedHistory.hashCode());

        // equal same object
        assertEquals(logic.getHistory(), logic.getHistory());
        assertEquals(logic.getHistory().hashCode(), logic.getHistory().hashCode());

        // equal null
        assertNotEquals(logic.getHistory(), null);
    }


    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 3";
        assertCommandException(deleteCommand, MESSAGE_INVALID_SLOT_DISPLAYED_INDEX);
        assertHistoryCorrect(deleteCommand);
    }

    @Test
    public void execute_validCommand_success() {
        String listCommand = ListCommand.COMMAND_WORD + " n/CS2113T";
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS_NONE, model);
        assertHistoryCorrect(listCommand);
    }

    /**
     * Executes the command, confirms that no exceptions are thrown and that the result message is correct.
     * Also confirms that {@code expectedModel} is as specified.
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage, Model expectedModel) {
        assertCommandBehavior(null, inputCommand, expectedMessage, expectedModel);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<?> expectedException, String expectedMessage) {
        Model expectedModel = new ModelManager();
        assertCommandBehavior(expectedException, inputCommand, expectedMessage, expectedModel);
    }

    /**
     * Executes the command, confirms that the result message is correct and that the expected exception is thrown,
     * and also confirms that the following two parts of the LogicManager object's state are as expected:<br>
     *      - the internal model manager data are same as those in the {@code expectedModel} <br>
     *      - {@code expectedModel}'s planner was saved to the storage file.
     */
    private void assertCommandBehavior(Class<?> expectedException, String inputCommand,
                                       String expectedMessage, Model expectedModel) {

        try {
            CommandResult result = logic.execute(inputCommand);
            assertNull(expectedException);
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException | ParseException e) {
            assertEquals(expectedException, e.getClass());
            assertEquals(expectedMessage, e.getMessage());
        }

        assertEquals(expectedModel, model);
    }

    /**
     * Asserts that the result display shows all the {@code expectedCommands} upon the execution of
     * {@code HistoryCommand}.
     */
    private void assertHistoryCorrect(String... expectedCommands) {
        try {
            CommandResult result = logic.execute(HistoryCommand.COMMAND_WORD);
            String expectedMessage = String.format(
                    HistoryCommand.MESSAGE_SUCCESS, String.join("\n", expectedCommands));
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (ParseException | CommandException e) {
            throw new AssertionError("Parsing and execution of HistoryCommand.COMMAND_WORD should succeed.", e);
        }
    }
}
