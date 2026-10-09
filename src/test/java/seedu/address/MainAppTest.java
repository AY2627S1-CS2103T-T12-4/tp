package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.Model;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonTAssistStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class MainAppTest {

    @TempDir
    public Path temporaryFolder;

    private final MainApp mainApp = new MainApp();

    @Test
    public void initModelManager_missingDataFile_startsEmpty() {
        Model model = mainApp.initModelManager(createStorage(), new UserPrefs());
        assertEquals(new TAssist(), model.getTAssist());
    }

    @Test
    public void initModelManager_validDataFile_restoresData() throws Exception {
        TAssist saved = new TAssistBuilder(TypicalGroups.getTypicalTAssist())
                .withActiveGroup(TypicalGroups.NAME_T02).build();
        StorageManager storage = createStorage();
        storage.saveTAssist(saved);

        Model model = mainApp.initModelManager(storage, new UserPrefs());

        assertEquals(saved, model.getTAssist());
        assertEquals(TypicalGroups.getT02().getStudentList(), model.getActiveGroupStudentList());
    }

    @Test
    public void initModelManager_invalidDataFile_startsEmptyAndKeepsFile() throws Exception {
        String unknownActiveGroup = "{ \"activeGroup\" : \"T09\", \"groups\" : [ ] }";
        String missingBrackets = "{ \"groups\" : [ { \"name\" : \"T01\" }";
        for (String invalidData : List.of(unknownActiveGroup, missingBrackets, "null")) {
            Files.writeString(dataFile(), invalidData);

            Model model = mainApp.initModelManager(createStorage(), new UserPrefs());

            assertEquals(new TAssist(), model.getTAssist(), invalidData);
            assertEquals(invalidData, Files.readString(dataFile()));
        }
    }

    private Path dataFile() {
        return temporaryFolder.resolve("tassist.json");
    }

    private StorageManager createStorage() {
        return new StorageManager(new JsonTAssistStorage(dataFile()),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
    }
}
