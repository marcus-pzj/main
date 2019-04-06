package planmysem.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.model.semester.Semester;
import planmysem.model.slot.Slot;
import planmysem.testutil.SlotBuilder;

public class VersionedPlannerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
    public void initTest() {
        Planner planner = new Planner();
        VersionedPlanner versionedPlanner = new VersionedPlanner(planner);
        assertEquals(versionedPlanner.getSemester(), planner.getSemester());
    }

    @Test
    public void undo() throws Semester.DateNotFoundException {
        VersionedPlanner versionedPlanner = new VersionedPlanner(new Planner());
        Slot slot = new SlotBuilder().slotOne();
        LocalDate date = LocalDate.of(2019, 1, 15);
        versionedPlanner.addSlot(date, slot);
        versionedPlanner.commit();
        versionedPlanner.undo();

        VersionedPlanner expectedPlanner = new VersionedPlanner(new Planner());

        assertEquals(versionedPlanner.getSemester(), expectedPlanner.getSemester());
    }

    @Test
    public void undo_throwsNoUndoableStateException() {
        VersionedPlanner versionedPlanner = new VersionedPlanner(new Planner());

        thrown.expect(VersionedPlanner.NoUndoableStateException.class);
        versionedPlanner.undo();
    }

    @Test
    public void redo() throws Semester.DateNotFoundException {
        VersionedPlanner versionedPlanner = new VersionedPlanner(new Planner());
        Slot slot = new SlotBuilder().slotOne();
        LocalDate date = LocalDate.of(2019, 1, 15);
        versionedPlanner.addSlot(date, slot);
        versionedPlanner.commit();
        versionedPlanner.undo();
        versionedPlanner.redo();

        VersionedPlanner expectedPlanner = new VersionedPlanner(new Planner());
        expectedPlanner.addSlot(date, slot);

        assertEquals(versionedPlanner.getSemester(), expectedPlanner.getSemester());
    }

    @Test
    public void redo_throwsNoRedoableStateException() {
        VersionedPlanner versionedPlanner = new VersionedPlanner(new Planner());

        thrown.expect(VersionedPlanner.NoRedoableStateException.class);
        versionedPlanner.redo();
    }

    @Test
    public void equals() {
        VersionedPlanner versionedPlanner = new VersionedPlanner(new Planner());
        VersionedPlanner expectedPlanner = new VersionedPlanner(new Planner());

        // equals same object
        assertEquals(versionedPlanner, versionedPlanner);
        assertEquals(versionedPlanner.hashCode(), versionedPlanner.hashCode());

        // equals null
        assertNotEquals(versionedPlanner, null);

        // different objects same values
        assertEquals(versionedPlanner, expectedPlanner);
        assertEquals(versionedPlanner.hashCode(), expectedPlanner.hashCode());
    }
}
