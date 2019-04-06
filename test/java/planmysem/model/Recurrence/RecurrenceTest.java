package planmysem.model.Recurrence;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import planmysem.common.Clock;
import planmysem.model.recurrence.Recurrence;
import planmysem.model.semester.Semester;


public class RecurrenceTest {

    private Semester semester;

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
        semester = Semester.generateSemester(LocalDate.now(Clock.get()));
    }

    @Test
    public void generateDatesTest() {
        Recurrence all = new Recurrence(new HashSet<>(
                Arrays.asList(
                        "normal",
                        "recess",
                        "reading",
                        "exam",
                        "past"
                )), 1);
        assertEquals(all.generateDates(semester).size(), 17);

        Clock.set("2019-05-12T10:00:00Z");
        Recurrence noPast = new Recurrence(new HashSet<>(
                Arrays.asList(
                        "normal",
                        "recess",
                        "reading",
                        "exam"
                )), 2);
        assertEquals(noPast.generateDates(semester).size(), 0);

        Clock.set("2019-01-14T10:00:00Z");
        Recurrence futureOnly = new Recurrence(new HashSet<>(
                Arrays.asList(
                        "normal",
                        "recess",
                        "reading",
                        "exam"
                )), 2);
        assertEquals(futureOnly.generateDates(semester).size(), 17);
    }

    @Test
    public void getDateTest() {
        Recurrence all = new Recurrence(new HashSet<>(
                Arrays.asList(
                        "normal",
                        "recess",
                        "reading",
                        "exam",
                        "past"
                )), 1);
        assertEquals(all.getDate(), LocalDate.of(2019, 1, 21));
    }

    @Test
    public void equals() {
        Recurrence all = new Recurrence(new HashSet<>(
                Arrays.asList(
                        "normal",
                        "recess",
                        "reading",
                        "exam",
                        "past"
                )), 1);

        Recurrence allCopy = new Recurrence(new HashSet<>(
                Arrays.asList(
                        "normal",
                        "recess",
                        "reading",
                        "exam",
                        "past"
                )), 1);
        assertEquals(all, allCopy);
        assertEquals(all.hashCode(), allCopy.hashCode());
    }
}
