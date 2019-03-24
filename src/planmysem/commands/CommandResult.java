package planmysem.commands;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javafx.util.Pair;
import planmysem.data.semester.ReadOnlyDay;
import planmysem.data.slot.ReadOnlySlot;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    /**
     * The feedback message to be shown to the user. Contains a description of the execution result
     */
    public final String feedbackToUser;

    /**
     * The list of Slots that was produced by the command
     */
    private final Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slots;

    public CommandResult(String feedbackToUser) {
        this.feedbackToUser = feedbackToUser;
        slots = null;
    }

    public CommandResult(String feedbackToUser, Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slots) {
        this.feedbackToUser = feedbackToUser;
        this.slots = slots;
    }

    /**
     * Returns list of Slots relevant to the command command result, if any.
     */
    public Optional<Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getRelevantSlots() {
        return Optional.ofNullable(slots);
    }

}
