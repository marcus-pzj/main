package planmysem.common;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static planmysem.common.Utils.getDuration;
import static planmysem.common.Utils.getEndTime;
import static planmysem.common.Utils.getNearestDayOfWeek;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.tag.Tag;

public class UtilsTest {
    @Test
    public void isAnyNull() {
        // empty list
        assertFalse(Utils.isAnyNull());

        // Any non-empty list
        assertFalse(Utils.isAnyNull(new Object(), new Object()));
        assertFalse(Utils.isAnyNull("test"));
        assertFalse(Utils.isAnyNull(""));

        // non empty list with just one null at the beginning
        assertTrue(Utils.isAnyNull((Object) null));
        assertTrue(Utils.isAnyNull(null, "", new Object()));
        assertTrue(Utils.isAnyNull(null, new Object(), new Object()));

        // non empty list with nulls in the middle
        assertTrue(Utils.isAnyNull(new Object(), null, null, "test"));
        assertTrue(Utils.isAnyNull("", null, new Object()));

        // non empty list with one null as the last element
        assertTrue(Utils.isAnyNull("", new Object(), null));
        assertTrue(Utils.isAnyNull(new Object(), new Object(), null));

        // confirms nulls inside the list are not considered
        List<Object> nullList = Arrays.asList((Object) null);
        assertFalse(Utils.isAnyNull(nullList));
    }

    @Test
    public void elementsAreUnique() {
        // empty list
        assertAreUnique();

        // only one object
        assertAreUnique((Object) null);
        assertAreUnique(1);
        assertAreUnique("");
        assertAreUnique("abc");

        // all objects unique
        assertAreUnique("abc", "ab", "a");
        assertAreUnique(1, 2);

        // some identical objects
        assertNotUnique("abc", "abc");
        assertNotUnique("abc", "", "abc", "ABC");
        assertNotUnique("", "abc", "a", "abc");
        assertNotUnique(1, Integer.valueOf(1));
        assertNotUnique(null, 1, Integer.valueOf(1));
        assertNotUnique(null, null);
        assertNotUnique(null, "a", "b", null);
    }

    @Test
    public void parse_day_successful() {
        assertEquals(Utils.parseDay("Monday"), 1);
        assertEquals(Utils.parseDay("monday "), 1);
        assertEquals(Utils.parseDay("Mon"), 1);
        assertEquals(Utils.parseDay("mon"), 1);
        assertEquals(Utils.parseDay("1"), 1);

        assertEquals(Utils.parseDay("Tuesday"), 2);
        assertEquals(Utils.parseDay("tuesday "), 2);
        assertEquals(Utils.parseDay("Tues"), 2);
        assertEquals(Utils.parseDay("tues"), 2);
        assertEquals(Utils.parseDay("2"), 2);

        assertEquals(Utils.parseDay("Wednesday"), 3);
        assertEquals(Utils.parseDay("Wed"), 3);
        assertEquals(Utils.parseDay("wed"), 3);
        assertEquals(Utils.parseDay("3"), 3);

        assertEquals(Utils.parseDay("Thursday"), 4);
        assertEquals(Utils.parseDay(" thursday"), 4);
        assertEquals(Utils.parseDay("Thurs"), 4);
        assertEquals(Utils.parseDay("thurs"), 4);
        assertEquals(Utils.parseDay("4"), 4);

        assertEquals(Utils.parseDay("Friday"), 5);
        assertEquals(Utils.parseDay(" friday"), 5);
        assertEquals(Utils.parseDay("Fri"), 5);
        assertEquals(Utils.parseDay("fri"), 5);
        assertEquals(Utils.parseDay("5"), 5);

        assertEquals(Utils.parseDay("Saturday"), 6);
        assertEquals(Utils.parseDay(" saturday"), 6);
        assertEquals(Utils.parseDay("Sat"), 6);
        assertEquals(Utils.parseDay("sat"), 6);
        assertEquals(Utils.parseDay("6"), 6);

        assertEquals(Utils.parseDay("Sunday"), 7);
        assertEquals(Utils.parseDay(" sunday"), 7);
        assertEquals(Utils.parseDay("Sun"), 7);
        assertEquals(Utils.parseDay("sun"), 7);
        assertEquals(Utils.parseDay("7"), 7);
    }

    @Test
    public void parse_day_unsuccessful() {
        assertEquals(Utils.parseDay("Mond"), -1);
        assertEquals(Utils.parseDay("Mo"), -1);
        assertEquals(Utils.parseDay("Fr"), -1);
        assertEquals(Utils.parseDay("8"), -1);
        assertEquals(Utils.parseDay("0"), -1);
    }

    @Test
    public void parse_time_successful() {
        assertEquals(Utils.parseTime("08:00"), LocalTime.of(8, 0));
        assertEquals(Utils.parseTime("8:00 PM"), LocalTime.of(20, 0));
        assertEquals(Utils.parseTime("14:00"), LocalTime.of(14, 0));
        assertEquals(Utils.parseTime("00:00"), LocalTime.of(0, 0));
        assertEquals(Utils.parseTime("8:00"), LocalTime.of(8, 0));
        assertEquals(Utils.parseTime("8:00 AM"), LocalTime.of(8, 0));
    }

    @Test
    public void parse_time_unsuccessful() {
        assertEquals(Utils.parseTime("8-00"), null);
        assertEquals(Utils.parseTime("8:00 am"), null);
        assertEquals(Utils.parseTime("8:00 pm"), null);
        assertEquals(Utils.parseTime("14:00 am"), null);
        assertEquals(Utils.parseTime("16:00 pm"), null);
        assertEquals(Utils.parseTime("24:00"), null);
    }

    @Test
    public void parse_integer_successful() {
        assertEquals(Utils.parseInteger("800"), 800);
        assertEquals(Utils.parseInteger("0"), 0);
        assertEquals(Utils.parseInteger("1"), 1);
        assertEquals(Utils.parseInteger("60"), 60);
        assertEquals(Utils.parseInteger("90"), 90);
        assertEquals(Utils.parseInteger("120"), 120);
    }

    @Test
    public void parse_integer_unsuccessful() {
        assertEquals(Utils.parseInteger("12 0"), -1);
        assertEquals(Utils.parseInteger("0.1"), -1);
        assertEquals(Utils.parseInteger("test"), -1);
        assertEquals(Utils.parseInteger("OO"), -1);
    }

    @Test
    public void parse_tags_successful() {
        List<String> listOfTag = new ArrayList<>();
        listOfTag.add("0");
        listOfTag.add("tag1");
        listOfTag.add("tag 2");
        listOfTag.add("tag 3 super long tag");

        Set<String> tagStrings = new HashSet<>(listOfTag);

        Set<Tag> expectedTags = new HashSet<>();
        Set<Tag> tags = new HashSet<>();
        try {
            expectedTags = new HashSet<>(Arrays.asList(new Tag("0"),
                    new Tag("tag1"), new Tag("tag 2"),
                    new Tag("tag 3 super long tag")));

            tags = Utils.parseTags(tagStrings);
        } catch (IllegalValueException ive) {
        }

        assertEquals(tags, expectedTags);

        try {
            tags = Utils.parseTags(null);
        } catch (IllegalValueException ive) {
        }

        assertEquals(tags, null);
    }

    @Test
    public void parse_tags_unsuccessful() {
        Set<Tag> tags = new HashSet<>();

        try {
            tags = Utils.parseTags(null);
        } catch (IllegalValueException ive) {
        }

        assertEquals(tags, null);
    }

    @Test
    public void parse_get_duration_successful() {
        LocalTime startTime = LocalTime.now();
        LocalTime endTime = startTime.plusMinutes(60);

        assertEquals(getDuration(startTime, endTime), 60);
    }

    @Test
    public void parse_get_end_time_successful() {
        LocalTime startTime = LocalTime.now();
        LocalTime endTime = startTime.plusMinutes(60);

        assertEquals(getEndTime(startTime, 60), endTime);
    }

    @Test
    public void parse_get_nearest_day_of_week_successful() {
        LocalDate date = LocalDate.of(2019, 1, 1);
        LocalDate nearestMonday = LocalDate.of(2019, 1, 7);

        assertEquals(getNearestDayOfWeek(date, 1), nearestMonday);
    }

    private void assertAreUnique(Object... objects) {
        assertTrue(Utils.elementsAreUnique(Arrays.asList(objects)));
    }

    private void assertNotUnique(Object... objects) {
        assertFalse(Utils.elementsAreUnique(Arrays.asList(objects)));
    }
}
