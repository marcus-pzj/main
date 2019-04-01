package planmysem.logic.commands;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;

import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.model.Model;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;

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

    private final LocalDate date;
    private final LocalTime startTime;
    private final int duration;
    private final String name;
    private final String location;
    private final String description;
    private final Set<String> tags = new HashSet<>();
    private final Set<String> newTags = new HashSet<>();

    private final int targetIndex;

    /**
     * Convenience constructor using raw values. Edit via tags.
     */
    public EditCommand(String name, LocalTime startTime, int duration, String location, String description,
                       Set<String> tags, Set<String> newTags) {
        targetIndex = -1;
        this.date = null;
        this.startTime = startTime;
        this.duration = duration;
        this.name = name;
        this.location = location;
        this.description = description;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (newTags != null) {
            this.newTags.addAll(newTags);
        }
    }

    /**
     * Convenience constructor using raw values. Edit via index.
     */
    public EditCommand(int index, String name, LocalDate date, LocalTime startTime, int duration,
                       String location, String description, Set<String> newTags) {
        targetIndex = index;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.name = name;
        this.location = location;
        this.description = description;
        if (newTags != null) {
            this.newTags.addAll(newTags);
        }
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) throws CommandException {
        final Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();
        String messageSelected;
        String messageSlots;

        if (targetIndex == -1) {
            selectedSlots.putAll(model.getSlots(tags));

            if (selectedSlots.size() == 0) {
                throw new CommandException(String.format(MESSAGE_SUCCESS_NO_CHANGE,
                        Messages.craftSelectedMessage(tags)));
            }

            // Need to craft success message earlier to get original instead of edited Slots
            messageSlots = craftSuccessMessage(selectedSlots);

            for (Map.Entry<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> entry : selectedSlots.entrySet()) {
                model.editSlot(entry.getKey(), entry.getValue().getValue(), date,
                        startTime, duration, name, location, description, newTags);
            }

            messageSelected = Messages.craftSelectedMessage(tags);
        } else {
            try {
                final Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> target = model.getLastShownItem(targetIndex);
                selectedSlots.put(target.getKey(), target.getValue());

                // Need to craft success message earlier to get original instead of edited Slots
                messageSlots = craftSuccessMessage(selectedSlots);

                model.editSlot(target.getKey(), target.getValue().getValue(), date,
                        startTime, duration, name, location, description, newTags);

                messageSelected = Messages.craftSelectedMessage(targetIndex);

            } catch (IndexOutOfBoundsException ie) {
                throw new CommandException(Messages.MESSAGE_INVALID_SLOT_DISPLAYED_INDEX);
            }
        }
        model.commit();
        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                messageSelected, messageSlots));
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

            for (String tag : newTags) {
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

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EditCommand // instanceof handles nulls
                && Objects.equals(date, ((EditCommand) other).date)
                && Objects.equals(startTime, ((EditCommand) other).startTime)
                && duration == ((EditCommand) other).duration
                && Objects.equals(name, ((EditCommand) other).name)
                && Objects.equals(location, ((EditCommand) other).location)
                && Objects.equals(description, ((EditCommand) other).description)
                && tags.equals(((EditCommand) other).tags)
                && newTags.equals(((EditCommand) other).newTags)
                && targetIndex == ((EditCommand) other).targetIndex);
    }
}
