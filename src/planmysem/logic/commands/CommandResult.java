package planmysem.logic.commands;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javafx.util.Pair;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;

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

    /**
     * Returns list of Slots relevant to the command command result, if any.
     */
    public Optional<Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getRelevantSlots() {
        return Optional.ofNullable(slots);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        return feedbackToUser.equals(otherCommandResult.feedbackToUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser);
    }
}
