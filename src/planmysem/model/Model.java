package planmysem.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.semester.Semester;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;

/**
 * The API of the Model component.
 */
public interface Model {

    /**
     * Set last shown list.
     */
    void setLastShownList(List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> list);

    void setLastShownList(Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> list);

    void clearLastShownList();

    /**
     * Saves the current planner state for undo/redo.
     */
    void commit();

    /**
     * Get last shown list.
     */
    List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getLastShownList();

    /**
     * Get item in last shown list.
     */
    Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> getLastShownItem(int index);

    /**
     * Adds a slot to the Planner.
     */
    Day addSlot(LocalDate date, Slot slot) throws Semester.DateNotFoundException;

    /**
     * Removes a Slot in the Planner.
     */
    void removeSlot(LocalDate date, ReadOnlySlot slot);

    /**
     * Removes a Slot in the Planner.
     */
    void removeSlot(Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slot);

    /**
     * Edit specific slot within the planner.
     */
    void editSlot(LocalDate targetDate, ReadOnlySlot targetSlot, LocalDate date,
                         LocalTime startTime, int duration, String name, String location,
                         String description, Set<String> tags);

    /**
     * Clears all slots from the Planner.
     */
    void clearSlots();

    /**
     * gets all days in the Planner.
     */
    HashMap<LocalDate, Day> getDays();
    /**
     * Defensively copy the Semester in the Planner at the time of the call.
     */
    Planner getPlanner();

    /**
     * gets specific day in the Planner.
     */
    Day getDay(LocalDate date);

    /**
     * check if Slot exists in some day.
     */
    boolean slotExists(LocalDate date, ReadOnlySlot slot);

    /**
     * gets all slots in the Planner containing all specified tags.
     */
    Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> getSlots(Set<String> tags);

    /**
     * Returns true if the model has previous Planner states to restore.
     */
    boolean canUndo();

    /**
     * Returns true if the model has undone Planner states to restore.
     */
    boolean canRedo();

    /**
     * Restores the model's Planner to its previous state.
     */
    void undo();

    /**
     * Restores the model's Planner to its previously undone state.
     */
    void redo();

}

