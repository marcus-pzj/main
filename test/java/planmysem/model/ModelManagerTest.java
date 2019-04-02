package planmysem.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.semester.Semester;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;
import planmysem.testutil.SlotBuilder;

public class ModelManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
    public void clearLastShownList() {
        ModelManager modelManager = new ModelManager();
        List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> lastShownSlots = new ArrayList<>();
        Day day = new Day(DayOfWeek.TUESDAY, "Week 1");
        lastShownSlots.add(new Pair<>(LocalDate.of(2019, 1, 15),
                new Pair<>(day, new SlotBuilder().slotOne())));

        modelManager.setLastShownList(lastShownSlots);
        assertEquals(modelManager.lastShownList, lastShownSlots);

        modelManager.clearLastShownList();
        assertEquals(modelManager.lastShownList, Collections.EMPTY_LIST);
    }

    @Test
    public void undo() throws Semester.DateNotFoundException {
        ModelManager modelManager = new ModelManager();
        Slot slot = new SlotBuilder().slotOne();
        LocalDate date = LocalDate.of(2019, 1, 15);
        modelManager.addSlot(date, slot);
        modelManager.commit();

        // test canUndo()
        assertTrue(modelManager.canUndo());

        modelManager.undo();

        ModelManager expectedModelManager = new ModelManager();

        assertEquals(modelManager.getPlanner().getSemester(),
                expectedModelManager.getPlanner().getSemester());
    }

    @Test
    public void redo() throws Semester.DateNotFoundException {
        ModelManager modelManager = new ModelManager();
        Slot slot = new SlotBuilder().slotOne();
        LocalDate date = LocalDate.of(2019, 1, 15);
        modelManager.addSlot(date, slot);
        modelManager.commit();
        modelManager.undo();

        // test canRedo()
        assertTrue(modelManager.canRedo());

        modelManager.redo();

        ModelManager expectedModelManager = new ModelManager();
        expectedModelManager.addSlot(date, slot);

        assertEquals(modelManager.getPlanner().getSemester(),
                expectedModelManager.getPlanner().getSemester());
    }

    @Test
    public void equals() {
        ModelManager modelManager = new ModelManager();
        ModelManager expectedModelManager = new ModelManager();

        // equals same object
        assertEquals(modelManager, modelManager);
        assertEquals(modelManager.hashCode(), modelManager.hashCode());

        // equals null
        assertNotEquals(modelManager, null);

        // different objects same values
        assertEquals(modelManager, expectedModelManager);
        assertEquals(modelManager.hashCode(), expectedModelManager.hashCode());
    }
}
