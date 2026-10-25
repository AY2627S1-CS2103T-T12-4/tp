package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.stage.Stage;
import seedu.address.commons.core.DataLoadStatus;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonTAssistStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;
import seedu.address.ui.Ui;

public class MainAppTest {

    @TempDir
    public Path temporaryFolder;

    private final MainApp mainApp = new MainApp();

    @Test
    public void init_groupCommandsAndHelp_areRegistered() throws Exception {
        MainApp app = new MainApp(temporaryFolder.resolve("preferences.json"),
                temporaryFolder.resolve("tassist.json"));
        app.init();

        assertEquals("No tutorial groups exist. Create one using: group add g/GROUP_NAME",
                app.logic.execute("group list").getFeedbackToUser());
        assertEquals("Created tutorial group: T01.\nActive group is now T01.",
                app.logic.execute("group add g/T01").getFeedbackToUser());
        app.logic.execute("group add g/T02");
        assertEquals("Switched active group to T01.", app.logic.execute("group use g/t01").getFeedbackToUser());
        assertEquals("1. T01\n2. T02\nActive group: T01", app.logic.execute("group list").getFeedbackToUser());
        assertEquals(CommandResult.forHelp("Showing group commands.", "group"), app.logic.execute("help group"));
        assertEquals(List.of("group add g/GROUP_NAME", "group list", "group use g/GROUP_NAME", "group export"),
                HelpCatalog.filterByTopic(app.logic.getHelpEntries(), "group").stream()
                        .map(HelpEntry::getCommandFormat).toList());
    }

    @Test
    public void init_afterGroupCommandsAndRestart_restoresGroupsAndActiveSelection() throws Exception {
        Path prefsFile = temporaryFolder.resolve("preferences.json");
        Path dataFile = temporaryFolder.resolve("tassist.json");
        MainApp app = new MainApp(prefsFile, dataFile);
        app.init();
        app.logic.execute("group add g/ Tutorial   1-Thu ");
        app.logic.execute("group add g/T02");
        assertEquals("T02", new JsonTAssistStorage(dataFile).readTAssist().orElseThrow()
                .getActiveGroup().orElseThrow().getName().toString());
        app.logic.execute("group use g/tutorial 1-thu");
        TAssist expected = new TAssist(app.model.getTAssist());
        app.stop();

        MainApp reopened = new MainApp(prefsFile, dataFile);
        reopened.init();

        assertEquals(expected, reopened.model.getTAssist());
        assertEquals("1. Tutorial 1-Thu\n2. T02\nActive group: Tutorial 1-Thu",
                reopened.logic.execute("group list").getFeedbackToUser());
        assertEquals("Tutorial 1-Thu", reopened.logic.activeGroupProperty().get().orElseThrow().getName().toString());
    }

    @Test
    public void init_corruptData_commandsAndRestartKeepFileUntouched() throws Exception {
        Path prefsFile = temporaryFolder.resolve("preferences.json");
        for (String invalidData : List.of("{", "null", "{\"activeGroup\":\"unknown\"}")) {
            Files.writeString(dataFile(), invalidData);
            MainApp app = new MainApp(prefsFile, dataFile());
            app.init();

            for (String command : List.of("help", "view storage", "group list")) {
                assertFalse(app.logic.execute(command).hasSavedChanges());
            }
            assertThrows(CommandException.class,
                    LogicManager.MESSAGE_DATA_PROTECTED, () -> app.logic.execute("group add g/T01"));
            assertEquals(new TAssist(), app.model.getTAssist());
            app.logic.execute("exit");
            app.stop();
            assertEquals(invalidData, Files.readString(dataFile()));

            MainApp reopened = new MainApp(prefsFile, dataFile());
            reopened.init();
            assertEquals(DataLoadStatus.FAILED, reopened.getDataLoadStatus());
            assertEquals(invalidData, Files.readString(dataFile()));
        }
    }

    @Test
    public void init_unreadableDataPath_protectsExistingContents() throws Exception {
        Path directory = Files.createDirectory(dataFile());
        Path existingFile = directory.resolve("keep.txt");
        Files.writeString(existingFile, "Keep existing contents.");
        MainApp app = new MainApp(temporaryFolder.resolve("preferences.json"), directory);
        app.init();

        assertEquals(DataLoadStatus.FAILED, app.getDataLoadStatus());
        assertThrows(CommandException.class,
                LogicManager.MESSAGE_DATA_PROTECTED, () -> app.logic.execute("group add g/T01"));
        app.stop();
        assertEquals("Keep existing contents.", Files.readString(existingFile));
        assertTrue(Files.isDirectory(directory));
    }

    @Test
    public void constructor_nullPath_throwsNullPointerException() {
        Path prefsFile = temporaryFolder.resolve("preferences.json");
        Path dataFile = temporaryFolder.resolve("tassist.json");
        assertThrows(NullPointerException.class, () -> new MainApp(null, dataFile));
        assertThrows(NullPointerException.class, () -> new MainApp(prefsFile, null));
    }

    @Test
    public void init_missingDataFile_startsEmptyAndSavesPrefs() throws Exception {
        Path prefsFile = temporaryFolder.resolve("preferences.json");
        MainApp app = new MainApp(prefsFile, temporaryFolder.resolve("tassist.json"));

        app.init();

        assertEquals(new TAssist(), app.model.getTAssist());
        assertEquals(new UserPrefs(), app.model.getUserPrefs());
        assertEquals(Optional.of(new UserPrefs()), new JsonUserPrefsStorage(prefsFile).readUserPrefs());
        assertFalse(Files.exists(temporaryFolder.resolve("tassist.json")));
        assertEquals(DataLoadStatus.MISSING, app.getDataLoadStatus());
    }

    @Test
    public void init_savedFiles_restoresDataAndPrefs() throws Exception {
        Path prefsFile = temporaryFolder.resolve("preferences.json");
        Path dataFile = temporaryFolder.resolve("tassist.json");
        TAssist saved = new TAssistBuilder(TypicalGroups.getTypicalTAssist())
                .withActiveGroup(TypicalGroups.NAME_T02).build();
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(800, 600, 1, 2));
        StorageManager storage = new StorageManager(new JsonTAssistStorage(dataFile),
                new JsonUserPrefsStorage(prefsFile));
        storage.saveTAssist(saved);
        storage.saveUserPrefs(userPrefs);

        MainApp app = new MainApp(prefsFile, dataFile);
        app.init();

        assertEquals(saved, app.model.getTAssist());
        assertEquals(DataLoadStatus.LOADED, app.getDataLoadStatus());
        assertEquals(userPrefs, app.model.getUserPrefs());
        assertEquals(TypicalGroups.getT02().getStudentList(), app.model.getActiveGroupStudentList());
    }

    @Test
    public void start_delegatesToUi() {
        RecordingUi recordingUi = new RecordingUi();
        mainApp.ui = recordingUi;

        mainApp.start(null);

        assertTrue(recordingUi.isStarted());
        assertNull(recordingUi.getStartedStage());
    }

    @Test
    public void stop_savesUserPrefs() throws Exception {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(800, 600, 1, 2));
        mainApp.model = new ModelManager(new TAssist(), userPrefs);
        mainApp.storage = createStorage();

        mainApp.stop();

        assertEquals(Optional.of(userPrefs), mainApp.storage.readUserPrefs());
    }

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
            assertEquals(DataLoadStatus.FAILED, mainApp.getDataLoadStatus());
        }
    }

    private Path dataFile() {
        return temporaryFolder.resolve("tassist.json");
    }

    private StorageManager createStorage() {
        return new StorageManager(new JsonTAssistStorage(dataFile()),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
    }

    /**
     * Records the stage passed to {@link Ui#start(Stage)}.
     */
    private static class RecordingUi implements Ui {
        private boolean hasStarted;
        private Stage startedStage;

        @Override
        public void start(Stage primaryStage) {
            hasStarted = true;
            startedStage = primaryStage;
        }

        /**
         * Returns true if {@link #start(Stage)} has been called.
         */
        public boolean isStarted() {
            return hasStarted;
        }

        /**
         * Returns the stage passed to {@link #start(Stage)}.
         */
        public Stage getStartedStage() {
            return startedStage;
        }
    }
}
