package planmysem.commands;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;

import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.semester.ReadOnlyDay;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.tag.Tag;

/**
 * Adds a person to the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";
    public static final String COMMAND_WORD_SHORT = "e";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edit single or multiple slots in the Planner."
            + "\n\tParameters: "
            + "\n\t\tMandatory: t/TAG... or INDEX"
            + "\n\t\tOptional Parameters: [nst/NEW_START_TIME] [net/NEW_END_TIME|DURATION] "
            + "[nl/NEW_LOCATION] [nd/NEW_DESCRIPTION]"
            + "\n\tExample 1: " + COMMAND_WORD
            + " t/CS2113T t/Tutorial nl/COM2 04-01"
            + "\n\tExample 2: " + COMMAND_WORD
            + " 2 nl/COM2 04-01";


    public static final String MESSAGE_SUCCESS_NO_CHANGE = "No Slots were edited.\n\n%1$s";
    public static final String MESSAGE_SUCCESS = "%1$s Slots edited.\n\n%2$s\n%3$s";
    public static final String MESSAGE_FAIL_ILLEGAL_VALUE = MESSAGE_SUCCESS
        + " Illegal characters were detected.";

    private final LocalDate date;
    private final LocalTime startTime;
    private final int duration;
    private final String name;
    private final String location;
    private final String description;
    private final Set<Tag> tags = new HashSet<>();
    private final Set<Tag> newTags = new HashSet<>();

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public EditCommand(String name, LocalTime startTime, int duration, String location, String description,
                       Set<String> tags, Set<String> newTags) throws IllegalValueException {
        this.date = null;
        this.startTime = startTime;
        this.duration = duration;
        this.name = name;
        this.location = location;
        this.description = description;
        if (tags != null) {
            this.tags.addAll(Utils.parseTags(tags));
        }
        if (newTags != null) {
            this.newTags.addAll(Utils.parseTags(newTags));
        }
    }

    /**
     * Convenience constructor using raw values.
     */
    public EditCommand(int index, String name, LocalDate date, LocalTime startTime, int duration,
                       String location, String description, Set<String> newTags)
            throws IllegalValueException {
        super(index);
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.name = name;
        this.location = location;
        this.description = description;
        if (newTags != null) {
            this.newTags.addAll(Utils.parseTags(newTags));
        }
    }

    @Override
    public CommandResult execute() {
        final Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();

        if (getTargetIndex() == -1) {
            selectedSlots.putAll(planner.getSlots(tags));

            if (selectedSlots.size() == 0) {
                return new CommandResult(String.format(MESSAGE_SUCCESS_NO_CHANGE,
                        Messages.craftSelectedMessage(tags)));
            }
        } else {
            try {
                final Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> target = getTargetSlot();
                selectedSlots.put(target.getKey(), target.getValue());
            } catch (IndexOutOfBoundsException ie) {
                return new CommandResult(Messages.MESSAGE_INVALID_SLOT_DISPLAYED_INDEX);
            }
        }

        // Need to craft success message earlier to get original instead of edited Slots
        String successMessage = craftSuccessMessage(selectedSlots);

        for (Map.Entry<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> entry : selectedSlots.entrySet()) {
            try {
                planner.editSlot(entry.getKey(), entry.getValue().getValue(), date,
                        startTime, duration, name, location, description, newTags);
            } catch (IllegalValueException ive) {
                return new CommandResult(MESSAGE_FAIL_ILLEGAL_VALUE);
            }
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftSelectedMessage(tags), successMessage));
    }

    /**
     * Craft success message.
     */
    public String craftSuccessMessage(Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots) {
        StringBuilder sb = new StringBuilder();

        sb.append("Details Edited: ");

        if (name != null) {
            sb.append("\nName: ");
            sb.append("\"");
            if ("".equals(name)) {
                sb.append("null");
            } else {
                sb.append(name);
            }
            sb.append("\"");
        }
        if (startTime != null) {
            sb.append("\nStart Time: ");
            sb.append("\"");
            sb.append(startTime.toString());
            sb.append("\"");
        }
        if (duration != -1) {
            sb.append("\nDuration: ");
            sb.append("\"");
            sb.append(duration);
            sb.append("\"");
        }
        if (location != null) {
            sb.append("\nLocation: ");
            sb.append("\"");
            if ("".equals(location)) {
                sb.append("null");
            } else {
                sb.append(location);
            }
            sb.append("\"");
        }
        if (description != null) {
            sb.append("\nDescription: ");
            sb.append("\"");
            if ("".equals(description)) {
                sb.append("null");
            } else {
                sb.append(description);
            }
            sb.append("\"");
        }
        if (newTags.size() > 0) {
            sb.append("\nTags: ");
            StringJoiner sj = new StringJoiner(", ");

            for (Tag tag : newTags) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("\"");
                sb2.append(tag);
                sb2.append("\"");
                sj.add(sb2.toString());
            }
            sb.append(sj.toString());
        }

        sb.append("\n\n");

        sb.append(Messages.craftSelectedMessage("Edited Slots:", selectedSlots));

        return sb.toString();
    }
}
