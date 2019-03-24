package planmysem.data.semester;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import planmysem.data.exception.DuplicateDataException;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;
import planmysem.data.tag.Tag;

/**
 * A list of days. Does not allow null elements or duplicates.
 *
 * @see Day#equals(Object)
 */
public class Semester implements ReadOnlySemester {
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
     * Adds a Day to the list.
     *
     * @throws DuplicateDayException if the Day to addDay is a duplicate of an existing Day in the list.
     */
    public void addDay(LocalDate date, Day day) throws DuplicateDayException {
        if (contains(day)) {
            throw new DuplicateDayException();
        }
        days.put(date, day);
    }

    /**
     * Get set of slots which contain all specified tags.
     */
    public Map<LocalDateTime, ReadOnlySlot> getSlots(Set<Tag> tags) {
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
     *
     * @throws DateNotFoundException if a targetDate is not found in the semester.
     * @throws IllegalValueException if a targetDate is not found in the semester.
     */
    public void editSlot(LocalDate targetDate, ReadOnlySlot targetSlot, LocalDate date, LocalTime startTime,
                         int duration, String name, String location, String description, Set<Tag> tags)
            throws IllegalValueException {

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
    public HashMap<LocalDate, Day> getDays() {
        return days;
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
     * Signals that an operation would have violated the 'no duplicates' property.
     */
    public static class DuplicateDayException extends DuplicateDataException {
        protected DuplicateDayException() {
            super("Operation would result in duplicate days");
        }
    }

    /**
     * Signals that an operation targeting a specified Day in the list would fail because
     * there is no such matching Day in the list.
     */
    public static class DateNotFoundException extends Exception {
    }
}
