package planmysem.storage.jaxb;

import static planmysem.common.Messages.MESSAGE_ILLEGAL_VALUE;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import planmysem.common.Utils;
import planmysem.common.exceptions.IllegalValueException;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;

/**
 * JAXB-friendly adapted person model holder class.
 */
public class AdaptedSlot {
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String location;
    @XmlElement(required = true)
    private String description;
    @XmlElement(required = true)
    private int duration;
    @XmlElement(required = true)
    private String startTime;
    @XmlElement(required = true)
    private List<String> tags = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedSlot() {
    }

    /**
     * Converts a given Person into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedPerson
     */
    public AdaptedSlot(ReadOnlySlot source) {
        name = source.getName();
        location = source.getLocation();
        description = source.getDescription();
        duration = source.getDuration();
        startTime = source.getStartTime().toString();

        tags = new ArrayList<>();
        tags.addAll(source.getTags());
    }

    /**
     * Returns true if any required field is missing.
     */
    public boolean isAnyRequiredFieldMissing() {
        for (String tag : tags) {
            if (tag.isEmpty()) {
                return true;
            }
        }

        // second call only happens if name, duration, start time are all not null
        return Utils.isAnyNull(name, duration, startTime);
    }

    /**
     * Converts this jaxb-friendly adapted person object into the Person object.
     *
     * @throws IllegalValueException if there were any model constraints violated in the IcsSemester
     */
    public Slot toModelType() throws IllegalValueException {
        if ((name != null && hasIllegalValues(name)) || (location != null && hasIllegalValues(location))
                || (description != null && hasIllegalValues(description)) || duration < 0) {
            throw new IllegalValueException(MESSAGE_ILLEGAL_VALUE);
        }

        final LocalTime startTime;

        try {
            startTime = LocalTime.parse(this.startTime);
        } catch (DateTimeParseException dtpe) {
            throw new IllegalValueException(MESSAGE_ILLEGAL_VALUE);
        }

        final Set<String> tags = new HashSet<>();
        for (String tag : this.tags) {
            if (hasIllegalValues(tag)) {
                throw new IllegalValueException(MESSAGE_ILLEGAL_VALUE);
            }
            tags.add(tag);
        }

        return new Slot(name, location, description, startTime, duration, tags);
    }

    /**
     * Returns true if value has any illegal values.
     */
    private static boolean hasIllegalValues(String value) {
        return value.contains("/");
    }
}
