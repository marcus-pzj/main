package planmysem.model.semester;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;

/**
 * A list of days. Does not allow null elements or duplicates.
 *
 * @see Day#equals(Object)
 */
public class Semester implements ReadOnlySemester {
    private static HashMap<Integer, String> acadCal = new HashMap<>();
    private final String name;
    private final String academicYear;
    private final HashMap<LocalDate, Day> days = new HashMap<>();
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int noOfWeeks;

    // These variables aid in making searches more effective
    private final Set<LocalDate> recessDays = new HashSet<>();
    private final Set<LocalDate> readingDays = new HashSet<>();
    private final Set<LocalDate> normalDays = new HashSet<>();
    private final Set<LocalDate> examDays = new HashSet<>();

    /**
     * Constructs empty semester.
     */
    public Semester() {
        this.name = null;
        this.academicYear = null;
        this.startDate = null;
        this.endDate = null;
        this.noOfWeeks = 0;
    }

    /**
     * Constructs a semester with the given Days.
     */
    public Semester(String name, String academicYear, HashMap<LocalDate, Day> days, LocalDate startDate,
                    LocalDate endDate, int noOfWeeks, Set<LocalDate> recessDays, Set<LocalDate> readingDays,
                    Set<LocalDate> normalDays, Set<LocalDate> examDays) {
        this.name = name;
        this.academicYear = academicYear;
        this.days.putAll(days);
        this.startDate = startDate;
        this.endDate = endDate;
        this.noOfWeeks = noOfWeeks;

        this.recessDays.addAll(recessDays);
        this.readingDays.addAll(readingDays);
        this.normalDays.addAll(normalDays);
        this.examDays.addAll(examDays);
    }

    /**
     * Constructs a shallow copy of the Semester.
     */
    public Semester(Semester source) {
        this.name = source.getName();
        this.academicYear = source.getAcademicYear();
        this.days.putAll(source.days);
        this.startDate = source.startDate;
        this.endDate = source.endDate;
        this.noOfWeeks = source.noOfWeeks;

        this.recessDays.addAll(source.recessDays);
        this.readingDays.addAll(source.readingDays);
        this.normalDays.addAll(source.normalDays);
        this.examDays.addAll(source.examDays);
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
        acadCal = acadCalMap;
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

        //System.out.println(acadCalMap); //why does this print twice?
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
     * Get set of slots which contain all specified tags.
     */
    public Map<LocalDateTime, ReadOnlySlot> getSlots(Set<String> tags) {
        Map<LocalDateTime, ReadOnlySlot> selectedSlots = new TreeMap<>();

        for (Map.Entry<LocalDate, Day> day : days.entrySet()) {
            for (Slot slot : day.getValue().getSlots()) {
                if (slot.getTags().containsAll(tags)) {
                    selectedSlots.put(LocalDateTime.of(day.getKey(), slot.getStartTime()), slot);
                }
            }
        }

        return selectedSlots;
    }

    /**
     * Adds a Slot to the Semester.
     *
     * @throws DateNotFoundException if a date is not found in the semester.
     */
    public Day addSlot(LocalDate date, Slot slot) throws DateNotFoundException {
        if (date == null || (date.isBefore(startDate) || date.isAfter(endDate))) {
            throw new DateNotFoundException();
        }
        days.get(date).addSlot(slot);
        return days.get(date);
    }

    /**
     * Removes a Slot to the Semester.
     */
    public void removeSlot(LocalDate date, ReadOnlySlot slot) {
        days.get(date).removeSlot(slot);
    }

    /**
     * Edits a Slot in the Semester.
     */
    public void editSlot(LocalDate targetDate, ReadOnlySlot targetSlot, LocalDate date, LocalTime startTime,
                         int duration, String name, String location, String description, Set<String> tags) {
        Slot editingSlot = days.get(targetDate).getSlots().stream()
            .filter(s -> s.equals(targetSlot)).findAny().orElse(null);

        if (date != null) {
            Slot savedSlot = new Slot(editingSlot);
            days.get(date).addSlot(savedSlot);
            days.get(targetDate).removeSlot(editingSlot);
            editingSlot = savedSlot;
        }
        if (startTime != null) {
            editingSlot.setStartTime(startTime);
        }
        if (duration != -1) {
            editingSlot.setDuration(duration);
        }

        editingSlot.setName(name);
        editingSlot.setLocation(location);
        editingSlot.setDescription(description);
        if (tags.size() > 0) {
            editingSlot.setTags(tags);
        }
    }

    /**
     * Removes the equivalent Day from the list.
     *
     * @throws DateNotFoundException if no such Day could be found in the list.
     */
    public void remove(ReadOnlyDay day) throws DateNotFoundException {
        if (!contains(day)) {
            throw new DateNotFoundException();
        }
        days.remove(day);
    }

    /**
     * Removes the equivalent Day from the list.
     *
     * @throws DateNotFoundException if no such Day could be found in the list.
     */
    public void remove(LocalDate date) throws DateNotFoundException {
        if (!contains(date)) {
            throw new DateNotFoundException();
        }
        days.remove(date);
    }

    /**
     * Clears all Days from the address book.
     */
    public void clearDays() {
        days.clear();
    }

    /**
     * Clears all Days from the address book.
     */
    public void clearSlots() {
        for (Map.Entry<LocalDate, Day> day : days.entrySet()) {
            day.getValue().clear();
        }
    }

    /**
     * Checks if the list contains an equivalent slot as the given argument.
     */
    public boolean contains(LocalDate date, ReadOnlySlot slot) {
        return days.get(date).contains(slot);
    }

    /**
     * Checks if the list contains an equivalent Day as the given argument.
     */
    public boolean contains(ReadOnlyDay day) {
        return days.containsValue(day);
    }

    /**
     * Checks if the list contains an equivalent date as the given argument.
     */
    public boolean contains(LocalDate date) {
        return days.containsKey(date);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAcademicYear() {
        return academicYear;
    }

    @Override
    public void setDays(HashMap<LocalDate, Day> days) {
        this.days.clear();
        this.days.putAll(days);
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public int getNoOfWeeks() {
        return noOfWeeks;
    }

    @Override
    public Set<LocalDate> getRecessDays() {
        return recessDays;
    }

    @Override
    public Set<LocalDate> getReadingDays() {
        return readingDays;
    }

    @Override
    public Set<LocalDate> getNormalDays() {
        return normalDays;
    }

    @Override
    public Set<LocalDate> getExamDays() {
        return examDays;
    }

    @Override
    public HashMap<LocalDate, Day> getDays() {
        return days;
    }

    @Override
    public HashMap<Integer, String> getAcadCal() {
        return acadCal;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Semester // instanceof handles nulls
                && this.name.equals(((Semester) other).name)
                && this.academicYear.equals(((Semester) other).academicYear)
                && this.days.equals(((Semester) other).days)
                && this.startDate.equals(((Semester) other).startDate)
                && this.endDate.equals(((Semester) other).endDate)
                && this.noOfWeeks == (((Semester) other).noOfWeeks)
                && this.recessDays.equals(((Semester) other).recessDays)
                && this.readingDays.equals(((Semester) other).readingDays)
                && this.normalDays.equals(((Semester) other).normalDays)
                && this.examDays.equals(((Semester) other).examDays));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, academicYear, days, startDate, endDate, noOfWeeks,
                recessDays, readingDays, normalDays, examDays);
    }

    /**
     * Signals that an operation targeting a specified Day in the list would fail because
     * there is no such matching Day in the list.
     */
    public static class DateNotFoundException extends Exception {
    }
}
