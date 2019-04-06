package planmysem.logic.Commands;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import planmysem.common.Clock;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.semester.IcsSemester;

public class ExportCommandTest {
    private Model model = new ModelManager();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
    public void execute_export_success() throws IOException {
        IcsSemester semester = new IcsSemester(model.getPlanner().getSemester());

        String expectedIcs = "BEGIN:VCALENDAR\r\nVERSION:2.0\r\nEND:VCALENDAR\r\n";
        String actualIcs = semester.toString();
        Assert.assertEquals(actualIcs, expectedIcs);
    }

    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }
}
