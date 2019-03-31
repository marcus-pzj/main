package planmysem.storage;

import planmysem.common.exceptions.IllegalValueException;
import planmysem.model.Planner;

/**
 * API of the Logic component
 */
public interface Storage {

    /**
     * Saves all model to this storage file.
     *
     * @throws StorageFile.StorageOperationException if there were errors converting and/or storing model to file.
     */
    void save(Planner planner) throws StorageFile.StorageOperationException;

    /**
     * Loads model from this storage file.
     *
     * @throws StorageFile.StorageOperationException if there were errors reading and/or converting model from file.
     */
    Planner load() throws StorageFile.StorageOperationException;

    /**
     * Gets path of file.
     **/
    String getPath();

    /**
     * Signals that the given file path does not fulfill the storage filepath constraints.
     */
    class InvalidStorageFilePathException extends IllegalValueException {
        public InvalidStorageFilePathException(String message) {
            super(message);
        }
    }

    /**
     * Signals that some error has occured while trying to convert and read/write model between the application
     * and the storage file.
     */
    class StorageOperationException extends Exception {
        public StorageOperationException(String message) {
            super(message);
        }
    }
}
