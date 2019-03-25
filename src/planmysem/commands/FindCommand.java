//@@author marcus-pzj
package planmysem.commands;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.data.semester.Day;
import planmysem.data.semester.ReadOnlyDay;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;
import planmysem.data.tag.Tag;


/**
 * Finds all slots in planner whose name contains the argument keyword.
 * Keyword matching is case sensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_WORD_SHORT = "f";
    private static final String MESSAGE_SUCCESS = "%1$s Slots listed.\n%2$s";
    private static final String MESSAGE_SUCCESS_NONE = "0 Slots listed.\n";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Finds all slots whose name "
            + "contains the specified keywords (case-sensitive).\n\t"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n\t"
            + "Example: " + COMMAND_WORD + "n/CS";

    private final String keyword;
    private final boolean isFindByName;

    public FindCommand(String name, String tag) {
        this.keyword = (name == null) ? tag.trim() : name.trim();
        this.isFindByName = (name != null);
    }

    @Override
    public CommandResult execute() {
        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();

        for (Map.Entry<LocalDate, Day> entry : planner.getAllDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (isFindByName) {
                    if (Pattern.matches(".*" + keyword + ".*", slot.getName().value)) {
                        selectedSlots.put(entry.getKey(), new Pair<>(entry.getValue(), slot));
                    }
                } else {
                    Set<Tag> tagSet = slot.getTags();
                    for (Tag tag : tagSet) {
                        if (Pattern.matches(".*" + keyword + ".*", tag.value)) {
                            selectedSlots.put(entry.getKey(), new Pair<>(entry.getValue(), slot));
                        }
                    }
                }
            }
        }

        if (selectedSlots.isEmpty()) {
            return new CommandResult(MESSAGE_SUCCESS_NONE);
        }
        setData(planner, selectedSlots);

        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftSelectedMessage(selectedSlots)));
    }
}
