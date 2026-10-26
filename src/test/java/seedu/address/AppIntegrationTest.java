package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.ReadOnlyTAssist;
import seedu.address.model.TAssist;
import seedu.address.storage.JsonTAssistStorage;

/**
 * Runs the whole app on files in a temporary folder, for integration tests that enter commands as a user would.
 * Subclasses open the app with {@link #openApp()} and use {@link #app} to reach its components.
 */
public abstract class AppIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    protected Path dataFile;
    protected MainApp app;

    @BeforeEach
    public void setUpDataFile() {
        dataFile = temporaryFolder.resolve("data").resolve("tassist.json");
    }

    /**
     * Returns a new app that has loaded {@link #dataFile} and the preferences in {@link #temporaryFolder}.
     */
    protected MainApp openApp() throws Exception {
        MainApp opened = new MainApp(temporaryFolder.resolve("preferences.json"), dataFile);
        opened.init();
        return opened;
    }

    /**
     * Saves {@code data} to {@link #dataFile} and returns a new app that has loaded it.
     */
    protected MainApp openAppWith(ReadOnlyTAssist data) throws Exception {
        new JsonTAssistStorage(dataFile).saveTAssist(data);
        return openApp();
    }

    /**
     * Stops {@link #app} and opens it again from the saved files.
     */
    protected void restart() throws Exception {
        app.stop();
        app = openApp();
    }

    /**
     * Executes {@code command} in {@link #app} and returns the feedback shown to the user.
     */
    protected String feedback(String command) throws Exception {
        return app.logic.execute(command).getFeedbackToUser();
    }

    /**
     * Asserts that {@code command} fails with {@code exceptionType} and changes neither the data, the displayed
     * students, nor the saved file.
     */
    protected void assertRejected(String command, Class<? extends Exception> exceptionType) throws Exception {
        TAssist expected = new TAssist(app.model.getTAssist());
        String saved = Files.readString(dataFile);
        assertThrows(exceptionType, () -> app.logic.execute(command));
        assertEquals(expected, app.model.getTAssist());
        assertEquals(saved, Files.readString(dataFile));
        assertEquals(expected.getActiveGroup().map(group -> List.copyOf(group.getStudentList())).orElse(List.of()),
                app.logic.getActiveGroupStudentList());
    }
}
