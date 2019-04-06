package planmysem.logic.commands;

import static planmysem.common.Messages.MESSAGE_INVALID_SLOT_DISPLAYED_INDEX;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public static final String MESSAGE_SLOT_NOT_IN_PLANNER =
            "Slot could not be found in Planner. Perhaps it was previously deleted.";

    private final Set<String> tags = new HashSet<>();
    private final int targetIndex;

    /**
     * Convenience constructor using raw values.
     */
    public DeleteCommand(Set<String> tags) {
        targetIndex = -1;
        this.tags.addAll(tags);
    }

    /**
     * Convenience constructor using raw values.
     */
    public DeleteCommand(int index) {
        this.targetIndex = index;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) throws CommandException {
        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();
        String messageSelected;
        String messageSlots;

        if (targetIndex == -1) {
            selectedSlots.addAll(model.getSlots(tags));

            if (selectedSlots.size() == 0) {
                throw new CommandException(String.format(MESSAGE_SUCCESS_NO_CHANGE,
                    Messages.craftSelectedMessage(tags)));
            }

            // perform deletion of slots from the planner
            for (Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> entry : selectedSlots) {
                model.removeSlot(entry.getKey(), entry.getValue().getValue());
            }
            messageSelected = Messages.craftSelectedMessage(tags);
            messageSlots = Messages.craftSelectedMessage("Deleted Slots:", selectedSlots);
        } else {
            try {
                final Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> target = model.getLastShownItem(targetIndex);

                // check if slot still exist
                if (!model.slotExists(target.getKey(), target.getValue().getValue())) {
                    throw new CommandException(MESSAGE_SLOT_NOT_IN_PLANNER);
                }

                selectedSlots.add(target);

                model.removeSlot(target);

                messageSelected = Messages.craftSelectedMessage(targetIndex);
                messageSlots = Messages.craftSelectedMessage("Deleted Slot:", selectedSlots);
            } catch (IndexOutOfBoundsException ie) {
                throw new CommandException(MESSAGE_INVALID_SLOT_DISPLAYED_INDEX);
            }
        }
        model.commit();
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                selectedSlots.size(), messageSelected, messageSlots));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && tags.equals(((DeleteCommand) other).tags)
                && targetIndex == ((DeleteCommand) other).targetIndex);
    }
}
