package planmysem.logic;

import java.time.LocalDate;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.logic.parser.exceptions.ParseException;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;

/**
 * API of the Logic component
 */
public interface Logic {

    /**
     * Execute command.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Gets the storage file's path.
     */
    String getStorageFilePath();

    /**
     * Gets unmodifiable view of the current last shown list.
     */
    List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getLastShownSlots();

    /**
     * Returns an unmodifiable view of the list of commands entered by the user.
     * The list is ordered from the least recent command to the most recent command.
     */
    ObservableList<String> getHistory();
}
