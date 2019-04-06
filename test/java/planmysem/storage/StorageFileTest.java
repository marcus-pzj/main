package planmysem.storage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import planmysem.common.Clock;
import planmysem.common.exceptions.IllegalValueException;
import planmysem.model.Planner;
import planmysem.model.slot.Slot;

public class StorageFileTest {
    private static final String TEST_DATA_FOLDER = "test/model/StorageFileTest";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
    public void constructor_nullFilePath_exceptionThrown() throws Exception {
        thrown.expect(NullPointerException.class);
        new StorageFile(null);
    }

    @Test
    public void constructor_noTxtExtension_exceptionThrown() throws Exception {
        thrown.expect(IllegalValueException.class);
        new StorageFile(TEST_DATA_FOLDER + "/" + "InvalidfileName");
    }

    //    @Test
    //    public void load_invalidData_ThrowIllegalBlockSizeException() throws Exception {
    //        StorageFile storage = getStorage("InvalidData.txt");
    //        thrown.expect(Storage.StorageOperationException.class);
    //        storage.load();
    //    }

    //    @Test
    //    public void load_validFormat() throws Exception {
    //        Planner actualPlanner = getStorage(TEST_DATA_FOLDER + "/" +"ValidData.txt").load();
    //        Planner expectedPlanner = getTestPlanner();
    //
    //        assertEquals(actualPlanner.getSemester(), expectedPlanner.getSemester());
    //    }

    @Test
    public void save_nullPlanner_exceptionThrown() throws Exception {
        StorageFile storage = getTempStorage();
        thrown.expect(NullPointerException.class);
        storage.save(null);
    }

    //    @Test
    //    public void save_validPlanner() throws Exception {
    //        Planner actualPlanner = getTestPlanner();
    //        StorageFile storage = getTempStorage();
    //        storage.save(actualPlanner);
    //
    //        assertEquals(storage.load().getSemester(),
    //                getStorage(TEST_DATA_FOLDER + "/" +"ValidData.txt").load().getSemester());
    //    }

    private StorageFile getStorage(String fileName) throws Exception {
        return new StorageFile(TEST_DATA_FOLDER + "/" + fileName);
    }

    private StorageFile getTempStorage() throws Exception {
        return new StorageFile(temporaryFolder.getRoot().getPath() + "/" + "temp.txt");
    }

    private Planner getTestPlanner() throws Exception {
        Planner planner = new Planner();

        planner.addSlot(LocalDate.of(2019, 4, 5),
                new Slot("CS2113T Tutorial",
                        null,
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        60,
                        new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))
                )
        );

        planner.addSlot(LocalDate.of(2019, 4, 8),
                new Slot("CS2113T Tutorial",
                        null,
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        60,
                        new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))
                )
        );

        return planner;
    }
}
