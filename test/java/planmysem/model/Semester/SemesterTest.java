package planmysem.model.Semester;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.model.semester.Day;
import planmysem.model.semester.Semester;
import planmysem.model.slot.Slot;
import planmysem.testutil.SlotBuilder;


public class SemesterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void initTest() {
        // sem 1
        Semester generatedSemester = Semester.generateSemester(LocalDate.of(2018, 8, 14));
        Semester expectedSemester = new Semester(generatedSemester);
        assertEquals(generatedSemester, expectedSemester);

        // vacation
        generatedSemester = Semester.generateSemester(LocalDate.of(2018, 12, 10));
        expectedSemester = new Semester(generatedSemester);
        assertEquals(generatedSemester, expectedSemester);

        // sem 2
        generatedSemester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        expectedSemester = new Semester(generatedSemester);
        assertEquals(generatedSemester, expectedSemester);

        // vacation
        generatedSemester = Semester.generateSemester(LocalDate.of(2019, 6, 13));
        expectedSemester = new Semester(generatedSemester);
        assertEquals(generatedSemester, expectedSemester);
    }

    @Test
    public void getSlotsTest() throws Exception {
        Semester semester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        assertEquals(semester.getSlots(new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))).size(),
                0);
        semester.addSlot(LocalDate.of(2019, 1, 14),
                new SlotBuilder().slotOne());
        assertEquals(semester.getSlots(new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))).size(),
                1);
        semester.addSlot(LocalDate.of(2019, 1, 15),
                new SlotBuilder().slotOne());
        assertEquals(semester.getSlots(new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))).size(),
                2);
    }

    @Test
    public void addSlotTest() throws Exception {
        Semester semester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        Slot slot = new SlotBuilder().slotOne();
        Day day = semester.addSlot(LocalDate.of(2019, 1, 14), slot);

        Day expectedDay = new Day(DayOfWeek.MONDAY, "Week 1");
        expectedDay.addSlot(slot);
        assertEquals(day, expectedDay);
    }


    @Test
    public void addSlotTest_throwsDateNotFoundException() throws Exception {
        Semester semester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        Slot slot = new SlotBuilder().slotOne();

        // null date
        thrown.expect(Semester.DateNotFoundException.class);
        semester.addSlot(null, slot);

        // add before start date
        thrown.expect(Semester.DateNotFoundException.class);
        semester.addSlot(LocalDate.of(2019, 1, 12), slot);

        // add after end date
        thrown.expect(Semester.DateNotFoundException.class);
        semester.addSlot(LocalDate.of(2020, 1, 12), slot);
    }

    @Test
    public void execute_generateSemester() {
        TestDataHelper helper = new TestDataHelper();

        // Assert Semester One generation
        Semester generatedSemester = Semester.generateSemester(LocalDate.of(2018, 8, 6));
        Semester expectedSemester = helper.generateSemesterFromDate(LocalDate.of(2018, 8, 6), "Sem 1");
        assertEquals(generatedSemester, expectedSemester);

        expectedSemester = Semester.generateSemester(LocalDate.of(2018, 10, 6));
        assertEquals(generatedSemester, expectedSemester);

        // Assert Semester Two generation
        generatedSemester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        LocalDate semTwoDate = LocalDate.of(2019, 1, 14);
        expectedSemester = helper.generateSemesterFromDate(semTwoDate, "Sem 2");
        assertEquals(generatedSemester, expectedSemester);

        expectedSemester = Semester.generateSemester(LocalDate.of(2019, 3, 17));
        assertEquals(generatedSemester, expectedSemester);
    }

    @Test
    public void containsSlotTest() throws Exception {
        Semester semester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        Slot slot = new SlotBuilder().slotOne();
        semester.addSlot(LocalDate.of(2019, 1, 14),
                slot);
        assertTrue(semester.contains(LocalDate.of(2019, 1, 14), slot));
    }

    @Test
    public void containsDayTest() {
        Semester semester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        Day day = new Day(DayOfWeek.MONDAY, "Week 1");
        assertTrue(semester.contains(day));
        assertTrue(semester.contains(LocalDate.of(2019, 1, 14)));
        assertFalse(semester.contains(LocalDate.of(2019, 1, 13)));
    }

    @Test
    public void getAcadCal() {
        Semester semester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        Semester anotherSemester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        assertEquals(semester.getAcadCal(), anotherSemester.getAcadCal());
    }

    @Test
    public void equalsTest() {
        assertEquals(Semester.generateSemester(LocalDate.now(Clock.get())),
                Semester.generateSemester(LocalDate.now(Clock.get())));
        assertNotEquals(Semester.generateSemester(LocalDate.now(Clock.get())),
                Semester.generateSemester(LocalDate.of(1999, 1, 1)));

        // test hashcode
        assertEquals(Semester.generateSemester(LocalDate.now(Clock.get())).hashCode(),
                Semester.generateSemester(LocalDate.now(Clock.get())).hashCode());
        assertNotEquals(Semester.generateSemester(LocalDate.now(Clock.get())).hashCode(),
                Semester.generateSemester(LocalDate.of(1999, 1, 1)).hashCode());
    }

    /**
     * A utility class to generate test model.
     */
    public class TestDataHelper {

        /**
         * Generates a Semester from the given date
         *
         * @param startDate given date which the semester should start from
         * @param acadSem the semester of the academic year
         * @return a Semester object from a specified date
         */
        Semester generateSemesterFromDate(LocalDate startDate, String acadSem) {
            String acadYear = null;
            LocalDate endDate = LocalDate.now(Clock.get());
            int givenYear = startDate.getYear();
            int weekOfStartDate = startDate.get(WeekFields.ISO.weekOfWeekBasedYear());
            int noOfWeeks = 0;
            HashMap<Integer, String> weekNames = new HashMap<>();
            HashMap<LocalDate, Day> days = new HashMap<>();
            Set<LocalDate> recessDays = new HashSet<>();
            Set<LocalDate> readingDays = new HashSet<>();
            Set<LocalDate> normalDays = new HashSet<>();
            Set<LocalDate> examDays = new HashSet<>();

            if ("Sem 1".equals(acadSem)) {
                noOfWeeks = 18;
                acadYear = "AY" + givenYear + "/" + (givenYear + 1);
                endDate = startDate.with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfStartDate + 18 - 1);
                endDate = endDate.with(WeekFields.ISO.dayOfWeek(), 7);

                weekNames.put(weekOfStartDate, "Orientation Week");
                int week = 1;
                for (int i = weekOfStartDate + 1; i < weekOfStartDate + 7; i++) {
                    weekNames.put(i, "Week " + week);
                    week++;
                }
                weekNames.put(weekOfStartDate + 7, "Recess Week");
                week = 7;
                for (int i = weekOfStartDate + 8; i < weekOfStartDate + 15; i++) {
                    weekNames.put(i, "Week " + week);
                    week++;
                }
                weekNames.put(weekOfStartDate + 15, "Reading Week");
                weekNames.put(weekOfStartDate + 16, "Examination Week");
                weekNames.put(weekOfStartDate + 17, "Examination Week");
            } else if ("Sem 2".equals(acadSem)) {
                noOfWeeks = 17;
                acadYear = "AY" + (givenYear - 1) + "/" + givenYear;
                endDate = startDate.with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfStartDate + 17 - 1);
                endDate = endDate.with(WeekFields.ISO.dayOfWeek(), 7);

                int week = 1;
                for (int i = weekOfStartDate; i < weekOfStartDate + 6; i++) {
                    weekNames.put(i, "Week " + week);
                    week++;
                }
                weekNames.put(weekOfStartDate + 6, "Recess Week");
                week = 7;
                for (int i = weekOfStartDate + 7; i < weekOfStartDate + 14; i++) {
                    weekNames.put(i, "Week " + week);
                    week++;
                }
                weekNames.put(weekOfStartDate + 14, "Reading Week");
                weekNames.put(weekOfStartDate + 15, "Examination Week");
                weekNames.put(weekOfStartDate + 16, "Examination Week");
            }

            // Initialises HashMap and Sets of all days in current semester
            List<LocalDate> datesList = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
            for (LocalDate date: datesList) {
                int weekOfYear = date.get(WeekFields.ISO.weekOfWeekBasedYear());
                String weekType = weekNames.get(weekOfYear);
                days.put(date, new Day(date.getDayOfWeek(), weekType));
                switch (weekType) {
                    case "Recess Week":
                        recessDays.add(date);
                        break;
                    case "Reading Week":
                        readingDays.add(date);
                        break;
                    case "Examination Week":
                        examDays.add(date);
                        break;
                    default:
                        normalDays.add(date);
                        break;
                }
            }

            return new Semester(acadSem, acadYear, days, startDate, endDate, noOfWeeks,
                    recessDays, readingDays, normalDays, examDays);
        }
    }
}
