package planmysem.data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.util.Pair;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.semester.Day;
import planmysem.data.semester.ReadOnlyDay;
import planmysem.data.semester.Semester;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;
import planmysem.data.tag.Tag;

/**
 * Represents the entire Planner. Contains the data of the Planner.
 */
public class Planner {
    private final Semester semester;

    /**
     * Creates an empty planner.
     */
    public Planner() {
        semester = generateSemester(LocalDate.now());
    }

    /**
     * Constructs a Planner with the given data.
     *
     * @param semester external changes to this will not affect this Planner
     */
    public Planner(Semester semester) {
        this.semester = new Semester(semester);
    }

    /**
     * Generates current semester based on current date.
     * As long as the current date falls within a semester, the generated semester is always the same.
     *
     * @param currentDate the current date when the program is run
     * @return the current semester object
     */
    public static Semester generateSemester(LocalDate currentDate) {
        String acadSem;
        String acadYear;
        String[] semesterDetails;
        int noOfWeeks;
        LocalDate startDate;
        LocalDate endDate;
        List<LocalDate> datesList;
        HashMap<Integer, String> acadCalMap;
        HashMap<LocalDate, Day> days = new HashMap<>();
        Set<LocalDate> recessDays = new HashSet<>();
        Set<LocalDate> readingDays = new HashSet<>();
        Set<LocalDate> normalDays = new HashSet<>();
        Set<LocalDate> examDays = new HashSet<>();

        acadCalMap = generateAcadCalMap(currentDate);
        semesterDetails = getSemesterDetails(currentDate, acadCalMap);
        acadSem = semesterDetails[1];
        acadYear = semesterDetails[2];
        noOfWeeks = Integer.parseInt(semesterDetails[3]);
        startDate = LocalDate.parse(semesterDetails[4]);
        endDate = LocalDate.parse(semesterDetails[5]);

        // Initialise HashMap and Sets of all days in current semester
        datesList = startDate.datesUntil(endDate).collect(Collectors.toList());
        for (LocalDate date: datesList) {
            int weekOfYear = date.get(WeekFields.ISO.weekOfWeekBasedYear());
            String weekType = acadCalMap.get(weekOfYear).split("_")[0];
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

    /**
     * Generates academic calendar for a given date.
     *
     * @param date used to determine academic year
     * @return details of academic calendar
     */
    private static HashMap<Integer, String> generateAcadCalMap(LocalDate date) {
        HashMap<Integer, String> acadCalMap = new HashMap<>();
        LocalDate semOneStartDate = date;
        LocalDate semTwoEndDate = date;
        int currentMonth = date.getMonthValue();
        int currentYear = date.getYear();
        int semOneStartWeek;
        int semTwoStartWeek;
        int semTwoEndWeek;
        int acadWeekNo;
        int noOfWeeksInYear;
        int vacationWeekNo;

        if (currentMonth < 8) {
            // Academic Year beginning from August of previous year
            semOneStartDate = semOneStartDate.withYear(currentYear - 1).withMonth(8)
                    .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
            semOneStartWeek = semOneStartDate.get(WeekFields.ISO.weekOfWeekBasedYear());
            semTwoEndDate = semTwoEndDate.withYear(currentYear).withMonth(8)
                    .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).minusDays(1);
            semTwoEndWeek = semTwoEndDate.get(WeekFields.ISO.weekOfWeekBasedYear());
        } else {
            // Academic Year beginning from August of current year
            semOneStartDate = semOneStartDate.withYear(currentYear).withMonth(8)
                    .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
            semOneStartWeek = semOneStartDate.get(WeekFields.ISO.weekOfWeekBasedYear());
            semTwoEndDate = semTwoEndDate.withYear(currentYear + 1).withMonth(8)
                    .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).minusDays(1);
            semTwoEndWeek = semTwoEndDate.get(WeekFields.ISO.weekOfWeekBasedYear());
        }

        // Sem 1 - Orientation Week
        acadCalMap.put(semOneStartWeek, "Orientation Week_Sem 1");

        // Sem 1 - Week 1 to 6
        acadWeekNo = 1;
        for (int i = semOneStartWeek + 1; i < semOneStartWeek + 7; i++) {
            acadCalMap.put(i, "Week " + acadWeekNo + "_Sem 1");
            acadWeekNo++;
        }

        // Sem 1 - Recess Week
        acadCalMap.put(semOneStartWeek + 7, "Recess Week_Sem 1");

        // Sem 1 - Week 7 to 13
        acadWeekNo = 7;
        for (int i = semOneStartWeek + 8; i < semOneStartWeek + 15; i++) {
            acadCalMap.put(i, "Week " + acadWeekNo + "_Sem 1");
            acadWeekNo++;
        }

        // Sem 1 - Reading & Examination Weeks
        acadCalMap.put(semOneStartWeek + 15, "Reading Week_Sem 1");
        acadCalMap.put(semOneStartWeek + 16, "Examination Week_Sem 1");
        acadCalMap.put(semOneStartWeek + 17, "Examination Week_Sem 1");

        // Sem 1 - Vacation
        noOfWeeksInYear = (int) semOneStartDate.range(WeekFields.ISO.weekOfWeekBasedYear()).getMaximum();
        vacationWeekNo = semOneStartWeek + 18;
        semTwoStartWeek = 1;
        for (int i = 0; i < 5; i++) {
            if ((vacationWeekNo + i) <= noOfWeeksInYear) {
                acadCalMap.put(vacationWeekNo + i, "Vacation_Sem 1");
            } else {
                acadCalMap.put(semTwoStartWeek++, "Vacation_Sem 1");
            }
        }

        // Sem 2 - Week 1 to 6
        acadWeekNo = 1;
        for (int i = semTwoStartWeek; i < semTwoStartWeek + 6; i++) {
            acadCalMap.put(i, "Week " + acadWeekNo + "_Sem 2");
            acadWeekNo++;
        }

        // Sem 2 - Recess Week
        acadCalMap.put(semTwoStartWeek + 6, "Recess Week_Sem 2");

        // Sem 2 - Week 7 to 13
        acadWeekNo = 7;
        for (int i = semTwoStartWeek + 7; i < semTwoStartWeek + 14; i++) {
            acadCalMap.put(i, "Week " + acadWeekNo + "_Sem 2");
            acadWeekNo++;
        }

        // Sem 2 - Reading & Examination Weeks
        acadCalMap.put(semTwoStartWeek + 14, "Reading Week_Sem 2");
        acadCalMap.put(semTwoStartWeek + 15, "Examination Week_Sem 2");
        acadCalMap.put(semTwoStartWeek + 16, "Examination Week_Sem 2");

        // Sem 2 - Vacation
        vacationWeekNo = semTwoStartWeek + 17;
        while (vacationWeekNo <= semTwoEndWeek) {
            acadCalMap.put(vacationWeekNo++, "Vacation_Sem 2");
        }

        return acadCalMap;
    }

    /**
     * Initialises current semester's details.
     *
     * @param date the current date when the program is run
     * @param acadCalMap used to determine current academic week
     * @return an array of Strings of the current semester's details
     */
    private static String[] getSemesterDetails(LocalDate date, HashMap<Integer, String> acadCalMap) {
        String acadWeek;
        String acadSem;
        String acadYear = null;
        String noOfWeeks = null;
        String[] acadWeekDetails;
        LocalDate startDate = date;
        LocalDate endDate = date;
        int currentYear = date.getYear();
        int currentWeekOfYear = date.get(WeekFields.ISO.weekOfWeekBasedYear());

        // Initialise week numbers for certain weeks.
        int firstWeekSemOne = 0;
        int firstWeekSemOneHol = 0;
        int lastWeekSemOneHol = 0;
        int firstWeekSemTwo = 0;
        int firstWeekSemTwoHol = 0;
        for (Map.Entry<Integer, String> entry: acadCalMap.entrySet()) {
            if ("Orientation Week_Sem 1".equals(entry.getValue())) {
                firstWeekSemOne = entry.getKey();
            } else if ("Examination Week_Sem 1".equals(entry.getValue())) {
                firstWeekSemOneHol = entry.getKey() + 1;
            } else if ("Week 1_Sem 2".equals(entry.getValue())) {
                lastWeekSemOneHol = entry.getKey() - 1;
                firstWeekSemTwo = entry.getKey();
            } else if ("Examination Week_Sem 2".equals(entry.getValue())) {
                firstWeekSemTwoHol = entry.getKey() + 1;
            }
        }

        // Set semester details.
        acadWeekDetails = acadCalMap.get(currentWeekOfYear).split("_");
        acadWeek = acadWeekDetails[0];
        acadSem = acadWeekDetails[1];
        if ("Vacation".equals(acadWeek) && "Sem 1".equals(acadSem)) {
            noOfWeeks = "5";
            if (currentWeekOfYear < 4) {
                acadYear = "AY" + (currentYear - 1) + "/" + currentYear;
                startDate = startDate.withYear(currentYear - 1);
            } else {
                acadYear = "AY" + currentYear + "/" + (currentYear + 1);
                endDate = endDate.withYear(currentYear + 1);
            }
            startDate = startDate.with(WeekFields.ISO.weekOfWeekBasedYear(), firstWeekSemOneHol);
            startDate = startDate.with(WeekFields.ISO.dayOfWeek(), 1);
            endDate = endDate.with(WeekFields.ISO.weekOfWeekBasedYear(), lastWeekSemOneHol);
            endDate = endDate.with(WeekFields.ISO.dayOfWeek(), 7);
        } else if ("Vacation".equals(acadWeek) && "Sem 2".equals(acadSem)) {
            noOfWeeks = "12";
            acadYear = "AY" + (currentYear - 1) + "/" + currentYear;
            startDate = startDate.with(WeekFields.ISO.weekOfWeekBasedYear(), firstWeekSemTwoHol);
            startDate = startDate.with(WeekFields.ISO.dayOfWeek(), 1);
            endDate = endDate.with(WeekFields.ISO.weekOfWeekBasedYear(), firstWeekSemTwoHol + 11);
            endDate = endDate.with(WeekFields.ISO.dayOfWeek(), 7);
        } else if ("Sem 1".equals(acadSem)) {
            noOfWeeks = "18";
            acadYear = "AY" + currentYear + "/" + (currentYear + 1);
            startDate = startDate.with(WeekFields.ISO.weekOfWeekBasedYear(), firstWeekSemOne);
            startDate = startDate.with(WeekFields.ISO.dayOfWeek(), 1);
            endDate = endDate.with(WeekFields.ISO.weekOfWeekBasedYear(), firstWeekSemOne + 17);
            endDate = endDate.with(WeekFields.ISO.dayOfWeek(), 7);
        } else if ("Sem 2".equals(acadSem)) {
            noOfWeeks = "17";
            acadYear = "AY" + (currentYear - 1) + "/" + currentYear;
            startDate = startDate.with(WeekFields.ISO.weekOfWeekBasedYear(), firstWeekSemTwo);
            startDate = startDate.with(WeekFields.ISO.dayOfWeek(), 1);
            endDate = endDate.with(WeekFields.ISO.weekOfWeekBasedYear(), firstWeekSemTwo + 16);
            endDate = endDate.with(WeekFields.ISO.dayOfWeek(), 7);
        }
        return new String[] {acadWeek, acadSem, acadYear, noOfWeeks, startDate.toString(), endDate.toString()};
    }

    /**
     * Adds a day to the Planner.
     *
     * @throws Semester.DuplicateDayException if a date is not found in the semester.
     */
    //    public void addDay(LocalDate date, Day day) throws Semester.DuplicateDayException {
    //        semester.addDay(date, day);
    //    }

    /**
     * Adds a slot to the Planner.
     *
     */
    public Day addSlot(LocalDate date, Slot slot) throws Semester.DateNotFoundException {
        return semester.addSlot(date, slot);
    }

    /**
     * Removes a Slot in the Planner.
     */
    public void removeSlot(LocalDate date, ReadOnlySlot slot) {
        semester.removeSlot(date, slot);
    }

    /**
     * Edit specific slot within the planner.
     *
     * @throws IllegalValueException if a targetDate is not found in the semester.
     */
    public void editSlot(LocalDate targetDate, ReadOnlySlot targetSlot, LocalDate date,
                         LocalTime startTime, int duration, String name, String location,
                         String description, Set<Tag> tags) throws IllegalValueException {
        semester.editSlot(targetDate, targetSlot, date, startTime, duration, name, location, description, tags);
    }

    /**
     * Checks if an slot exists in planner.
     */
    public boolean containsSlot(LocalDate date, ReadOnlySlot slot) {
        return semester.contains(date, slot);
    }

    /**
     * Checks if an equivalent Day exists in the Planner.
     */
    public boolean containsDay(ReadOnlyDay day) {
        return semester.contains(day);
    }

    /**
     * Checks if an equivalent Day exists in the Planner.
     */
    public boolean containsDay(LocalDate date) {
        return semester.contains(date);
    }

    /**
     * Removes the equivalent day from the Planner.
     *
     * @throws Semester.DateNotFoundException if no such Day could be found.
     */
    public void removeDay(ReadOnlyDay day) throws Semester.DateNotFoundException {
        semester.remove(day);
    }

    /**
     * Removes the equivalent day from the Planner.
     *
     * @throws Semester.DateNotFoundException if no such Day could be found.
     */
    public void removeDay(LocalDate date) throws Semester.DateNotFoundException {
        semester.remove(date);
    }

    /**
     * Clears all days from the Planner.
     */
    public void clearDays() {
        semester.clearDays();
    }

    /**
     * Clears all slots from the Planner.
     */
    public void clearSlots() {
        semester.clearSlots();
    }

    /**
     * Defensively copy the Semester in the Planner at the time of the call.
     */
    public Semester getSemester() {
        return semester;
    }

    /**
     * gets all days in the Planner.
     */
    public HashMap<LocalDate, Day> getAllDays() {
        return semester.getDays();
    }

    /**
     * gets specific day in the Planner.
     */
    public Day getDay(LocalDate date) {
        return getAllDays().get(date);
    }

    /**
     * gets all slots in the Planner containing all specified tags.
     */
    public Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> getSlots(Set<Tag> tags) {
        final Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();

        for (Map.Entry<LocalDate, Day> entry : getAllDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (slot.getTags().containsAll(tags)) {
                    selectedSlots.put(entry.getKey(), new Pair<>(entry.getValue(), slot));
                }
            }
        }

        return selectedSlots;
    }


    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Planner // instanceof handles nulls
                && this.semester.equals(((Planner) other).semester));
    }

    @Override
    public int hashCode() {
        return semester.hashCode();
    }
}
