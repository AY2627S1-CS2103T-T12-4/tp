package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.GroupExportCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.TAssist;
import seedu.address.model.student.Student;
import seedu.address.storage.GroupRecordsCsvExporter;

public class StudentCommandsIntegrationTest extends AppIntegrationTest {
    @BeforeEach
    public void setUp() throws Exception {
        app = openApp();
        app.logic.execute("group add g/T01");
    }

    @Test
    public void execute_namesWithSlashParticles_addedAndRestored() throws Exception {
        assertEquals("Added Ravi s/o Kumar to T01.", feedback("student add n/Ravi s/o Kumar id/A0345678Z"));
        assertEquals("Added Siti D/O Ahmad to T01.", feedback("student add n/Siti D/O Ahmad id/A0456789W"));
        restart();
        assertEquals("Students in T01:\n1. Ravi s/o Kumar | A0345678Z\n2. Siti D/O Ahmad | A0456789W",
                feedback("student list"));
        // The particle does not change how the other commands of the group read their own s/ prefixes.
        assertEquals("Recorded Ravi s/o Kumar as present for Week 3.",
                feedback("attendance mark id/A0345678Z w/3 s/present"));
    }

    @Test
    public void execute_findWithSlashParticles_matchesNames() throws Exception {
        feedback("student add n/Ravi s/o Kumar id/A0345678Z");
        feedback("student add n/Muthu a/l Raju id/A0456789W");
        Map<String, String> searches = Map.of("student find n/Ravi s/o", "A0345678Z",
                "student find n/MUTHU A/L raju", "A0456789W");
        for (Map.Entry<String, String> search : searches.entrySet()) {
            assertEquals("Found 1 matching student(s) in T01.", feedback(search.getKey()), search.getKey());
            assertEquals(List.of(search.getValue()), app.logic.getDisplayedStudentList().stream()
                    .map(student -> student.getStudentId().toString()).toList(), search.getKey());
        }
        String gluedParticleMessage = "Unknown prefix 's/'. Allowed prefixes: n/ id/. "
                + "If s/o is part of the value, put a space before and after it.";
        String gluedSearch = "student find n/Ravi s/oKumar";
        assertThrows(ParseException.class, gluedParticleMessage, () -> app.logic.execute(gluedSearch));
    }

    @Test
    public void execute_studentCommandsAndRestart_preservesRosterAndDeletion() throws Exception {
        assertEquals("Added Alice Tan to T01.", feedback("student add n/Alice Tan id/a0123456x"));
        assertEquals("Added Alice Tan to T01. Warning: T01 already has a student named Alice Tan (A0123456X). "
                + "Check that the student ID is correct.", feedback("student add n/Alice Tan id/A2"));
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
        for (String command : List.of("student add n/Alice id/A1", "student list", "student find n/Alice",
                "student sort by/name", "student delete id/A1")) {
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
        assertEquals(app.logic.getConfirmationPrompt("student delete id/A1"),
                app.logic.getConfirmationPrompt("STUDENT DeLeTe id/A1"));
        assertEquals(Optional.empty(), app.logic.getConfirmationPrompt("STUDENT DELETE id/A1 f/yes"));
        assertEquals(app.logic.getConfirmationPrompt("group delete g/T01"),
                app.logic.getConfirmationPrompt("GROUP DeLeTe g/T01"));
        assertEquals(previousData, app.model.getTAssist());
        assertEquals(savedFile, Files.readString(dataFile));
    }

    @Test
    public void init_studentHelp_registersFormatsAndWorkingExamples() throws Exception {
        List<HelpEntry> entries = HelpCatalog.filterByTopic(app.logic.getHelpEntries(), "student");
        assertEquals(List.of("student add n/NAME id/STUDENT_ID", "student list",
                        "student find [n/NAME_FRAGMENT] [id/ID_FRAGMENT]", "student sort by/FIELD",
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

    @Test
    public void execute_searchAndSort_errorsPreserveDisplayAndOtherFeaturesUseFullRoster() throws Exception {
        feedback("student add n/Ben Lim id/B2");
        feedback("student add n/Alice Tan id/A1");
        feedback("student find n/alice");
        feedback("student sort by/id");
        List<Student> displayed = List.copyOf(app.logic.getDisplayedStudentList());
        TAssist previousData = new TAssist(app.model.getTAssist());
        String saved = Files.readString(dataFile);
        for (String command : List.of("student find", "student find n/", "student find id/",
                "student find n/Alice n/Ben", "student find n/Alice w/1", "student sort by/grade",
                "student sort by/name by/id", "student sort", "student sort by/id now")) {
            assertThrows(ParseException.class, () -> app.logic.execute(command));
            assertEquals(displayed, app.logic.getDisplayedStudentList(), command);
        }
        assertThrows(CommandException.class, () -> app.logic.execute("student delete id/C3"));
        assertEquals(displayed, app.logic.getDisplayedStudentList());
        assertEquals(previousData, app.model.getTAssist());
        assertEquals(saved, Files.readString(dataFile));

        feedback("attendance mark id/B2 w/1 s/present");
        feedback("participation set id/B2 w/1 s/5");
        feedback("assignment add a/PS1");
        feedback("assignment submit a/PS1 id/B2 s/yes");
        feedback("assignment grade a/PS1 id/B2 m/87");
        for (String command : List.of("attendance list w/1", "participation list w/1", "assignment list a/PS1")) {
            String rows = feedback(command);
            assertTrue(rows.indexOf("Ben Lim | B2") < rows.indexOf("Alice Tan | A1"), command);
            assertTrue(rows.contains("Ben Lim | B2"), command);
        }
        GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(temporaryFolder.resolve("exports"));
        new GroupExportCommand(exporter).execute(app.model);
        Path exportFile = exporter.getExportFile(app.model.getActiveGroup().orElseThrow().getName());
        List<String> rows = Files.readAllLines(exportFile);
        assertEquals(3, rows.size());
        assertTrue(rows.get(1).startsWith("Ben Lim,B2,"));
        assertTrue(rows.get(2).startsWith("Alice Tan,A1,"));
        assertEquals(displayed, app.logic.getDisplayedStudentList());
    }

    @Test
    public void execute_emptyRosterQueries_preservesFileAndShowsLaterAdditions() throws Exception {
        String saved = Files.readString(dataFile);
        CommandResult findResult = app.logic.execute("student find n/alice");
        assertEquals("No students in T01. Add one using: student add n/NAME id/STUDENT_ID",
                findResult.getFeedbackToUser());
        assertFalse(findResult.hasSavedChanges());
        CommandResult sortResult = app.logic.execute("student sort by/name");
        assertEquals("Sorted 0 student(s) in T01 by name.", sortResult.getFeedbackToUser());
        assertFalse(sortResult.hasSavedChanges());
        assertEquals(saved, Files.readString(dataFile));

        feedback("student add n/Ben Lim id/B2");
        feedback("student add n/Alice Tan id/A1");
        assertEquals(List.of("A1", "B2"), app.logic.getDisplayedStudentList().stream()
                .map(student -> student.getStudentId().toString()).toList());
        restart();
        assertEquals("Students in T01:\n1. Ben Lim | B2\n2. Alice Tan | A1", feedback("student list"));
    }
}
