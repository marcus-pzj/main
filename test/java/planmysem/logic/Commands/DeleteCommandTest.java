package planmysem.logic.Commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static planmysem.common.Messages.MESSAGE_INVALID_SLOT_DISPLAYED_INDEX;
import static planmysem.logic.Commands.CommandTestUtil.assertCommandFailure;
import static planmysem.logic.Commands.CommandTestUtil.assertCommandSuccess;
import static planmysem.logic.commands.DeleteCommand.MESSAGE_SLOT_NOT_IN_PLANNER;
import static planmysem.logic.commands.DeleteCommand.MESSAGE_SUCCESS;
import static planmysem.logic.commands.DeleteCommand.MESSAGE_SUCCESS_NO_CHANGE;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.common.Messages;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.DeleteCommand;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.testutil.SlotBuilder;

public class DeleteCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Model model;
    private Model expectedModel;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair1;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair2;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair3;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair4;
    private CommandHistory commandHistory = new CommandHistory();
    private SlotBuilder slotBuilder = new SlotBuilder();

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
                        slotBuilder.generateSlot(1)
                )
        );
        pair2 = new Pair<>(
                LocalDate.of(2019, 02, 02),
                new Pair<>(
                        new Day(
                                DayOfWeek.SATURDAY,
                                "Week 3"
                        ),
                        slotBuilder.generateSlot(2)
                )
        );
        pair3 = new Pair<>(
                LocalDate.of(2019, 02, 03),
                new Pair<>(
                        new Day(
                                DayOfWeek.SUNDAY,
                                "Week 3"
                        ),
                        slotBuilder.generateSlot(3)
                )
        );
        pair4 = new Pair<>(
                LocalDate.of(2019, 02, 04),
                new Pair<>(
                        new Day(
                                DayOfWeek.MONDAY,
                                "Week 4"
                        ),
                        slotBuilder.generateSlot(3)
                )
        );
        model.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        model.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        model.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        model.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));

        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> list = new HashMap<>();
        list.put(pair1.getKey(), pair1.getValue());
        list.put(pair2.getKey(), pair2.getValue());
        list.put(pair3.getKey(), pair3.getValue());
        list.put(pair4.getKey(), pair4.getValue());
        model.setLastShownList(list);

        expectedModel = new ModelManager();
        expectedModel.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        expectedModel.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        expectedModel.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        expectedModel.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));
        expectedModel.setLastShownList(model.getLastShownList());

    }

    @Test
    public void constructor_nullSlotRecursion_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new DeleteCommand(null);
    }

    @Test
    public void execute_validTag_success() {
        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();
        selectedSlots.put(pair3.getKey(), pair3.getValue());
        selectedSlots.put(pair4.getKey(), pair4.getValue());
        Set<String> tags = pair4.getValue().getValue().getTags();
        DeleteCommand deleteCommand = new DeleteCommand(tags);

        String expectedMessage = String.format(MESSAGE_SUCCESS,
                2, Messages.craftSelectedMessage(tags),
                Messages.craftSelectedMessage("Deleted Slots:", selectedSlots));

        expectedModel.removeSlot(pair3);
        expectedModel.removeSlot(pair4);
        expectedModel.commit();

        assertCommandSuccess(deleteCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndex_success() {
        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();
        Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slot = model.getLastShownItem(1);
        selectedSlots.put(slot.getKey(), slot.getValue());
        DeleteCommand deleteCommand = new DeleteCommand(1);

        String expectedMessage = String.format(MESSAGE_SUCCESS,
                1, Messages.craftSelectedMessage(1),
                Messages.craftSelectedMessage("Deleted Slot:", selectedSlots));

        expectedModel.removeSlot(slot);
        expectedModel.commit();

        assertCommandSuccess(deleteCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidTag_throwsCommandException() {
        Set<String> tags = pair4.getValue().getValue().getTags();
        DeleteCommand deleteCommand = new DeleteCommand(tags);

        String expectedMessage = String.format(MESSAGE_SUCCESS_NO_CHANGE,
                Messages.craftSelectedMessage(tags));

        // removed slots with the valid tags, so the exception will occur
        model.removeSlot(pair3);
        model.removeSlot(pair4);

        assertCommandFailure(deleteCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_invalidSlot_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(1);

        String expectedMessage = MESSAGE_SLOT_NOT_IN_PLANNER;

        // removed slots with of index 1 in lastShownSlot, so the exception will occur
        model.removeSlot(pair4);

        assertCommandFailure(deleteCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(5);
        String expectedMessage = MESSAGE_INVALID_SLOT_DISPLAYED_INDEX;

        assertCommandFailure(deleteCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(1);

        // same object -> returns true
        assertEquals(deleteFirstCommand, deleteFirstCommand);

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(1);
        assertEquals(deleteFirstCommand, deleteFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(deleteFirstCommand, 1);

        // null -> returns false
        assertNotEquals(deleteFirstCommand, null);

        // different command -> returns false
        DeleteCommand deleteSecondCommand = new DeleteCommand(2);
        assertNotEquals(deleteFirstCommand, deleteSecondCommand);
    }
}
