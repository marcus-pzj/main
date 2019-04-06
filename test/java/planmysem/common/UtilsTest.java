package planmysem.common;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static planmysem.common.Utils.getDuration;
import static planmysem.common.Utils.getEndTime;
import static planmysem.common.Utils.getNearestDayOfWeek;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class UtilsTest {
    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

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
        List<Object> nullList = Collections.singletonList(null);
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
        assertNotUnique(1, 1);
        assertNotUnique(null, 1, 1);
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
        assertEquals(Utils.parseDay("Tue"), 2);
        assertEquals(Utils.parseDay("tue"), 2);
        assertEquals(Utils.parseDay("2"), 2);

        assertEquals(Utils.parseDay("Wednesday"), 3);
        assertEquals(Utils.parseDay("Wed"), 3);
        assertEquals(Utils.parseDay("wed"), 3);
        assertEquals(Utils.parseDay("3"), 3);

        assertEquals(Utils.parseDay("Thursday"), 4);
        assertEquals(Utils.parseDay(" thursday"), 4);
        assertEquals(Utils.parseDay("Thu"), 4);
        assertEquals(Utils.parseDay("thu"), 4);
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
        assertEquals(Utils.parseDay(""), -1);
    }

    @Test
    public void parse_date_successful() {
        assertEquals(Utils.parseDate("01-03-2019"), LocalDate.of(2019, 03, 01));
        assertEquals(Utils.parseDate("01-04-2019"), LocalDate.of(2019, 04, 01));
        assertEquals(Utils.parseDate("01-05-2019"), LocalDate.of(2019, 05, 01));
        assertEquals(Utils.parseDate("01-06-2019"), LocalDate.of(2019, 06, 01));
        assertEquals(Utils.parseDate("01-06"), LocalDate.of(2019, 06, 01));
    }

    @Test
    public void parse_date_unsuccessful() {
        assertNull(Utils.parseDate("00-06-2019"));
        assertNull(Utils.parseDate("01-13-2019"));
        assertNull(Utils.parseDate("v"));
    }

    @Test
    public void parse_time_successful() {
        assertEquals(Utils.parseTime("08:00"), LocalTime.of(8, 0));
        assertEquals(Utils.parseTime("8:00 PM"), LocalTime.of(20, 0));
        assertEquals(Utils.parseTime("14:00"), LocalTime.of(14, 0));
        assertEquals(Utils.parseTime("00:00"), LocalTime.of(0, 0));
        assertEquals(Utils.parseTime("8:00"), LocalTime.of(8, 0));
        assertEquals(Utils.parseTime("8:00 AM"), LocalTime.of(8, 0));
        assertEquals(Utils.parseTime("8:00 am"), LocalTime.of(8, 0));
        assertNull(Utils.parseTime(null));
    }

    @Test
    public void parse_time_unsuccessful() {
        assertNull(Utils.parseTime("14:00 am"));
        assertNull(Utils.parseTime("16:00 pm"));
        assertNull(Utils.parseTime("-00:20"));
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
        assertEquals(Utils.parseInteger("120000000000000"), -1);
        assertEquals(Utils.parseInteger("12 0"), -1);
        assertEquals(Utils.parseInteger("0.1"), -1);
        assertEquals(Utils.parseInteger("test"), -1);
        assertEquals(Utils.parseInteger("OO"), -1);
    }

    @Test
    public void parse_getDuration_successful() {
        LocalTime startTime = LocalTime.now(Clock.get());
        LocalTime endTime = startTime.plusMinutes(60);

        assertEquals(getDuration(startTime, endTime), 60);
    }

    @Test
    public void parse_getEndTime_successful() {
        LocalTime startTime = LocalTime.now(Clock.get());
        LocalTime endTime = startTime.plusMinutes(60);

        assertEquals(getEndTime(startTime, 60), endTime);
    }

    @Test
    public void parse_getNearestDayOfWeek_successful() {
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
