package planmysem.logic.commands;

import static planmysem.common.Utils.getNearestDayOfWeek;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import planmysem.common.Clock;
import planmysem.common.Utils;
import planmysem.logic.CommandHistory;
import planmysem.model.Model;
import planmysem.model.semester.Day;
import planmysem.model.semester.Semester;
import planmysem.model.slot.Slot;

/**
 * View the planner.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";
    public static final String COMMAND_WORD_SHORT = "v";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": View month/week/day view or all details of planner."
            + "\n\tFormat: view [viewType] [specifier]"
            + "\n\tParameters:"
            + "\n\t\tMandatory: [viewType]"
            + "\n\t\tOptional: [specifier]"
            + "\n\tExample 1: " + COMMAND_WORD
            + " month"
            + "\n\tExample 2: " + COMMAND_WORD
            + " week 7"
            + "\n\tExample 3: " + COMMAND_WORD
            + " week recess"
            + "\n\tExample 4: " + COMMAND_WORD
            + " week"
            + "\n\tExample 5: " + COMMAND_WORD
            + " day 01/03/2019"
            + "\n\tExample 6: " + COMMAND_WORD
            + " day monday"
            + "\n\tExample 7: " + COMMAND_WORD
            + " day"
            + "\n\tExample 8: " + COMMAND_WORD
            + " all";

    private final String viewArgs;

    public ViewCommand(String viewArgs) {
        this.viewArgs = viewArgs;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) {
        String viewType;
        String viewSpecifier;
        final Semester currentSemester = model.getPlanner().getSemester();
        String output = null;

        if ("all".equals(viewArgs)) {
            //TODO: print all planner details
            output = "all";
        } else if ("month".equals(viewArgs)) {
            output = displayMonthView(currentSemester);
        } else if ("week".equals(viewArgs)) {
            output = displayWeekView(currentSemester, null);
        } else if ("day".equals(viewArgs)) {
            output = displayDayView(currentSemester, null);
        } else {
            viewType = viewArgs.split(" ")[0];
            viewSpecifier = viewArgs.split(" ")[1];

            switch (viewType) {
            case "week":
                output = displayWeekView(currentSemester, viewSpecifier);
                break;
            case "day":
                output = displayDayView(currentSemester, viewSpecifier);
                break;
            default:
                break;
            }
        }

        return new CommandResult(output);
    }

    /**
     * Display all months for the semester.
     */
    private String displayMonthView(Semester currentSemester) {
        HashMap<LocalDate, Day> allDays = currentSemester.getDays();
        LocalDate semesterStartDate = currentSemester.getStartDate();
        LocalDate semesterEndDate = currentSemester.getEndDate();
        int year = semesterStartDate.getYear();
        LocalDate firstDayOfMonth = semesterStartDate.withDayOfMonth(1);
        int spaces = firstDayOfMonth.getDayOfWeek().getValue();
        int lastMonthOfSem = semesterEndDate.getMonthValue();
        StringBuilder sb = new StringBuilder();

        String[] months = {"", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

        int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        for (int m = 1; m <= lastMonthOfSem; m++) {
            // Set number of days in February to 29 if it is a leap year.
            if ((((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) && m == 2) {
                days[m] = 29;
            }

            // Print calendar header.
            sb.append("          " + months[m] + " " + year + "\n");
            sb.append("_____________________________________\n");
            sb.append("   Sun  Mon Tue   Wed Thu   Fri  Sat\n");

            // Print spaces required for the start of a month.
            spaces = (days[m - 1] + spaces) % 7;
            for (int i = 0; i < spaces; i++) {
                sb.append("     ");
            }
            // Print the days in the month.
            for (int i = 1; i <= days[m]; i++) {
                sb.append(String.format("  %3d", i));
                if (((i + spaces) % 7 == 0)) {
                    Day tempDay = allDays.get(LocalDate.of(year, m, i));
                    String weekType = "";
                    if (tempDay != null) {
                        weekType = tempDay.getType();
                    }
                    sb.append("   | " + weekType + "\n");
                }
                if (i == days[m]) {
                    LocalDate tempDate = LocalDate.of(year, m, i);
                    Day tempDay = allDays.get(tempDate);
                    String weekType = "";
                    int extraSpaces = 6 - (tempDate.getDayOfWeek().getValue() % 7);
                    for (int j = 0; j < extraSpaces; j++) {
                        sb.append("     ");
                    }
                    if (tempDay != null) {
                        weekType = tempDay.getType();
                    }
                    sb.append("   | " + weekType + "\n");
                }
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Display all slots for a given week.
     */
    private String displayWeekView(Semester currentSemester, String week) {
        HashMap<LocalDate, Day> allDays = currentSemester.getDays();
        StringBuilder sb = new StringBuilder();

        if (week == null) {
            System.out.println(allDays.get(LocalDate.now()).getType());

        } else {
            HashMap<Integer, String> acadCal = currentSemester.getAcadCal();
            //System.out.println(currentSemester.getAcadCal());
            String key;
            int[] weekOfYear = {0, 0};
            List<LocalDate> datesList;

            if ("Recess".equals(week) || "Reading".equals(week) || "Examination".equals(week)
                    || "Orientation".equals(week)) {
                key = week + " Week" + "_" + currentSemester.getName();
                sb.append(week + "Week" + " of " + currentSemester.getName() + "\n");
            } else {
                key = "Week " + week + "_" + currentSemester.getName();
                sb.append("Week " + week + " of " + currentSemester.getName() + "\n");
            }
            //System.out.println(key);

            for (Map.Entry<Integer, String> entry: acadCal.entrySet()) {
                if (key.equals(entry.getValue())) {
                    if (weekOfYear[0] == 0) {
                        weekOfYear[0] = entry.getKey();
                    } else {
                        weekOfYear[1] = entry.getKey();
                    }
                }
            }
            //System.out.println(weekOfYear[0] + ", " + weekOfYear[1]);

            LocalDate weekStart = LocalDate.now().with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfYear[0]);
            weekStart = weekStart.with(WeekFields.ISO.dayOfWeek(), 1);
            LocalDate weekEnd = weekStart.with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfYear[0] + 1);
            datesList = weekStart.datesUntil(weekEnd).collect(Collectors.toList());
            //System.out.println(datesList);

            sb.append("__________________________________________________________________________\n\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            for (LocalDate date : datesList) {
                sb.append(displayDayView(currentSemester, date.format(formatter)));
                sb.append("__________________________________________________________________________\n\n");
            }

            // get acad cal map
            // swap key and value
            // find week of year from acad week
            // get all dates for week of year
            // get all slots for all dates found
            // format and print out all slots
        }

        return sb.toString();
    }

    /**
     * Display all slots for a given day/date.
     */
    private String displayDayView(Semester currentSemester, String dateOrDay) {
        HashMap<LocalDate, Day> allDays = currentSemester.getDays();
        StringBuilder sb = new StringBuilder();

        // Parse different formats of given day/date.
        LocalDate givenDate;
        if (dateOrDay == null) {
            givenDate = LocalDate.now();
        } else {
            int day = -1;
            givenDate = Utils.parseDate(dateOrDay);
            if (givenDate == null) {
                day = Utils.parseDay(dateOrDay);
            }
            if (day == -1 && givenDate == null) {
                return MESSAGE_USAGE;
            }
            if (day != -1) {
                givenDate = getNearestDayOfWeek(LocalDate.now(Clock.get()), day);
            }
        }
        sb.append(givenDate.getDayOfWeek().name() + " , " + givenDate + "\n\n");

        // Retrieve all slots for given day/date in sorted order.
        ArrayList<Slot> allSlotsInDay = allDays.get(givenDate).getSlots();
        Comparator<Slot> comparator = new Comparator<Slot>() {
            @Override
            public int compare(final Slot o1, final Slot o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        };
        allSlotsInDay.sort(comparator);

        // Print each slot.
        for (Slot slot : allSlotsInDay) {
            sb.append("* " + slot.getStartTime());
            sb.append(" to ");
            sb.append(Utils.getEndTime(slot.getStartTime(), slot.getDuration()));

            sb.append("\n\t" + slot.getName() + "\n");
            sb.append("\t" + "Location: " + slot.getLocation() + "\n");
            sb.append("\t" + "Description: " + slot.getDescription() + "\n");
            sb.append("\n\tTags: \n");

            int count = 1;
            for (String tag : slot.getTags()) {
                sb.append("\t");
                sb.append(count);
                sb.append(". ");
                sb.append(tag);
                count++;
            }

            sb.append("\n\n");
        }

        return sb.toString();
    }
}
