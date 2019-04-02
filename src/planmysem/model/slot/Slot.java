package planmysem.model.slot;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import planmysem.common.Utils;

/**
 * Represents a slot in the planner.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Slot implements ReadOnlySlot {
    private final Set<String> tags = new HashSet<>();
    private String name;
    private String location;
    private String description;
    private LocalTime startTime;
    private int duration;

    /**
     * Assumption: Every field must be present and not null.
     */
    public Slot(String name, String location, String description,
                LocalTime startTime, LocalTime endTime, Set<String> tags) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.duration = Utils.getDuration(startTime, endTime);
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Assumption: Every field must be present and not null.
     */
    public Slot(String name, String location, String description,
                LocalTime startTime, int duration, Set<String> tags) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Copy constructor.
     */
    public Slot(ReadOnlySlot source) {
        this(source.getName(), source.getLocation(), source.getDescription(),
                source.getStartTime(), source.getDuration(), source.getTags());
    }

    /**
     * Set name.
     */
    public void setName(String value) {
        if (value == null) {
            return;
        }
        name = value;
    }

    /**
     * Set location.
     */
    public void setLocation(String value) {
        if (value == null) {
            return;
        }
        location = value;
    }

    /**
     * Set description.
     */
    public void setDescription(String value) {
        if (value == null) {
            return;
        }
        description = value;
    }

    /**
     * Set start time.
     */
    public void setStartTime(LocalTime value) {
        if (value == null) {
            return;
        }
        startTime = value;
    }

    /**
     * Replaces this slot's tags with the tags in {@code replacement}.
     */
    public void setTags(Set<String> tags) {
        if (tags == null) {
            return;
        }
        this.tags.clear();
        this.tags.addAll(tags);
    }

    /**
     * Set duration.
     */
    public void setDuration(int value) {
        duration = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlySlot // instanceof handles nulls
                && this.isSameStateAs((ReadOnlySlot) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, location, description, startTime, duration, tags);
    }

    @Override
    public String toString() {
        return getAsTextShowAll();
    }

}
