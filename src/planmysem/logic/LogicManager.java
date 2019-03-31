package planmysem.logic;

import java.time.LocalDate;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import planmysem.logic.commands.Command;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.logic.parser.ParserManager;
import planmysem.logic.parser.exceptions.ParseException;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.storage.Storage;
import planmysem.storage.StorageFile;

/**
 * Represents the main LogicManager of the Planner.
 */
public class LogicManager implements Logic {
    public static final String STORAGE_ERROR = "Could not save data to file: ";

    private final Storage storageFile;
    private final Model model;
    private final CommandHistory history;
    private final ParserManager parserManager;

    public LogicManager(Storage storageFile) throws Exception {
        this.storageFile = storageFile;
        this.model = new ModelManager(storageFile.load());
        this.history = new CommandHistory();
        this.parserManager = new ParserManager();
    }

    @Override
    public String getStorageFilePath() {
        return storageFile.getPath();
    }

    @Override
    public List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getLastShownSlots() {
        return model.getLastShownList();
    }

    @Override
    public CommandResult execute(String userCommandText) throws CommandException, ParseException {
        CommandResult result;
        try {
            Command command = parserManager.parseCommand(userCommandText);
            result = command.execute(model, history);
        } finally {
            history.add(userCommandText);
        }
        try {
            storageFile.save(model.getPlanner());
        } catch (StorageFile.StorageOperationException soe) {
            throw new CommandException(STORAGE_ERROR + soe, soe);
        }

        return result;
    }

    @Override
    public ObservableList<String> getHistory() {
        return history.getHistory();
    }


    //    /**
    //     * Creates the StorageFile object based on the user specified path (if any) or the default storageFile path.
    //     *
    //     * @throws StorageFile.InvalidStorageFilePathException if the target file path is incorrect.
    //     */
    //    private StorageFile initializeStorage() throws JAXBException, StorageFile.InvalidStorageFilePathException {
    //        return new StorageFile();
    //    }

    //    /**
    //     * Updates the {@link #lastShownSlots} if the commandResult contains a list of Days.
    //     */
    //    private void recordResult(CommandResult commandResult) {
    //        final Optional<Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> slots = commandResult.getRelevantSlots();
    //        if (slots.isPresent()) {
    //            lastShownSlots = slots.get();
    //        }
    //    }
}
