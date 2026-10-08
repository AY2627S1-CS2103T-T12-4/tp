package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyTAssist;

/**
 * A class to access TAssist data stored as a JSON file on the hard disk.
 */
public class JsonTAssistStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonTAssistStorage.class);

    private final Path filePath;

    /**
     * Creates a {@code JsonTAssistStorage} that reads from and saves to {@code filePath}.
     */
    public JsonTAssistStorage(Path filePath) {
        requireNonNull(filePath);
        this.filePath = filePath;
    }

    public Path getTAssistFilePath() {
        return filePath;
    }

    /**
     * Returns TAssist data as a {@link ReadOnlyTAssist}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyTAssist> readTAssist() throws DataLoadingException {
        return readTAssist(filePath);
    }

    /**
     * Similar to {@link #readTAssist()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyTAssist> readTAssist(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableTAssist> jsonTAssist = JsonUtil.readJsonFile(
                filePath, JsonSerializableTAssist.class);
        if (jsonTAssist.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonTAssist.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyTAssist} to the storage.
     *
     * @param tAssist cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveTAssist(ReadOnlyTAssist tAssist) throws IOException {
        saveTAssist(tAssist, filePath);
    }

    /**
     * Similar to {@link #saveTAssist(ReadOnlyTAssist)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveTAssist(ReadOnlyTAssist tAssist, Path filePath) throws IOException {
        requireNonNull(tAssist);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableTAssist(tAssist), filePath);
    }

}
