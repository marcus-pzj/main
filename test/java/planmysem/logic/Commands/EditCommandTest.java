package planmysem.logic.Commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static planmysem.logic.Commands.CommandTestUtil.assertCommandFailure;
import static planmysem.logic.Commands.CommandTestUtil.assertCommandSuccess;
import static planmysem.logic.commands.EditCommand.MESSAGE_SUCCESS;
import static planmysem.logic.commands.EditCommand.MESSAGE_SUCCESS_NO_CHANGE;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.common.Messages;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.EditCommand;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.testutil.SlotBuilder;

public class EditCommandTest {
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

        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> list = new ArrayList<>();
        list.add(new Pair<>(pair1.getKey(), pair1.getValue()));
        list.add(new Pair<>(pair2.getKey(), pair2.getValue()));
        list.add(new Pair<>(pair3.getKey(), pair3.getValue()));
        list.add(new Pair<>(pair4.getKey(), pair4.getValue()));
        model.setLastShownList(list);

        expectedModel = new ModelManager();
        expectedModel.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        expectedModel.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        expectedModel.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        expectedModel.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));
        expectedModel.setLastShownList(model.getLastShownList());

    }

    @Test
    public void execute_validTag_success() {
        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();
        selectedSlots.add(new Pair<>(pair4.getKey(), pair4.getValue()));
        selectedSlots.add(new Pair<>(pair3.getKey(), pair3.getValue()));
        Set<String> selectTags = pair3.getValue().getValue().getTags();

        // values to edit
        String name = "new name";
        String location = "new location";
        String description = "new description";
        LocalTime startTime = LocalTime.of(8, 0);
        int duration = 60;
        Set<String> tags = new HashSet<>(Collections.singletonList("tag1"));

        EditCommand editCommand = new EditCommand(
                name,
                startTime,
                duration,
                location,
                description,
                selectTags,
                tags
        );

        String messageSelected = Messages.craftSelectedMessage(selectTags);
        String messageSlots = editCommand.craftSuccessMessage(selectedSlots);

        String expectedMessage = String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                messageSelected, messageSlots);

        expectedModel.editSlot(
                pair3.getKey(),
                pair3.getValue().getValue(),
                null,
                startTime,
                duration,
                name,
                location,
                description,
                tags
        );

        expectedModel.editSlot(
                pair4.getKey(),
                pair4.getValue().getValue(),
                null,
                startTime,
                duration,
                name,
                location,
                description,
                tags
        );
        expectedModel.commit();
        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidTag_throwsCommandException() {
        Set<String> selectTags = new HashSet<>(Collections.singletonList("tag does not exist"));

        // values to edit
        String name = "new name";
        String location = "new location";
        String description = "new description";
        LocalTime startTime = LocalTime.of(8, 0);
        int duration = 60;
        Set<String> tags = new HashSet<>(Collections.singletonList("tag1"));

        EditCommand editCommand = new EditCommand(
                name,
                startTime,
                duration,
                location,
                description,
                selectTags,
                tags
        );

        String expectedMessage = String.format(MESSAGE_SUCCESS_NO_CHANGE,
                Messages.craftSelectedMessage(selectTags));

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        // values to edit
        String name = "new name";
        String location = "new location";
        String description = "new description";
        LocalDate date = LocalDate.of(2019, 2, 2);
        LocalTime startTime = LocalTime.of(8, 0);
        int duration = 60;
        Set<String> tags = new HashSet<>(Collections.singletonList("tag1"));

        EditCommand editCommand = new EditCommand(
                5,
                name,
                date,
                startTime,
                duration,
                location,
                description,
                tags
        );

        String expectedMessage = Messages.MESSAGE_INVALID_SLOT_DISPLAYED_INDEX;

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_validIndex_success() {
        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();
        Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slot = model.getLastShownItem(1);
        selectedSlots.add(slot);

        // values to edit
        String name = "new name";
        String location = "new location";
        String description = "new description";
        LocalDate date = LocalDate.of(2019, 2, 2);
        LocalTime startTime = LocalTime.of(8, 0);
        int duration = 60;
        Set<String> tags = new HashSet<>(Collections.singletonList("tag1"));

        EditCommand editCommand = new EditCommand(
                1,
                name,
                date,
                startTime,
                duration,
                location,
                description,
                tags
        );

        String messageSelected = Messages.craftSelectedMessage(1);
        String messageSlots = editCommand.craftSuccessMessage(selectedSlots);

        String expectedMessage = String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                messageSelected, messageSlots);

        expectedModel.editSlot(
                pair1.getKey(),
                pair1.getValue().getValue(),
                date,
                startTime,
                duration,
                name,
                location,
                description,
                tags
        );
        expectedModel.commit();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexEmptyValues_success() {
        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();
        Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slot = model.getLastShownItem(1);
        selectedSlots.add(new Pair<>(slot.getKey(), slot.getValue()));

        // values to edit
        String name = "";
        String location = "";
        String description = "";
        LocalDate date = LocalDate.of(2019, 2, 2);
        LocalTime startTime = LocalTime.of(8, 0);
        int duration = 60;
        Set<String> tags = new HashSet<>(Collections.singletonList("tag1"));

        EditCommand editCommand = new EditCommand(
                1,
                name,
                date,
                startTime,
                duration,
                location,
                description,
                tags
        );

        String messageSelected = Messages.craftSelectedMessage(1);
        String messageSlots = editCommand.craftSuccessMessage(selectedSlots);

        String expectedMessage = String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                messageSelected, messageSlots);

        expectedModel.editSlot(
                pair1.getKey(),
                pair1.getValue().getValue(),
                date,
                startTime,
                duration,
                name,
                location,
                description,
                tags
        );
        expectedModel.commit();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        EditCommand editFirstCommand = new EditCommand(
                1,
                "name",
                LocalDate.of(2019, 2, 2),
                LocalTime.of(8, 0),
                60,
                "location",
                "description",
                new HashSet<>(Collections.singletonList("tag1"))
        );

        // same object -> returns true
        assertEquals(editFirstCommand, editFirstCommand);

        // same values -> returns true
        EditCommand editFirstCommandCopy = new EditCommand(
                1,
                "name",
                LocalDate.of(2019, 2, 2),
                LocalTime.of(8, 0),
                60,
                "location",
                "description",
                new HashSet<>(Collections.singletonList("tag1"))
        );
        assertEquals(editFirstCommand, editFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(editFirstCommand, 1);

        // null -> returns false
        assertNotEquals(editFirstCommand, null);

        // different command -> returns false
        EditCommand deleteSecondCommand = new EditCommand(
                2,
                "name",
                LocalDate.of(2019, 2, 2),
                LocalTime.of(8, 0),
                60,
                "location",
                "description",
                new HashSet<>(Collections.singletonList("tag1"))
        );
        assertNotEquals(editFirstCommand, deleteSecondCommand);
    }
}
