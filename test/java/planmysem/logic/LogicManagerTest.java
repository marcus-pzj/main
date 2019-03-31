package planmysem.logic;

import static org.junit.Assert.assertEquals;
import static planmysem.common.Messages.MESSAGE_INVALID_SLOT_DISPLAYED_INDEX;

import java.io.IOException;

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
import planmysem.storage.StorageFile;


public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy exception");
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
//
//    @Test
//    public void execute_storageThrowsIoException_throwsCommandException() throws Exception {
//        // Setup LogicManager with JsonAddressBookIoExceptionThrowingStub
//        JsonAddressBookStorage addressBookStorage =
//                new JsonAddressBookIoExceptionThrowingStub(temporaryFolder.newFile().toPath());
//        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.newFile().toPath());
//        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
//        logic = new LogicManager(model, storage);
//
//        // Execute add command
//        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY
//                + ADDRESS_DESC_AMY;
//        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
//        ModelManager expectedModel = new ModelManager();
//        expectedModel.addPerson(expectedPerson);
//        expectedModel.commitAddressBook();
//        String expectedMessage = LogicManager.FILE_OPS_ERROR_MESSAGE + DUMMY_IO_EXCEPTION;
//        assertCommandBehavior(CommandException.class, addCommand, expectedMessage, expectedModel);
//        assertHistoryCorrect(addCommand);
//    }
//
//    @Test
//    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
//        thrown.expect(UnsupportedOperationException.class);
//        logic.getFilteredPersonList().remove(0);
//    }

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
            assertEquals(expectedException, null);
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
