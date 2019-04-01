package planmysem.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import planmysem.logic.CommandHistory;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.model.Model;
import planmysem.model.recurrence.Recurrence;
import planmysem.model.semester.Day;
import planmysem.model.semester.Semester;
import planmysem.model.slot.Slot;

/**
 * Adds a person to the planner.
 */
public class AddCommand extends Command {
    public static final String COMMAND_WORD = "add";
    public static final String COMMAND_WORD_SHORT = "a";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Add single or multiple slots to the Planner."
            + "\n\tParameters: "
            + "\n\t\tMandatory: n/NAME d/DATE_OR_DAY_OF_WEEK st/START_TIME et/END_TIME_OR_DURATION"
            + "\n\t\tOptional: [l/LOCATION] [des/DESCRIPTION] [r/normal] [r/recess] [r/reading] [r/exam]"
            + "[r/past] [t/TAG]..."
            + "\n\tExample: " + COMMAND_WORD
            + " n/CS2113T Tutorial d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram t/CS2113T "
            + "t/Tutorial r/normal";

    public static final String MESSAGE_SUCCESS = "%1$s Slots added.\n\n%2$s";
    public static final String MESSAGE_FAIL_OUT_OF_BOUNDS = "Date specified is out of bounds.";

    private final Slot slot;
    private final Recurrence recurrence;

    /**
     * Convenience constructor using raw values.
     */
    public AddCommand(LocalDate date, String name, String location, String description, LocalTime startTime,
                      int duration, Set<String> tags, Set<String> recurrences) {
        slot = new Slot(name, location, description, startTime, duration, tags);
        recurrence = new Recurrence(recurrences, date);
    }

    /**
     * Convenience constructor using raw values.
     */
    public AddCommand(int day, String name, String location, String description, LocalTime startTime,
                      int duration, Set<String> tags, Set<String> recurrences) {
        slot = new Slot(name, location, description, startTime, duration, tags);
        recurrence = new Recurrence(recurrences, day);
    }

    /**
     * Creates an AddCommand to add the specified {@code slot}
     * and using specific {@code recurrence}
     */
    public AddCommand(Slot slot, Recurrence recurrence) {
        requireNonNull(slot);
        requireNonNull(recurrence);
        this.slot = slot;
        this.recurrence = recurrence;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) throws CommandException {
        Set<LocalDate> dates = recurrence.generateDates(model.getPlanner().getSemester());
        Map<LocalDate, Day> days = new TreeMap<>();
        for (LocalDate date : dates) {
            try {
                days.put(date, model.addSlot(date, slot));
            } catch (Semester.DateNotFoundException dnfe) {
                throw new CommandException(MESSAGE_FAIL_OUT_OF_BOUNDS);
            }
        }
        model.commit();
        return new CommandResult(String.format(MESSAGE_SUCCESS, dates.size(),
                craftSuccessMessage(days, slot)));
    }

    /**
     * Craft success message.
     */
    public static String craftSuccessMessage(Map<LocalDate, Day> days, Slot slot) {
        StringBuilder sb = new StringBuilder();
        sb.append("On dates:");

        int count = 1;
        for (Map.Entry<LocalDate, Day> day : days.entrySet()) {
            sb.append("\n");
            sb.append(count);
            sb.append(".\t");
            sb.append(day.getValue().getType());
            sb.append(", ");
            sb.append(day.getKey().toString());
            sb.append(", ");
            sb.append(day.getKey().getDayOfWeek().toString());
            count++;
        }
        sb.append("\n\n");

        sb.append(slot.toString());

        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddCommand // instanceof handles nulls
                && slot.equals(((AddCommand) other).slot)
                && recurrence.equals(((AddCommand) other).recurrence));
    }
}
