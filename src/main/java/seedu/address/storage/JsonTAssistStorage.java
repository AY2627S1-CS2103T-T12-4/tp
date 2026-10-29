package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
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

        Optional<JsonSerializableTAssist> jsonTAssist = JsonUtil.readJsonFileStrictly(
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
     * @param tAssist cannot be null.
     * @param filePath location of the data. Cannot be null.
     */
    public void saveTAssist(ReadOnlyTAssist tAssist, Path filePath) throws IOException {
        requireNonNull(tAssist);
        requireNonNull(filePath);

        String json = JsonUtil.toJsonString(new JsonSerializableTAssist(tAssist));
        Path target = filePath.toAbsolutePath();
        if (Files.exists(target) && !Files.isWritable(target)) {
            throw new AccessDeniedException(target.toString());
        }
        Files.createDirectories(target.getParent());
        Path temporaryFile = Files.createTempFile(target.getParent(), ".tassist-", ".tmp");
        try {
            writeTemporaryFile(temporaryFile, json);
            replaceDataFile(temporaryFile, target);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Writes the complete JSON to a temporary file before the saved file is touched.
     */
    void writeTemporaryFile(Path temporaryFile, String json) throws IOException {
        Files.writeString(temporaryFile, json);
    }

    /**
     * Atomically replaces the saved file, failing safely if atomic moves are unsupported.
     */
    void replaceDataFile(Path temporaryFile, Path target) throws IOException {
        Files.move(temporaryFile, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    private void deleteTemporaryFile(Path temporaryFile) {
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException e) {
            logger.warning("Could not remove temporary data file " + temporaryFile + ": " + e);
        }
    }

}
