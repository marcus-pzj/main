package planmysem.model;

import static junit.framework.TestCase.assertEquals;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import planmysem.common.Clock;
import planmysem.model.semester.Day;
import planmysem.model.semester.Semester;


public class PlannerTest {

    @Test
    public void execute_generateSemester() {
        TestDataHelper helper = new TestDataHelper();
        Semester generatedSemester;
        Semester expectedSemester;

        // Assert Semester One generation
        generatedSemester = Semester.generateSemester(LocalDate.of(2018, 8, 6));
        LocalDate semOneDate = LocalDate.of(2018, 8, 6);
        expectedSemester = helper.generateSemesterFromDate(semOneDate, "Sem 1");
        assertSameSemester(generatedSemester, expectedSemester);

        expectedSemester = Semester.generateSemester(LocalDate.of(2018, 10, 6));
        assertSameSemester(generatedSemester, expectedSemester);

        // Assert Semester Two generation
        generatedSemester = Semester.generateSemester(LocalDate.of(2019, 1, 14));
        LocalDate semTwoDate = LocalDate.of(2019, 1, 14);
        expectedSemester = helper.generateSemesterFromDate(semTwoDate, "Sem 2");
        assertSameSemester(generatedSemester, expectedSemester);

        expectedSemester = Semester.generateSemester(LocalDate.of(2019, 3, 17));
        assertSameSemester(generatedSemester, expectedSemester);
    }

    /**
     * Asserts that the generated and expected Semester contents are equal.
     */
    private void assertSameSemester(Semester generatedSemester, Semester expectedSemester) {
        //Confirm the state of model is as expected
        assertEquals(generatedSemester.hashCode(), expectedSemester.hashCode());
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
                endDate = endDate.with(WeekFields.ISO.dayOfWeek(), 7).plusDays(1);

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
                endDate = endDate.with(WeekFields.ISO.dayOfWeek(), 7).plusDays(1);

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
            List<LocalDate> datesList = startDate.datesUntil(endDate).collect(Collectors.toList());
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
