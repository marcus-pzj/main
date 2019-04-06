package planmysem.logic.commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import planmysem.logic.CommandHistory;
import planmysem.model.Model;
import planmysem.model.semester.IcsSemester;

/**
 * Exports the calendar into a .ics file.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Export the planner as a .ics file."
            + "\n\tParameters: filename"
            + "\n\tExample: " + COMMAND_WORD + " my_planner.ics";
    public static final String MESSAGE_EXPORT_ACKNOWLEDGEMENT = "Calendar exported";

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) {
        IcsSemester semester = new IcsSemester(model.getPlanner().getSemester());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("PlanMySem.ics"));
            writer.write(semester.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new CommandResult(MESSAGE_EXPORT_ACKNOWLEDGEMENT);
    }

}
