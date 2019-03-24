package planmysem.logic;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBException;

import javafx.util.Pair;
import planmysem.commands.Command;
import planmysem.commands.CommandResult;
import planmysem.data.Planner;
import planmysem.data.semester.ReadOnlyDay;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.parser.Parser;
import planmysem.storage.StorageFile;

/**
 * Represents the main Logic of the Planner.
 */
public class Logic {
    private StorageFile storage;
    private Planner planner;

    /**
     * The list of Slots shown to the user most recently.
     */
    private Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> lastShownSlots;

    public Logic() throws Exception {
        setStorage(initializeStorage());
        setPlanner(storage.load());
    }

    public Logic(StorageFile storageFile, Planner planner) {
        setStorage(storageFile);
        setPlanner(planner);
    }

    public void setStorage(StorageFile storage) {
        this.storage = storage;
    }

    public void setPlanner(Planner planner) {
        this.planner = planner;
    }

    /**
     * Creates the StorageFile object based on the user specified path (if any) or the default storage path.
     *
     * @throws StorageFile.InvalidStorageFilePathException if the target file path is incorrect.
     */
    private StorageFile initializeStorage() throws JAXBException, StorageFile.InvalidStorageFilePathException {
        return new StorageFile();
    }

    public String getStorageFilePath() {
        return storage.getPath();
    }

    /**
     * Unmodifiable view of the current last shown list.
     */
    public Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> getLastShownSlots() {
        return lastShownSlots;
    }

    protected void setLastShownSlots(Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slots) {
        lastShownSlots = slots;
    }

    /**
     * Parses the user command, executes it, and returns the result.
     *
     * @throws Exception if there was any problem during command execution.
     */
    public CommandResult execute(String userCommandText) throws Exception {
        Command command = new Parser().parseCommand(userCommandText);
        CommandResult result = execute(command);
        recordResult(result);
        return result;
    }

    /**
     * Executes the command, updates storage, and returns the result.
     *
     * @param command user command
     * @return result of the command
     * @throws Exception if there was any problem during command execution.
     */
    private CommandResult execute(Command command) throws Exception {
        command.setData(planner, lastShownSlots);
        CommandResult result = command.execute();
        storage.save(planner);
        return result;
    }

    /**
     * Updates the {@link #lastShownSlots} if the result contains a list of Days.
     */
    private void recordResult(CommandResult result) {
        final Optional<Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> slots = result.getRelevantSlots();
        if (slots.isPresent()) {
            lastShownSlots = slots.get();
        }
    }
}
