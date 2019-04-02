package planmysem.model.recurrence;

import static planmysem.common.Utils.getNearestDayOfWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import planmysem.common.Clock;
import planmysem.model.semester.Semester;

/**
 * Represents a Recurrence Value of a slot in the Planner.
 */
public class Recurrence {
    private final boolean normal; // Represents whether to recurse
    private final boolean recess; // Represents recess week
    private final boolean reading; // Represents reading week
    private final boolean exam; // Represents exam weeks
    private final boolean past; // Represents past academic weeks
    private final DayOfWeek day;
    private final LocalDate date;

    /**
     * Generate Recurrence values from a set that recurse over a day.
     */
    public Recurrence(Set<String> recurrences, int day) {
        this.day = DayOfWeek.of(day);
        date = getNearestDayOfWeek(LocalDate.now(Clock.get()), day);

        if (recurrences == null) {
            normal = false;
            recess = false;
            reading = false;
            exam = false;
            past = false;
            return;
        }
        normal = recurrences.contains("normal");
        recess = recurrences.contains("recess");
        reading = recurrences.contains("reading");
        exam = recurrences.contains("exam");
        past = recurrences.contains("past");
    }

    /**
     * Generate Recurrence values from a set that recurse over a date.
     */
    public Recurrence(Set<String> recurrences, LocalDate date) {
        day = date.getDayOfWeek();
        this.date = date;

        if (recurrences == null) {
            normal = false;
            recess = false;
            reading = false;
            past = false;
            exam = false;
            return;
        }
        normal = recurrences.contains("normal");
        recess = recurrences.contains("recess");
        reading = recurrences.contains("reading");
        exam = recurrences.contains("exam");
        past = recurrences.contains("past");
    }

    /**
     * Generate dates to place slots in the semester.
     */
    public Set<LocalDate> generateDates(Semester semester) {
        final Set<LocalDate> result = new TreeSet<>();

        if (!normal && !recess && !reading && !exam) {
            result.add(date);
            return result;
        }

        if (past) {
            // recurse over normal days
            if (normal) {
                result.addAll(getDates(semester.getNormalDays()));
            }

            // recurse over recess days
            if (recess) {
                result.addAll(getDates(semester.getRecessDays()));
            }

            // recurse over reading days
            if (reading) {
                result.addAll(getDates(semester.getReadingDays()));
            }

            // recurse over exam days
            if (exam) {
                result.addAll(getDates(semester.getExamDays()));
            }
        } else {
            LocalDate dateStart = LocalDate.now(Clock.get());

            // recurse over normal days
            if (normal) {
                result.addAll(getDates(semester.getNormalDays(), dateStart));
            }

            // recurse over recess days
            if (recess) {
                result.addAll(getDates(semester.getRecessDays(), dateStart));
            }

            // recurse over reading days
            if (reading) {
                result.addAll(getDates(semester.getReadingDays(), dateStart));
            }

            // recurse over examination days
            if (exam) {
                result.addAll(getDates(semester.getExamDays(), dateStart));
            }
        }

        return result;
    }

    /**
     * Get set of dates where it is a specific DayOfWeek and is after a start date.
     */
    private Set<LocalDate> getDates(Set<LocalDate> dates) {
        final Set<LocalDate> result = new HashSet<>();
        for (LocalDate d : dates) {
            if (d.getDayOfWeek() == day) {
                result.add(d);
            }
        }

        return result;
    }

    /**
     * Get set of dates where it is a specific DayOfWeek and is after a start date.
     */
    private Set<LocalDate> getDates(Set<LocalDate> dates, LocalDate dateStart) {
        final Set<LocalDate> result = new HashSet<>();
        for (LocalDate d : dates) {
            if (d.getDayOfWeek() == day && (d.isAfter(dateStart) || d.isEqual(dateStart))) {
                result.add(d);
            }
        }

        return result;
    }

    /**
     * Get date.
     */
    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Recurrence // instanceof handles nulls
                && this.recess == ((Recurrence) other).recess
                && this.reading == ((Recurrence) other).reading
                && this.normal == ((Recurrence) other).normal
                && this.exam == ((Recurrence) other).exam
                && this.past == ((Recurrence) other).past); // state check
    }

    @Override
    public int hashCode() {
        return Objects.hash(recess, reading, normal, exam, past, date);
    }
}
