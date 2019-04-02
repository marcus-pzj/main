package planmysem.logic.Commands;

import static junit.framework.TestCase.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.util.Pair;
import planmysem.common.Clock;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.ViewCommand;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.testutil.SlotBuilder;

public class ViewCommandTest {
    private Model model;
    private Model expectedModel;
    private CommandHistory commandHistory = new CommandHistory();

    private SlotBuilder slotBuilder = new SlotBuilder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        Clock.set("2020-02-01T10:00:00Z");

        // Create typical planner
        model = new ModelManager();
        model.addSlot(LocalDate.of(2020, 02, 01), slotBuilder.slotOne());
        model.addSlot(LocalDate.of(2020, 02, 01), slotBuilder.generateSlot(1));
        model.addSlot(LocalDate.of(2020, 02, 02), slotBuilder.slotOne());
        model.addSlot(LocalDate.of(2020, 02, 02), slotBuilder.slotOne());
        model.addSlot(LocalDate.of(2020, 02, 03), slotBuilder.slotOne());
        model.addSlot(LocalDate.of(2020, 02, 04), slotBuilder.slotOne());

        expectedModel = new ModelManager();
        expectedModel.addSlot(LocalDate.of(2020, 02, 01), slotBuilder.slotOne());
        expectedModel.addSlot(LocalDate.of(2020, 02, 01), slotBuilder.generateSlot(1));
        expectedModel.addSlot(LocalDate.of(2020, 02, 02), slotBuilder.slotOne());
        expectedModel.addSlot(LocalDate.of(2020, 02, 02), slotBuilder.slotOne());
        expectedModel.addSlot(LocalDate.of(2020, 02, 03), slotBuilder.slotOne());
        expectedModel.addSlot(LocalDate.of(2020, 02, 04), slotBuilder.slotOne());
    }

    @Test
    public void execute_displayMonthView_success() {
        ViewCommand expectedCommand = new ViewCommand(new String[]{"month"});
        CommandResult expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        ViewCommand actualCommand = new ViewCommand(new String[]{"month"});
        CommandResult actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());
    }

    @Test
    public void execute_displayWeekView_success() {
        ViewCommand expectedCommand = new ViewCommand(new String[]{"week"});
        CommandResult expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        ViewCommand actualCommand = new ViewCommand(new String[]{"week"});
        CommandResult actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());

        expectedCommand = new ViewCommand(new String[]{"week", "Examination"});
        expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        actualCommand = new ViewCommand(new String[]{"week", "Examination"});
        actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());

        expectedCommand = new ViewCommand(new String[]{"week", "3"});
        expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        actualCommand = new ViewCommand(new String[]{"week", "3"});
        actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());
    }

    @Test
    public void execute_displayDetailedWeekView_success() {
        ViewCommand expectedCommand = new ViewCommand(new String[]{"week", "Details"});
        CommandResult expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        ViewCommand actualCommand = new ViewCommand(new String[]{"week", "Details"});
        CommandResult actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());

        expectedCommand = new ViewCommand(new String[]{"week", "Examination", "Details"});
        expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        actualCommand = new ViewCommand(new String[]{"week", "Examination", "Details"});
        actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());

        expectedCommand = new ViewCommand(new String[]{"week", "3", "Details"});
        expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        actualCommand = new ViewCommand(new String[]{"week", "3", "Details"});
        actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());
    }

    @Test
    public void execute_displayDayView_success() {
        ViewCommand expectedCommand = new ViewCommand(new String[]{"day"});
        CommandResult expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        ViewCommand actualCommand = new ViewCommand(new String[]{"day"});
        CommandResult actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());

        expectedCommand = new ViewCommand(new String[]{"day", "1"});
        expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        actualCommand = new ViewCommand(new String[]{"day", "1"});
        actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());

        expectedCommand = new ViewCommand(new String[]{"day", "02-02"});
        expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        actualCommand = new ViewCommand(new String[]{"day", "02-02"});
        actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());
    }

    @Test
    public void execute_displayDayView_failure() {
        ViewCommand expectedCommand = new ViewCommand(new String[]{"day", "29-02"});
        CommandResult expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        ViewCommand actualCommand = new ViewCommand(new String[]{"day", "29-02"});
        CommandResult actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());

        expectedCommand = new ViewCommand(new String[]{"day", "01-01"});
        expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        actualCommand = new ViewCommand(new String[]{"day", "01-01"});
        actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());

        expectedCommand = new ViewCommand(new String[]{"day", "0"});
        expectedCommandResult = expectedCommand.execute(expectedModel, commandHistory);

        actualCommand = new ViewCommand(new String[]{"day", "0"});
        actualCommandResult = actualCommand.execute(model, commandHistory);

        assertEquals(expectedCommandResult.getFeedbackToUser(), actualCommandResult.getFeedbackToUser());
    }
}
