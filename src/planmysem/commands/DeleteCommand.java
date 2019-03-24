package planmysem.commands;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String COMMAND_WORD_ALT = "del";
    public static final String COMMAND_WORD_SHORT = "d";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Delete single or multiple slots in the Planner."
            + "\n\tParameters: "
            + "\n\t\tMandatory: t/TAG... or INDEX"
            + "\n\tExample 1: " + COMMAND_WORD
            + " t/CS2113T t/Tutorial"
            + "\n\tExample 2: " + COMMAND_WORD
            + " 2";

    public static final String MESSAGE_SUCCESS_NO_CHANGE = "No Slots were deleted.\n\n%1$s";
    public static final String MESSAGE_SUCCESS = "%1$s Slots deleted.\n\n%2$s\n%3$s";

    private final Set<Tag> tags = new HashSet<>();

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public DeleteCommand(Set<String> tags) throws IllegalValueException {
        this.tags.addAll(Utils.parseTags(tags));
    }

    /**
     * Convenience constructor using raw values.
     */
    public DeleteCommand(int index) {
        super(index);
    }

    @Override
    public CommandResult execute() {
        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();

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

        // perform deletion of slots from the planner
        for (Map.Entry<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> entry: selectedSlots.entrySet()) {
            planner.removeSlot(entry.getKey(), entry.getValue().getValue());
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftSelectedMessage(tags), Messages.craftSelectedMessage("Deleted Slots:", selectedSlots)));
    }
}
