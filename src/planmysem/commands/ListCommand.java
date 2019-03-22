package planmysem.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;
import planmysem.data.semester.Day;
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
        final List<Pair<LocalDate, ? extends ReadOnlySlot>> relevantSlots = new ArrayList<>();
        List<Slot> matchedSlots = new ArrayList<>();
        LocalDate date;

        for (Map.Entry<LocalDate, Day> entry : planner.getSemester().getDays().entrySet()) {
            for (Slot slots : entry.getValue().getSlots()) {
                if (isListByName) {
                    if (slots.getName().value.equalsIgnoreCase(keyword)) {
                        matchedSlots.add(slots);
                        date = entry.getKey();
                        relevantSlots.add(new Pair<>(date, slots));
                    }
                } else {
                    Set<Tag> tagSet = slots.getTags();
                    for (Tag tag : tagSet) {
                        if (tag.value.equalsIgnoreCase(keyword)) {
                            matchedSlots.add(slots);
                            date = entry.getKey();
                            relevantSlots.add(new Pair<>(date, slots));
                        }
                    }
                }
            }
        }

        if (matchedSlots.isEmpty()) {
            return new CommandResult(MESSAGE_SUCCESS_NONE);
        }
        setData(this.planner, relevantSlots);

        return new CommandResult(String.format(MESSAGE_SUCCESS, matchedSlots.size(),
                craftSuccessMessage(relevantSlots)));
    }


    /**
     * Craft success message.
     */
    private String craftSuccessMessage(List<Pair<LocalDate, ? extends ReadOnlySlot>> result) {
        int count = 1;
        StringBuilder sb = new StringBuilder();

        for (Pair<LocalDate, ? extends ReadOnlySlot> pair : result) {
            sb.append("\n");
            sb.append(count + ".\t");
            sb.append("Name: ");
            sb.append(pair.getValue().getName().toString());
            sb.append(",\n\t");
            sb.append("Date: ");
            sb.append(pair.getKey().toString());
            sb.append(",\n\t");
            sb.append("Start Time: ");
            sb.append(pair.getValue().getStartTime());
            sb.append("\n\t");
            sb.append("Tags: ");
            sb.append(pair.getValue().getTags());
            sb.append("\n");
            count++;
        }
        return sb.toString();
    }
}


