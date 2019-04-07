//@@author marcus-pzj
package planmysem.model.semester;

import java.time.LocalDate;
import java.util.Map;

import planmysem.model.slot.Slot;

/**
 * WeightedName of integer and string
 */

public class WeightedName {
    private int dist;
    private String name;
    private Map.Entry<LocalDate, Day> map;
    private Slot slot;
    private LocalDate date;

    public WeightedName(Map.Entry<LocalDate, Day> map, Slot slot, LocalDate date, int dist) {
        this.map = map;
        this.dist = dist;
        this.slot = slot;
        this.name = slot.getName();
        this.date = date;
    }

    public int getDist() {
        return this.dist;
    }

    public Map.Entry<LocalDate, Day> getMap() {
        return map;
    }

    public String getName() {
        return this.name;
    }

    public Slot getSlot() {
        return this.slot;
    }

    public LocalDate getDate() {
        return this.date;
    }
}
