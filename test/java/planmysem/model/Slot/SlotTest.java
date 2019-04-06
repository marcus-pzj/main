package planmysem.model.Slot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import planmysem.model.slot.Slot;
import planmysem.testutil.SlotBuilder;


public class SlotTest {
    private Slot slot;
    private Slot slotNull;

    @Before
    public void setup() {
        slot = new SlotBuilder().slotOne();
        slotNull = new SlotBuilder().slotNull();
    }

    @Test
    public void getName() {
        assertEquals(slot.getName(), slot.getName());
        assertNotEquals(slot.getName(), slotNull.getName());
    }

    @Test
    public void getLocation() {
        assertEquals(slot.getLocation(), slot.getLocation());
        assertNull(slotNull.getLocation());
        assertNotEquals(slot.getLocation(), slotNull.getLocation());
    }

    @Test
    public void getDescription() {
        assertEquals(slot.getDescription(), slot.getDescription());
        assertNull(slotNull.getDescription());
        assertNotEquals(slot.getDescription(), slotNull.getDescription());
    }

    @Test
    public void getDuration() {
        assertEquals(slot.getDuration(), slot.getDuration());
        assertEquals(slotNull.getDuration(), 0);
        assertNotEquals(slot.getDuration(), 0);
    }

    @Test
    public void getStartTime() {
        assertEquals(slot.getStartTime(), slot.getStartTime());
        assertNotEquals(slot.getStartTime(), slotNull.getStartTime());
    }

    @Test
    public void getTags() {
        assertEquals(slot.getTags(), slot.getTags());
        assertTrue(slotNull.getTags().isEmpty());
        assertNotEquals(slot.getTags(), slotNull.getTags());
    }

    @Test
    public void setName() {
        assertNotEquals(slot.getName(), "test");
        slot.setName("test");
        assertEquals(slot.getName(), "test");
        slot.setName(null);
        assertEquals(slot.getName(), "test");

    }

    @Test
    public void setLocation() {
        assertNotEquals(slot.getLocation(), "test");
        slot.setLocation("test");
        assertEquals(slot.getLocation(), "test");
        slot.setLocation(null);
        assertEquals(slot.getLocation(), "test");

    }

    @Test
    public void setDescription() {
        assertNotEquals(slot.getDescription(), "test");
        slot.setDescription("test");
        assertEquals(slot.getDescription(), "test");
        slot.setDescription(null);
        assertEquals(slot.getDescription(), "test");
    }

    @Test
    public void setStartTime() {
        assertNotEquals(slot.getStartTime(), slotNull.getStartTime());
        slot.setStartTime(LocalTime.MIN);
        assertEquals(slot.getStartTime(), LocalTime.MIN);
        slot.setStartTime(null);
        assertEquals(slot.getStartTime(), LocalTime.MIN);
    }

    @Test
    public void setTags() {
        assertEquals(slot.getTags(), slot.getTags());
        slot.setTags(new HashSet<>(Arrays.asList("test", "test2")));
        assertEquals(slot.getTags(), new HashSet<>(Arrays.asList("test", "test2")));
        slot.setTags(null);
        assertEquals(slot.getTags(), new HashSet<>(Arrays.asList("test", "test2")));
    }

    @Test
    public void setDuration() {
        assertEquals(slot.getDuration(), slot.getDuration());
        assertNotEquals(slot.getDuration(), 0);

        slot.setDuration(0);
        assertEquals(slot.getDuration(), slotNull.getDuration());
    }

    @Test
    public void equals() {
        assertEquals(slot, slot);
        assertEquals(slotNull, slotNull);
        assertEquals(slot.hashCode(), slot.hashCode());
        assertEquals(slotNull.hashCode(), slotNull.hashCode());

        assertNotEquals(slot, slotNull);
    }
}
