package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.TAssist;
import seedu.address.storage.JsonTAssistStorage;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalAssignments;
import seedu.address.testutil.TypicalGroups;

public class AssignmentCommandsIntegrationTest {
    private static final String LIST_PS1_IN_T01 = "PS1 records for T01:"
            + "\nAlice Tan | A0123456X | Not submitted | -"
            + "\nBen Lim | A0234567Y | Not submitted | -"
            + "\nCarl Goh | A0345678Z | Not submitted | -";

    @TempDir
    public Path temporaryFolder;

    private MainApp app;
    private Path dataFile;

    @BeforeEach
    public void setUp() throws Exception {
        dataFile = temporaryFolder.resolve("tassist.json");
        new JsonTAssistStorage(dataFile).saveTAssist(new TAssistBuilder(TypicalGroups.getTypicalTAssist())
                .withActiveGroup(NAME_T01).build());
        app = openApp();
    }

    @Test
    public void execute_addListAndRestart_preservesAssignmentsPerGroup() throws Exception {
        assertEquals("Created assignment PS1 for T01.", feedback("assignment add a/PS1"));
        assertEquals("Created assignment Mid-term 1 for T01.", feedback("assignment add a/  Mid-term   1 "));
        assertEquals(LIST_PS1_IN_T01, feedback("assignment list a/ps1"));
        TAssist expected = new TAssist(app.model.getTAssist());

        restart();

        assertEquals(expected, app.model.getTAssist());
        assertEquals(List.of(TypicalAssignments.PS1.getName().fullName, "Mid-term 1"),
                app.model.getActiveGroup().orElseThrow().getAssignmentList().stream()
                        .map(assignment -> assignment.getName().fullName).toList());
        assertEquals(LIST_PS1_IN_T01, feedback("assignment list a/PS1"));
        app.logic.execute("group use g/T02");
        assertRejected("assignment list a/PS1", CommandException.class);
        assertEquals("Created assignment PS1 for T02.", feedback("assignment add a/PS1"));
    }

    @Test
    public void execute_submitAndGrade_persistsStudentRecordsAfterRestart() throws Exception {
        feedback("assignment add a/PS1");
        assertEquals("Updated Alice Tan's PS1 submission status to submitted.",
                feedback("assignment submit a/PS1 id/A0123456X s/yes"));
        assertEquals("Recorded grade 87 for Alice Tan's PS1.",
                feedback("assignment grade a/PS1 id/A0123456X g/87"));
        TAssist expected = new TAssist(app.model.getTAssist());

        restart();

        assertEquals(expected, app.model.getTAssist());
        assertEquals("PS1 records for T01:\nAlice Tan | A0123456X | Submitted | 87"
                        + "\nBen Lim | A0234567Y | Not submitted | -"
                        + "\nCarl Goh | A0345678Z | Not submitted | -",
                feedback("assignment list a/PS1"));
    }

    @Test
    public void execute_rejectedInput_preservesModelAndSavedFile() throws Exception {
        feedback("assignment add a/PS1");
        for (String command : List.of("assignment", "assignment remove", "assignment add", "assignment add a/",
                "assignment add a/PS_1", "assignment add a/PS1/PS2", "assignment add a/--",
                "assignment add a/" + "a".repeat(10000), "assignment add a/PS2 a/PS3", "assignment add PS2",
                "assignment add n/PS2", "assignment list", "assignment list a/", "assignment list a/PS2 a/PS3",
                "assignment list a/PS1!", "assignment list w/3")) {
            assertRejected(command, ParseException.class);
        }
        assertRejected("assignment add a/PS1", CommandException.class);
        assertRejected("assignment add a/ps1", CommandException.class);
        assertRejected("assignment list a/PS2", CommandException.class);
    }

    @Test
    public void execute_noActiveGroup_preservesModelAndSavedFile() throws Exception {
        app.model.setTAssist(new TAssist());
        app.storage.saveTAssist(app.model.getTAssist());
        for (String command : List.of("assignment add a/PS1", "assignment list a/PS1")) {
            assertRejected(command, CommandException.class);
        }
    }

    @Test
    public void execute_duplicateAndUnknownAssignment_showSpecificMessages() throws Exception {
        feedback("assignment add a/PS1");
        assertThrows(CommandException.class, "Assignment PS1 already exists in T01", () ->
                app.logic.execute("assignment add a/ps1"));
        assertThrows(CommandException.class, "Assignment PS2 does not exist in T01. "
                + "Check the name, or create it with: assignment add a/PS2", () ->
                app.logic.execute("assignment list a/PS2"));
    }

    @Test
    public void init_assignmentHelp_registersFormatsAndWorkingExamples() throws Exception {
        List<HelpEntry> entries = HelpCatalog.filterByTopic(app.logic.getHelpEntries(), "assignment");
        assertEquals(List.of("assignment add a/ASSIGNMENT_NAME",
                        "assignment submit a/ASSIGNMENT_NAME id/STUDENT_ID s/STATUS",
                        "assignment grade a/ASSIGNMENT_NAME id/STUDENT_ID g/GRADE",
                        "assignment list a/ASSIGNMENT_NAME"),
                entries.stream().map(HelpEntry::getCommandFormat).toList());
        assertEquals(CommandResult.forHelp("Showing assignment commands.", "assignment"),
                app.logic.execute("help assignment"));
        for (HelpEntry entry : entries) {
            for (String example : entry.getExamples()) {
                app.logic.execute(example);
            }
        }
    }

    private void assertRejected(String command, Class<? extends Exception> exceptionType) throws Exception {
        TAssist expected = new TAssist(app.model.getTAssist());
        String saved = Files.readString(dataFile);
        assertThrows(exceptionType, () -> app.logic.execute(command));
        assertEquals(expected, app.model.getTAssist());
        assertEquals(saved, Files.readString(dataFile));
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
