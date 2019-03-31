package planmysem.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
 * Represents the entire Planner. Contains the model of the Planner.
 */
public class ModelManager implements Model {
    protected List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> lastShownList = new ArrayList<>();
    private final VersionedPlanner versionedPlanner;

    /**
     * Creates an empty planner.
     */
    public ModelManager() {
        versionedPlanner = new VersionedPlanner(new Planner());
    }

    /**
     * Constructs a Planner with the given model.
     *
     * @param planner external changes to this will not affect this Planner
     */
    public ModelManager(ReadOnlyPlanner planner) {
        versionedPlanner = new VersionedPlanner(planner);
    }

    @Override
    public void setLastShownList(List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> list) {
        lastShownList.clear();

        if (list != null) {
            lastShownList.addAll(list);
        }
    }

    public void setLastShownList(Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> list) {
        lastShownList.clear();

        if (list != null) {
            for (Map.Entry<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> entry : list.entrySet()) {
                lastShownList.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
        }
    }

    @Override
    public void clearLastShownList() {
        lastShownList.clear();
    }


    @Override
    public void commit() {
        versionedPlanner.commit();
    }

    @Override
    public List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getLastShownList() {
        return lastShownList;
    }

    @Override
    public Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> getLastShownItem(int index) {
        if (lastShownList == null || lastShownList.size() < index) {
            throw new IndexOutOfBoundsException();
        }

        return lastShownList.get(index - 1);
    }

    @Override
    public Day addSlot(LocalDate date, Slot slot) throws Semester.DateNotFoundException {
        return versionedPlanner.addSlot(date, slot);
    }

    @Override
    public void removeSlot(LocalDate date, ReadOnlySlot slot) {
        versionedPlanner.removeSlot(date, slot);
    }

    @Override
    public void removeSlot(Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slot) {
        versionedPlanner.removeSlot(slot.getKey(), slot.getValue().getValue());
    }

    @Override
    public void editSlot(LocalDate targetDate, ReadOnlySlot targetSlot, LocalDate date,
                         LocalTime startTime, int duration, String name, String location,
                         String description, Set<String> tags) {
        versionedPlanner.editSlot(targetDate, targetSlot, date, startTime, duration, name, location, description, tags);
    }

    @Override
    public void clearSlots() {
        versionedPlanner.clearSlots();
    }

    @Override
    public Planner getPlanner() {
        return versionedPlanner;
    }

    @Override
    public HashMap<LocalDate, Day> getDays() {
        return versionedPlanner.getDays();
    }

    @Override
    public Day getDay(LocalDate date) {
        return getDays().get(date);
    }

    @Override
    public Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> getSlots(Set<String> tags) {
        return versionedPlanner.getSlots(tags);
    }

    @Override
    public boolean canUndo() {
        return versionedPlanner.canUndo();
    }

    @Override
    public boolean canRedo() {
        return versionedPlanner.canRedo();
    }

    @Override
    public void undo() {
        versionedPlanner.undo();
    }

    @Override
    public void redo() {
        versionedPlanner.redo();
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return versionedPlanner.equals(other.versionedPlanner)
                && lastShownList.equals(other.lastShownList);
    }
}
