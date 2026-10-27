package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;

import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.TAssist;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalAssignments;
import seedu.address.testutil.TypicalGroups;

public class AssignmentCommandsIntegrationTest extends AppIntegrationTest {
    private static final String LIST_PS1_IN_T01 = "PS1 records for T01:"
            + "\nAlice Tan | A0123456X | Not submitted | -"
            + "\nBen Lim | A0234567Y | Not submitted | -"
            + "\nCarl Goh | A0345678Z | Not submitted | -";

    @BeforeEach
    public void setUp() throws Exception {
        app = openAppWith(new TAssistBuilder(TypicalGroups.getTypicalTAssist()).withActiveGroup(NAME_T01).build());
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
                feedback("assignment grade a/PS1 id/A0123456X m/87"));
        TAssist expected = new TAssist(app.model.getTAssist());

        restart();

        assertEquals(expected, app.model.getTAssist());
        assertEquals("PS1 records for T01:\nAlice Tan | A0123456X | Submitted | 87"
                        + "\nBen Lim | A0234567Y | Not submitted | -"
                        + "\nCarl Goh | A0345678Z | Not submitted | -",
                feedback("assignment list a/PS1"));
    }

    @Test
    public void execute_noWithoutSubmission_preservesModelAndSavedFile() throws Exception {
        feedback("assignment add a/PS1");
        TAssist expected = new TAssist(app.model.getTAssist());
        String saved = Files.readString(dataFile);

        for (int i = 0; i < 2; i++) {
            CommandResult result = app.logic.execute("assignment submit a/PS1 id/A0234567Y s/no");

            assertEquals("Ben Lim's PS1 submission status is already not submitted. Nothing changed.",
                    result.getFeedbackToUser());
            assertFalse(result.hasSavedChanges());
            assertEquals(expected, app.model.getTAssist());
            assertEquals(saved, Files.readString(dataFile));
            restart();
        }
    }

    @Test
    public void execute_withdrawSubmission_reportsActualGradeRemovalAndPersists() throws Exception {
        feedback("assignment add a/PS1");
        feedback("assignment submit a/PS1 id/A0123456X s/yes");
        CommandResult ungraded = app.logic.execute("assignment submit a/PS1 id/A0123456X s/no");
        assertEquals("Updated Alice Tan's PS1 submission status to not submitted.", ungraded.getFeedbackToUser());
        assertTrue(ungraded.hasSavedChanges());
        feedback("assignment submit a/PS1 id/A0123456X s/yes");
        feedback("assignment grade a/PS1 id/A0123456X m/87");

        CommandResult graded = app.logic.execute("assignment submit a/PS1 id/A0123456X s/no");
        assertEquals("Updated Alice Tan's PS1 submission status to not submitted; the recorded grade was cleared.",
                graded.getFeedbackToUser());
        assertTrue(graded.hasSavedChanges());
        TAssist expected = new TAssist(app.model.getTAssist());
        restart();
        assertEquals(expected, app.model.getTAssist());
        assertEquals(LIST_PS1_IN_T01, feedback("assignment list a/PS1"));

        String saved = Files.readString(dataFile);
        CommandResult repeated = app.logic.execute("assignment submit a/PS1 id/A0123456X s/no");
        assertEquals("Alice Tan's PS1 submission status is already not submitted. Nothing changed.",
                repeated.getFeedbackToUser());
        assertFalse(repeated.hasSavedChanges());
        assertEquals(expected, app.model.getTAssist());
        assertEquals(saved, Files.readString(dataFile));
    }

    @Test
    public void execute_rejectedInput_preservesModelAndSavedFile() throws Exception {
        feedback("assignment add a/PS1");
        assertRejected("assignment submit a/PS1 id/A0123456X s/maybe", ParseException.class);
        assertRejected("assignment grade a/PS1 id/A0123456X m/101", ParseException.class);
        assertRejected("assignment grade a/PS1 id/A0123456X m/87", CommandException.class);
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
        for (String command : List.of("assignment add a/PS1", "assignment submit a/PS1 id/A0123456X s/yes",
                "assignment grade a/PS1 id/A0123456X m/87", "assignment list a/PS1")) {
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
                        "assignment grade a/ASSIGNMENT_NAME id/STUDENT_ID m/GRADE",
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
}
