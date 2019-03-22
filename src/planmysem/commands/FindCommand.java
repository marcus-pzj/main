package planmysem.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javafx.util.Pair;
import planmysem.data.semester.Day;
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

    public FindCommand(String name, String tag ) {
        this.keyword = (name == null) ? tag.trim() : name.trim();
        this.isFindByName = (name != null);
    }

    @Override
    public CommandResult execute() {
        final List<Pair<LocalDate, ? extends ReadOnlySlot>> relevantSlots = new ArrayList<>();
        List<Slot> matchedSlots = new ArrayList<>();
        LocalDate date;

        for (Map.Entry<LocalDate, Day> entry : planner.getSemester().getDays().entrySet()) {
            for (Slot slots : entry.getValue().getSlots()) {
                if (isFindByName) {
                    if (Pattern.matches(".*" + keyword + ".*", slots.getName().value)) {
                        matchedSlots.add(slots);
                        date = entry.getKey();
                        relevantSlots.add(new Pair<>(date, slots));
                    }
                } else {
                    Set<Tag> tagSet = slots.getTags();
                    for (Tag tag : tagSet) {
                        if (Pattern.matches(".*" + keyword + ".*", tag.value)) {
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
//        final List<Pair<LocalDate, ? extends ReadOnlySlot>> relevantSlots = new ArrayList<>();
//        List<Slot> matchedSlots = new ArrayList<>();
//        LocalDate date;
//
//        for (Map.Entry<LocalDate, Day> entry : planner.getSemester().getDays().entrySet()) {
//            for (Slot slots : entry.getValue().getSlots()) {
//                if (Pattern.matches(".*" + keyword + ".*", slots.getName().value)) {
//                    matchedSlots.add(slots);
//                    date = entry.getKey();
//                    relevantSlots.add(new Pair<>(date, slots));
//                }
//            }
//        }
//
//        if (matchedSlots.isEmpty()) {
//            return new CommandResult(MESSAGE_SUCCESS_NONE);
//        }
//        setData(this.planner, relevantSlots);
//
//        return new CommandResult(String.format(MESSAGE_SiUCCESS, matchedSlots.size(),
//                craftSuccessMessage(rele
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
