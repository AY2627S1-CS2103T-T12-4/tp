package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AssignmentCommandRegistration;
import seedu.address.logic.parser.AttendanceCommandRegistration;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.logic.parser.GroupCommandRegistration;
import seedu.address.logic.parser.ParticipationCommandRegistration;
import seedu.address.logic.parser.StudentCommandRegistration;
import seedu.address.model.ReadOnlyTAssist;
import seedu.address.model.TAssist;
import seedu.address.model.student.Student;
import seedu.address.storage.JsonTAssistStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class SaveFailureIntegrationTest extends AppIntegrationTest {
    private IOException saveFailure = new IOException("Disk write failed.");

    @Test
    public void execute_saveFailuresAcrossFeatures_restoreLastSavedState() throws Exception {
        app = openApp();
        for (String command : List.of("group add g/T01", "student add n/Alice id/A1",
                "attendance mark id/A1 w/1 s/present", "participation set id/A1 w/1 s/2",
                "assignment add a/PS1", "assignment submit a/PS1 id/A1 s/yes",
                "assignment grade a/PS1 id/A1 m/87", "group add g/T02", "group use g/T01")) {
            assertTrue(app.logic.execute(command).hasSavedChanges());
        }
        TAssist lastSaved = new TAssist(app.model.getTAssist());
        String savedContents = Files.readString(dataFile);
        injectFailingStorage();

        for (String command : List.of("group add g/T03", "group use g/T02", "student add n/Ben id/A2",
                "student delete id/A1", "attendance mark id/A1 w/1 s/absent", "participation set id/A1 w/1 s/5")) {
            assertThrows(CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE, () -> app.logic.execute(command));
            assertEquals(lastSaved, app.model.getTAssist(), command);
            assertEquals(lastSaved.getActiveGroup(), app.logic.activeGroupProperty().get(), command);
            assertEquals(lastSaved.getActiveGroup().orElseThrow().getStudentList(),
                    app.logic.getActiveGroupStudentList(), command);
            assertEquals(savedContents, Files.readString(dataFile), command);
        }
        for (String command : List.of("assignment submit a/PS1 id/A1 s/no",
                "assignment grade a/PS1 id/A1 m/91")) {
            assertThrows(CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE, () -> app.logic.execute(command));
            assertEquals(lastSaved, app.model.getTAssist(), command);
            assertEquals(savedContents, Files.readString(dataFile), command);
        }
        for (String command : List.of("group list", "student list", "attendance list w/1",
                "participation list w/1", "assignment list a/PS1", "help", "view storage")) {
            assertFalse(app.logic.execute(command).hasSavedChanges());
        }
        app.stop();
        assertEquals(lastSaved, openApp().model.getTAssist());
    }

    @Test
    public void execute_failedFirstSave_canRetryAndRestart() throws Exception {
        app = openApp();
        injectFailingStorage();
        assertThrows(CommandException.class,
                LogicManager.MESSAGE_SAVE_FAILURE, () -> app.logic.execute("group add g/T01"));
        assertEquals(new TAssist(), app.model.getTAssist());
        assertFalse(Files.exists(dataFile));

        saveFailure = null;
        assertTrue(app.logic.execute("group add g/T01").hasSavedChanges());
        assertFalse(app.logic.execute("group use g/T01").hasSavedChanges());
        saveFailure = new IOException("Disk write failed again.");
        assertThrows(CommandException.class,
                LogicManager.MESSAGE_SAVE_FAILURE, () -> app.logic.execute("group add g/T02"));
        saveFailure = null;
        assertTrue(app.logic.execute("group add g/T02").hasSavedChanges());
        app.stop();
        assertEquals(app.model.getTAssist(), openApp().model.getTAssist());
    }

    @Test
    public void execute_failedMutations_preservesSearchAndSorting() throws Exception {
        app = openApp();
        for (String command : List.of("group add g/T01", "student add n/Ben id/B2",
                "student add n/Alice id/A3", "student add n/Alice id/A1", "group add g/T02",
                "group use g/T01", "student find n/alice", "student sort by/id")) {
            app.logic.execute(command);
        }
        List<Student> displayed = List.copyOf(app.logic.getDisplayedStudentList());
        TAssist previousData = new TAssist(app.model.getTAssist());
        String saved = Files.readString(dataFile);
        injectFailingStorage();

        for (String command : List.of("group use g/T02", "student add n/Alice id/A2",
                "student delete id/A1", "attendance mark id/A1 w/1 s/present")) {
            assertThrows(CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE, () -> app.logic.execute(command));
            assertEquals(displayed, app.logic.getDisplayedStudentList(), command);
            assertEquals(previousData, app.model.getTAssist(), command);
            assertEquals(saved, Files.readString(dataFile), command);
        }
        saveFailure = null;
        app.logic.execute("student add n/Alice id/A2");
        assertEquals(List.of("A1", "A2", "A3"), app.logic.getDisplayedStudentList().stream()
                .map(student -> student.getStudentId().toString()).toList());
    }

    private void injectFailingStorage() {
        JsonTAssistStorage dataStorage = new JsonTAssistStorage(dataFile) {
            @Override
            public void saveTAssist(ReadOnlyTAssist tAssist) throws IOException {
                if (saveFailure != null) {
                    throw saveFailure;
                }
                super.saveTAssist(tAssist);
            }
        };
        CommandRegistry registry = new CommandRegistry();
        GroupCommandRegistration.registerCommands(registry);
        StudentCommandRegistration.registerCommands(registry);
        AttendanceCommandRegistration.registerCommands(registry);
        AssignmentCommandRegistration.registerCommands(registry);
        ParticipationCommandRegistration.registerCommands(registry);
        app.logic = new LogicManager(app.model, new StorageManager(dataStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))), registry);
    }
}
