package planmysem.ui;

import java.time.LocalDate;
import java.util.List;

import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;

/**
 * Used for formatting text for display. e.g. for adding text decorations.
 */
public class Formatter {

    /**
     * A decorative prefix added to the beginning of lines printed by Planner
     */
    private static final String LINE_PREFIX = " ";

    /**
     * A platform independent line separator.
     */
    private static final String LS = System.lineSeparator();

    /**
     * Formats the given strings for displaying to the user.
     */
    public String format(String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String m : messages) {
            sb.append(LINE_PREFIX + m.replace("\n", LS + LINE_PREFIX) + LS);
        }
        return sb.toString();
    }

    /**
     * Formats the given list of slots for displaying to the user.
     */
    public String formatSlots(List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> slots) {
        return Messages.craftListMessage(slots);
    }

}
