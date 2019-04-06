package planmysem.logic.Commands;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static planmysem.logic.commands.AddCommand.MESSAGE_SUCCESS;
import static planmysem.logic.commands.AddCommand.craftSuccessMessage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.AddCommand;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.model.Model;
import planmysem.model.Planner;
import planmysem.model.recurrence.Recurrence;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.semester.Semester;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;
import planmysem.testutil.SlotBuilder;


public class AddCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final CommandHistory EMPTY_COMMAND_HISTORY = new CommandHistory();

    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
    public void constructor_nullSlotRecursion_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new AddCommand(null, null);
    }

    @Test
    public void execute_slotAcceptedByModel_addSuccessful() throws CommandException {
        ModelStubAcceptingSlotAdded modelStub = new ModelStubAcceptingSlotAdded();
        Slot validSlot = new SlotBuilder().slotOne();
        Recurrence validRecurrence = new SlotBuilder().recurrenceOne();

        CommandResult commandResult = new AddCommand(validSlot, validRecurrence).execute(modelStub, commandHistory);

        Set<LocalDate> dates = new HashSet<>();
        dates.add(LocalDate.of(2019, 2, 1));

        Map<LocalDate, Day> days = new TreeMap<>();
        Day day = new Day(DayOfWeek.MONDAY, "type");
        day.addSlot(validSlot);
        days.put(LocalDate.of(2019, 2, 1), day);

        assertEquals(String.format(MESSAGE_SUCCESS, dates.size(),
                craftSuccessMessage(days, validSlot)), commandResult.getFeedbackToUser());
        assertEquals(days, modelStub.days);
        assertEquals(EMPTY_COMMAND_HISTORY, commandHistory);
    }

    @Test
    public void execute_invalidDate_throwsCommandException() throws Exception {
        ModelStubNeverSlotAdded modelStub = new ModelStubNeverSlotAdded();
        Slot validSlot = new SlotBuilder().slotOne();
        Recurrence validRecurrence = new SlotBuilder().recurrenceOne();

        AddCommand addCommand = new AddCommand(validSlot, validRecurrence);

        thrown.expect(CommandException.class);
        thrown.expectMessage(AddCommand.MESSAGE_FAIL_OUT_OF_BOUNDS);
        addCommand.execute(modelStub, commandHistory);
    }

    @Test
    public void equals() {
        Slot slot1 = new SlotBuilder().generateSlot(1);
        Recurrence recurrence = new SlotBuilder().recurrenceOne();

        AddCommand addCommand1 = new AddCommand(slot1, recurrence);

        // same object -> returns true
        assertEquals(addCommand1, addCommand1);

        // same values -> returns true
        AddCommand addCommand1Copy = new AddCommand(slot1, recurrence);
        assertEquals(addCommand1, addCommand1Copy);

        // different types -> returns false
        assertNotEquals(addCommand1, 1);

        // null -> returns false
        assertNotEquals(addCommand1, null);

        // different command -> returns false
        Slot slot2 = new SlotBuilder().generateSlot(2);
        AddCommand addCommand2 = new AddCommand(slot2, recurrence);
        assertNotEquals(addCommand1, addCommand2);
    }


    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getLastShownList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setLastShownList(List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> list) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setLastShownList(Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> list) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearLastShownList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void commit() {
        }

        @Override
        public Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> getLastShownItem(int index) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean slotExists(LocalDate date, ReadOnlySlot slot) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Day addSlot(LocalDate date, Slot slot) throws Semester.DateNotFoundException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeSlot(LocalDate date, ReadOnlySlot slot) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeSlot(Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slot) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void editSlot(LocalDate targetDate, ReadOnlySlot targetSlot, LocalDate date,
                             LocalTime startTime, int duration, String name, String location,
                             String description, Set<String> tags) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearSlots() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Planner getPlanner() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public HashMap<LocalDate, Day> getDays() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Day getDay(LocalDate date) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getSlots(Set<String> tags) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void undo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void redo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean equals(Object obj) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that always accept the slot being added.
     */
    private class ModelStubAcceptingSlotAdded extends ModelStub {
        private Map<LocalDate, Day> days = new TreeMap<>();

        @Override
        public Day addSlot(LocalDate date, Slot slot) {
            Day day = new Day(DayOfWeek.MONDAY, "type");
            day.addSlot(slot);

            days.put(date, day);

            return day;
        }

        @Override
        public Planner getPlanner() {
            return new Planner();
        }
    }

    /**
     * A Model stub that never accepts the slot being added.
     */
    private class ModelStubNeverSlotAdded extends ModelStub {

        @Override
        public Day addSlot(LocalDate date, Slot slot) throws Semester.DateNotFoundException {
            throw new Semester.DateNotFoundException();
        }

        @Override
        public Planner getPlanner() {
            return new Planner();
        }
    }
}
