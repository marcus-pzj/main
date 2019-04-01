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
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair1;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair2;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair3;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair4;
    private CommandHistory commandHistory = new CommandHistory();

    private SlotBuilder slotBuilder = new SlotBuilder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        Clock.set("2019-01-14T10:00:00Z");

        // Create typical planner
        model = new ModelManager();
        pair1 = new Pair<>(
                LocalDate.of(2019, 02, 01),
                new Pair<>(
                        new Day(
                                DayOfWeek.FRIDAY,
                                "Week 3"
                        ),
                        slotBuilder.slotOne()
                )
        );
        pair2 = new Pair<>(
                LocalDate.of(2019, 02, 02),
                new Pair<>(
                        new Day(
                                DayOfWeek.SATURDAY,
                                "Week 3"
                        ),
                        slotBuilder.slotOne()
                )
        );
        pair3 = new Pair<>(
                LocalDate.of(2019, 02, 03),
                new Pair<>(
                        new Day(
                                DayOfWeek.SUNDAY,
                                "Week 3"
                        ),
                        slotBuilder.slotOne()
                )
        );
        pair4 = new Pair<>(
                LocalDate.of(2019, 02, 04),
                new Pair<>(
                        new Day(
                                DayOfWeek.MONDAY,
                                "Week 4"
                        ),
                        slotBuilder.slotOne()
                )
        );
        model.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.slotOne());
        model.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.slotOne());
        model.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.slotOne());
        model.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.slotOne());
        model.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.slotOne());

        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> list = new TreeMap<>();
        list.put(pair4.getKey(), pair4.getValue());
        list.put(pair3.getKey(), pair3.getValue());
        list.put(pair2.getKey(), pair2.getValue());
        list.put(pair2.getKey(), pair2.getValue());
        list.put(pair1.getKey(), pair1.getValue());
        model.setLastShownList(list);

        expectedModel = new ModelManager();
        expectedModel.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.slotOne());
        expectedModel.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.slotOne());
        expectedModel.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.slotOne());
        expectedModel.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.slotOne());
        expectedModel.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.slotOne());
        expectedModel.setLastShownList(model.getLastShownList());
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
