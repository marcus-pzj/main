//@@author marcus-pzj
package planmysem.commands;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.data.semester.Day;
import planmysem.data.semester.ReadOnlyDay;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;
import planmysem.data.tag.Tag;

/**
 * Displays a list of all slots in the planner whose name matches the argument keyword.
 * Keyword matching is case sensitive.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String COMMAND_WORD_SHORT = "l";
    public static final String MESSAGE_SUCCESS = "%1$s Slots listed.\n%2$s";
    public static final String MESSAGE_SUCCESS_NONE = "0 Slots listed.\n";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all slots whose name "
            + "directly matches the specified keyword (case-sensitive)."
            //+ "\n\tOptional Parameters: [past] [next] [all]"
            //+ "\n\tDefault: list all"
            + "\n\tExample: " + COMMAND_WORD + " n/CS1010";

    private final String keyword;
    private final boolean isListByName;

    public ListCommand(String name, String tag) {
        this.keyword = (name == null) ? tag.trim() : name.trim();
        this.isListByName = (name != null);
    }
    @Override
    public CommandResult execute() {
        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();

        for (Map.Entry<LocalDate, Day> entry : planner.getAllDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (isListByName) {
                    if (slot.getName().value.equalsIgnoreCase(keyword)) {
                        selectedSlots.put(entry.getKey(), new Pair<>(entry.getValue(), slot));
                    }
                } else {
                    Set<Tag> tagSet = slot.getTags();
                    for (Tag tag : tagSet) {
                        //                        if (slot.getTags().contains(keyword))
                        if (tag.value.equalsIgnoreCase(keyword)) {
                            selectedSlots.put(entry.getKey(), new Pair<>(entry.getValue(), slot));
                        }
                    }
                }
            }
        }

        if (selectedSlots.isEmpty()) {
            return new CommandResult(MESSAGE_SUCCESS_NONE);
        }
        setData(this.planner, relevantSlots);

        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessage(selectedSlots)));
    }

}


