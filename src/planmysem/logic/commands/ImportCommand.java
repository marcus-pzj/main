package planmysem.logic.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import planmysem.logic.CommandHistory;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.model.Model;
import planmysem.model.recurrence.Recurrence;
import planmysem.model.semester.Day;
import planmysem.model.semester.Semester;
import planmysem.model.slot.Slot;

/**
 * Imports a .ics file into the Planner.
 */
public class ImportCommand extends Command {
    public static final String COMMAND_WORD = "import";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Imports a .ics file into the Planner."
            + "\n\tParameters: "
            + "\n\t\tMandatory: fn/FILENAME"
            + "\n\tExample: " + COMMAND_WORD + " my_outlook_calendar.ics";
    public static final String MESSAGE_SUCCESS = "File imported.\n";
    public static final String MESSAGE_FILE_NOT_FOUND = "File not found.\n";
    public static final String MESSAGE_ERROR_IN_READING_FILE = "Error in reading file.\n";

    private final String fileName;
    private int failedImports = 0;

    public ImportCommand(String fileName) {
        if (!fileName.endsWith(".ics")) {
            this.fileName = fileName.concat(".ics");
        } else {
            this.fileName = fileName;
        }
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) throws CommandException {
        try {
            FileReader fileReader;
            fileReader = new FileReader(this.fileName);
            BufferedReader br = new BufferedReader(fileReader);
            String sCurrentLine = br.readLine();
            while (!("END:VCALENDAR".equals(sCurrentLine))) {
                sCurrentLine = br.readLine();
                if ("BEGIN:VEVENT".equals(sCurrentLine)) {
                    LocalDate date = null;
                    String name = null;
                    String location = null;
                    String description = null;
                    LocalTime startTime = null;
                    Set<String> tags = new HashSet<>();
                    int duration = 0;
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

                    while (!("END:VEVENT".equals(sCurrentLine))) {
                        sCurrentLine = br.readLine();
                        String[] sSplit = sCurrentLine.split(":");
                        switch (sSplit[0]) {
                        case "SUMMARY":
                            name = sSplit[1];
                            break;

                        case "DTSTART":
                            date = LocalDate.parse(sSplit[1], dateFormat);
                            startTime = LocalTime.parse(sSplit[1], dateFormat);
                            break;

                        case "DTEND":
                            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
                            LocalDateTime endDateTime = LocalDateTime.parse(sSplit[1], dateFormat);
                            duration = (int) startDateTime.until(endDateTime, ChronoUnit.MINUTES);
                            break;

                        case "LOCATION":
                            location = sSplit[1];
                            break;

                        case "DESCRIPTION":
                            description = sSplit[1];
                            break;

                        case "X-TAGS":
                            String[] tagArray = sSplit[1].split(",");
                            tags.addAll(Arrays.asList(tagArray));
                            break;

                        default:
                            break;

                        }
                    }

                    Slot slot = new Slot(name, location, description, startTime, duration, tags);
                    Recurrence recurrence = new Recurrence(null, date);
                    Set<LocalDate> dates = recurrence.generateDates(model.getPlanner().getSemester());
                    Map<LocalDate, Day> days = new TreeMap<>();

                    for (LocalDate singleDate : dates) {
                        try {
                            days.put(singleDate, model.addSlot(date, slot));
                        } catch (Semester.DateNotFoundException dnfe) {
                            this.failedImports++;
                        }
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            throw new CommandException(MESSAGE_ERROR_IN_READING_FILE);
        }
        if (this.failedImports == 0) {
            return new CommandResult(MESSAGE_SUCCESS);
        } else {
            return new CommandResult(MESSAGE_SUCCESS + this.failedImports + " event(s) failed to import.\n");
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ImportCommand // instanceof handles nulls
                && fileName.equals(((ImportCommand) other).fileName));
    }

}


