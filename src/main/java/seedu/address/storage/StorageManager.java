package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyTAssist;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of AddressBook data, TAssist data and user prefs in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonAddressBookStorage addressBookStorage;
    private JsonTAssistStorage tAssistStorage;
    private JsonUserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given address book, TAssist and user prefs storage.
     */
    public StorageManager(JsonAddressBookStorage addressBookStorage, JsonTAssistStorage tAssistStorage,
            JsonUserPrefsStorage userPrefsStorage) {
        this.addressBookStorage = addressBookStorage;
        this.tAssistStorage = tAssistStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + addressBookStorage.getAddressBookFilePath());
        return addressBookStorage.readAddressBook();
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        logger.fine("Attempting to write to data file: " + addressBookStorage.getAddressBookFilePath());
        addressBookStorage.saveAddressBook(addressBook);
    }

    // ================ TAssist methods ==============================

    @Override
    public Path getTAssistFilePath() {
        return tAssistStorage.getTAssistFilePath();
    }

    @Override
    public Optional<ReadOnlyTAssist> readTAssist() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + tAssistStorage.getTAssistFilePath());
        return tAssistStorage.readTAssist();
    }

    @Override
    public void saveTAssist(ReadOnlyTAssist tAssist) throws IOException {
        logger.fine("Attempting to write to data file: " + tAssistStorage.getTAssistFilePath());
        tAssistStorage.saveTAssist(tAssist);
    }

}
