package planmysem.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import planmysem.data.slot.ReadOnlySlot;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindCommandP extends CommandP {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Finds all slots whose tags contain any of "
            + "the specified keywords (case-sensitive) and displays them as a list.\n\t"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n\t"
            + "Example: " + COMMAND_WORD + " CS1010 tutorial lab";

    private final Set<String> keywords;

    public FindCommandP(Set<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Returns copy of keywords in this command.
     */
    public Set<String> getKeywords() {
        return new HashSet<>(keywords);
    }

    @Override
    public CommandResultP execute() {
        // call method getSlotsWithTag here
        return new CommandResultP(MESSAGE_USAGE);
    }

    /**
     * Retrieve all persons in the address book whose names contain some of the specified keywords.
     *
     * @param keywords for searching
     * @return list of persons found
     */
    private List<ReadOnlySlot> getSlotsWithTag(Set<String> keywords) {
        List<ReadOnlySlot> test = new ArrayList<>();
        // retrieve list of Slots here
        return test;
    }
}
