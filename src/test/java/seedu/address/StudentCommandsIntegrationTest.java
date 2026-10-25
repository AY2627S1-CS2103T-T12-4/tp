package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.TAssist;

public class StudentCommandsIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private MainApp app;
    private Path dataFile;

    @BeforeEach
    public void setUp() throws Exception {
        dataFile = temporaryFolder.resolve("tassist.json");
        app = openApp();
        app.logic.execute("group add g/T01");
    }

    @Test
    public void execute_studentCommandsAndRestart_preservesRosterAndDeletion() throws Exception {
        assertEquals("Added Alice Tan to T01.", feedback("student add n/Alice Tan id/a0123456x"));
        assertEquals("Added Alice Tan to T01.", feedback("student add n/Alice Tan id/A2"));
        assertEquals(2, app.logic.getActiveGroupStudentList().size());
        restart();
        assertEquals("Students in T01:\n1. Alice Tan | A0123456X\n2. Alice Tan | A2", feedback("student list"));
        assertEquals("Removed Alice Tan from T01.", feedback("student delete id/a0123456x"));
        restart();
        assertEquals("Students in T01:\n1. Alice Tan | A2", feedback("student list"));
        feedback("student delete id/A2");
        restart();
        assertTrue(app.logic.getActiveGroupStudentList().isEmpty());
        assertEquals("No students in T01. Add one using: student add n/NAME id/STUDENT_ID", feedback("student list"));
    }

    @Test
    public void execute_rejectedCommands_preservesModelAndSavedFile() throws Exception {
        feedback("student add n/Alice Tan id/A1");
        for (String command : List.of("student add", "student add n/Alice", "student add n/ id/A2",
                "student add n/Alice id/ABC", "student add n/Alice id/A2 id/A3", "student add n/Alice x/A2",
                "student add n/Aliceid/A2", "student add n/" + "a".repeat(10000) + " id/A2",
                "student list extra", "student delete", "student delete id/", "student delete id/A1 id/A2",
                "student delete n/Alice", "student", "student update")) {
            assertRejected(command, ParseException.class);
        }
        assertRejected("student add n/Other Name id/a1", CommandException.class);
        assertRejected("student delete id/A2", CommandException.class);
    }

    @Test
    public void execute_noActiveGroup_preservesModelAndSavedFile() throws Exception {
        app.model.setTAssist(new TAssist());
        app.storage.saveTAssist(app.model.getTAssist());
        for (String command : List.of("student add n/Alice id/A1", "student list", "student delete id/A1")) {
            assertRejected(command, CommandException.class);
        }
    }

    @Test
    public void getConfirmationPrompt_namesStudentWithoutChangingSavedData() throws Exception {
        feedback("student add n/Alice Tan id/A1");
        TAssist previousData = new TAssist(app.model.getTAssist());
        String savedFile = Files.readString(dataFile);

        assertEquals(Optional.of("Remove Alice Tan (A1) and all their attendance, participation, submission, "
                        + "and grade records from T01?"),
                app.logic.getConfirmationPrompt("student delete id/A1"));
        assertEquals(Optional.empty(), app.logic.getConfirmationPrompt("student delete id/A1 f/yes"));
        assertEquals(previousData, app.model.getTAssist());
        assertEquals(savedFile, Files.readString(dataFile));
    }

    @Test
    public void init_studentHelp_registersFormatsAndWorkingExamples() throws Exception {
        List<HelpEntry> entries = HelpCatalog.filterByTopic(app.logic.getHelpEntries(), "student");
        assertEquals(List.of("student add n/NAME id/STUDENT_ID", "student list",
                        "student find [n/NAME_FRAGMENT] [id/ID_FRAGMENT]",
                        "student delete id/STUDENT_ID [f/yes]"),
                entries.stream().map(HelpEntry::getCommandFormat).toList());
        assertEquals(CommandResult.forHelp("Showing student commands.", "student"), app.logic.execute("help student"));
        for (HelpEntry entry : entries) {
            for (String example : entry.getExamples()) {
                app.logic.execute(example);
            }
        }
    }

    @Test
    public void execute_searchAndList_changesOnlyDisplayAndResetsOnRestart() throws Exception {
        feedback("student add n/Alice Tan id/A1");
        feedback("student add n/Ben Lim id/B2");
        TAssist previousData = new TAssist(app.model.getTAssist());
        String saved = Files.readString(dataFile);

        assertFalse(app.logic.execute("student find n/liCE id/a").hasSavedChanges());
        assertEquals(List.of("A1"), app.logic.getDisplayedStudentList().stream()
                .map(student -> student.getStudentId().toString()).toList());
        assertEquals(previousData, app.model.getTAssist());
        assertEquals(saved, Files.readString(dataFile));

        feedback("student find n/Ben");
        assertEquals("B2", app.logic.getDisplayedStudentList().getFirst().getStudentId().toString());
        feedback("student list");
        assertEquals(app.logic.getActiveGroupStudentList(), app.logic.getDisplayedStudentList());
        feedback("student find n/nobody");
        assertTrue(app.logic.getDisplayedStudentList().isEmpty());
        restart();
        assertEquals(app.logic.getActiveGroupStudentList(), app.logic.getDisplayedStudentList());
        assertEquals(saved, Files.readString(dataFile));
    }

    private void assertRejected(String command, Class<? extends Exception> exceptionType) throws Exception {
        TAssist expected = new TAssist(app.model.getTAssist());
        String saved = Files.readString(dataFile);
        assertThrows(exceptionType, () -> app.logic.execute(command));
        assertEquals(expected, app.model.getTAssist());
        assertEquals(saved, Files.readString(dataFile));
        assertEquals(expected.getActiveGroup().map(group -> List.copyOf(group.getStudentList())).orElse(List.of()),
                app.logic.getActiveGroupStudentList());
    }

    private String feedback(String command) throws Exception {
        return app.logic.execute(command).getFeedbackToUser();
    }

    private MainApp openApp() throws Exception {
        MainApp opened = new MainApp(temporaryFolder.resolve("prefs.json"), dataFile);
        opened.init();
        return opened;
    }

    private void restart() throws Exception {
        app.stop();
        app = openApp();
    }
}
