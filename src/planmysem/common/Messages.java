package planmysem.common;

import java.util.Set;

import planmysem.data.tag.Tag;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL = "Invalid command format! \n%1$s\n\n%2$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_INVALID_MULTIPLE_PARAMS = "Either search by NAME or by TAG only, not both.";
    public static final String MESSAGE_INVALID_SLOT_DISPLAYED_INDEX = "The slot index provided is invalid";
    public static final String MESSAGE_PERSON_NOT_IN_ADDRESSBOOK = "Person could not be found in address book";
    public static final String MESSAGE_SLOT_NOT_IN_PLANNER = "Slot could not be found in Planner";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_SLOTS_LISTED_OVERVIEW = "%1$d slots listed!";
    public static final String MESSAGE_PROGRAM_LAUNCH_ARGS_USAGE = "Launch command format: "
            + "java Main [STORAGE_FILE_PATH]";
    public static final String MESSAGE_WELCOME = "Welcome to PlanMySem!";
    public static final String MESSAGE_USING_STORAGE_FILE = "Using storage file : %1$s";

    public static final String MESSAGE_INVALID_DATE = "Date have to be in either these two formats:"
            + "\n\tIn the form of \"dd-mm\". e.g. \"01-01\""
            + "\n\tIn the form of \"dd-mm-yyyy\". e.g. \"01-01-2019\""
            + "\n\tOr perhaps target the next day of week. e.g. \"Monday\", \"mon\", \"1\"";

    public static final String MESSAGE_INVALID_TIME = "Time have to be in either these two formats:"
            + "\n\t24-Hour in the form of “hh:mm”. e.g. \"23:00\""
            + "\n\t12-Hour in the form of `hh:mm+AM|PM`. e.g. \"12:30am\""
            + "\n\tOr perhaps type a duration in minutes. e.g. \"60\" to represent 60 minutes";


    /**
     * Craft selected message.
     */
    public static String craftSelectedMessage(Set<Tag> tags) {
        StringBuilder sb = new StringBuilder();
        sb.append("Selected Slots containing tags: \n");

        int count = 1;
        for (Tag tag : tags) {
            sb.append(count);
            sb.append(".\t");
            sb.append(tag.toString());
            sb.append("\n");
            count++;
        }

        return sb.toString();
    }


}
