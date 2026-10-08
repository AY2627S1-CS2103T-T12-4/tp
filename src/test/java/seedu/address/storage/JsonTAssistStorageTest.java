package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.FIONA;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyTAssist;
import seedu.address.model.TAssist;
import seedu.address.model.group.GroupName;
import seedu.address.testutil.GroupBuilder;
import seedu.address.testutil.TypicalGroups;

public class JsonTAssistStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonTAssistStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void constructor_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new JsonTAssistStorage(null));
    }

    @Test
    public void readTAssist_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readTAssist(null));
    }

    private Optional<ReadOnlyTAssist> readTAssist(String filePath) throws Exception {
        return new JsonTAssistStorage(Paths.get("unused.json")).readTAssist(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String fileInTestDataFolder) {
        return fileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(fileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readTAssist("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readTAssist("notJsonFormatTAssist.json"));
    }

    @Test
    public void read_wrongJsonStructure_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readTAssist("wrongStructureTAssist.json"));
    }

    @Test
    public void read_emptyFile_exceptionThrown() throws Exception {
        Path emptyFile = Files.createFile(testFolder.resolve("empty.json"));
        assertThrows(DataLoadingException.class, () -> new JsonTAssistStorage(emptyFile).readTAssist());
    }

    @Test
    public void read_invalidAndValidGroups_exceptionThrown() {
        assertReadFailsWithReason("invalidAndValidGroupTAssist.json", GroupName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void read_unknownActiveGroup_exceptionThrown() {
        assertReadFailsWithReason("unknownActiveGroupTAssist.json",
                String.format(JsonSerializableTAssist.MESSAGE_UNKNOWN_ACTIVE_GROUP, "T09"));
    }

    /**
     * Asserts that reading {@code fileInTestDataFolder} fails because of {@code expectedReason}.
     */
    private void assertReadFailsWithReason(String fileInTestDataFolder, String expectedReason) {
        DataLoadingException e = Assertions.assertThrows(DataLoadingException.class, () ->
                readTAssist(fileInTestDataFolder));
        assertEquals(expectedReason, e.getCause().getMessage());
    }

    @Test
    public void readAndSaveTAssist_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("data").resolve("TempTAssist.json");
        TAssist original = TypicalGroups.getTypicalTAssist();
        JsonTAssistStorage jsonTAssistStorage = new JsonTAssistStorage(filePath);

        // Save in a new file and folder, and read back
        jsonTAssistStorage.saveTAssist(original, filePath);
        ReadOnlyTAssist readBack = jsonTAssistStorage.readTAssist(filePath).orElseThrow();
        assertEquals(original, new TAssist(readBack));

        // Modify data, overwrite existing file, and read back
        original.addGroup(new GroupBuilder().withName(TypicalGroups.NAME_T04).build());
        original.setActiveGroup(TypicalGroups.NAME_T04);
        jsonTAssistStorage.saveTAssist(original, filePath);
        readBack = jsonTAssistStorage.readTAssist(filePath).orElseThrow();
        assertEquals(original, new TAssist(readBack));

        // Save and read without specifying file path
        original.addStudent(TypicalGroups.NAME_T04, FIONA);
        jsonTAssistStorage.saveTAssist(original);
        readBack = jsonTAssistStorage.readTAssist().orElseThrow();
        assertEquals(original, new TAssist(readBack));
    }

    @Test
    public void saveTAssist_emptyTAssist_readsBackEmpty() throws Exception {
        JsonTAssistStorage jsonTAssistStorage = new JsonTAssistStorage(testFolder.resolve("Empty.json"));
        jsonTAssistStorage.saveTAssist(new TAssist());
        assertEquals(new TAssist(), new TAssist(jsonTAssistStorage.readTAssist().orElseThrow()));
    }

    @Test
    public void saveTAssist_nullTAssist_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveTAssist(null, "SomeFile.json"));
    }

    @Test
    public void saveTAssist_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveTAssist(new TAssist(), null));
    }

    /**
     * Saves {@code tAssist} at the specified {@code filePath}.
     */
    private void saveTAssist(ReadOnlyTAssist tAssist, String filePath) {
        try {
            new JsonTAssistStorage(Paths.get("unused.json"))
                    .saveTAssist(tAssist, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }
}
