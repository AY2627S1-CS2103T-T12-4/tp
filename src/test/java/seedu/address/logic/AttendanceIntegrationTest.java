package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_STUDENT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AttendanceCommandParserUtil;
import seedu.address.logic.parser.AttendanceCommandRegistration;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.logic.parser.GroupCommandRegistration;
import seedu.address.logic.parser.TAssistArgumentParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.storage.JsonTAssistStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.TypicalGroups;

public class AttendanceIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private StorageManager storage;
    private Path dataFile;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
        dataFile = temporaryFolder.resolve("tassist.json");
        storage = new StorageManager(new JsonTAssistStorage(dataFile),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        logic = createLogic();
    }

    @Test
    public void execute_markCorrectAndReload_preservesRecordsAndGroupIsolation() throws Exception {
        model.addStudent(TypicalGroups.NAME_T02, ALICE);
        model.setStudent(TypicalGroups.NAME_T01, ALICE.withParticipationRecord(3, 4));
        assertFeedback("attendance mark id/A0123456X w/1 s/present", "Recorded Alice Tan as present for Week 1.");
        assertFeedback("attendance mark id/A0123456X w/3 s/present", "Recorded Alice Tan as present for Week 3.");
        assertFeedback("attendance mark id/A0123456X w/3 s/absent",
                "Updated Alice Tan's attendance for Week 3: present to absent.");
        restart();

        assertFeedback("attendance list w/3", "Attendance for T01, Week 3:\n"
                + "Alice Tan | A0123456X | Absent\n"
                + "Ben Lim | A0234567Y | Not recorded\n"
                + "Carl Goh | A0345678Z | Not recorded");
        assertEquals(ALICE.withAttendanceRecord(1, "PRESENT").withAttendanceRecord(3, "ABSENT")
                .withParticipationRecord(3, 4), model.getGroup(TypicalGroups.NAME_T01)
                        .findStudent(ALICE.getStudentId()).orElseThrow());

        logic.execute("group use g/T02");
        assertFeedback("attendance list w/3", "No attendance records have been recorded for Week 3.");
        logic.execute("attendance mark id/A0123456X w/13 s/present");
        restart();
        assertFeedback("attendance list w/13", "Attendance for T02, Week 13:\n"
                + "Daniel Ong | A0456789W | Not recorded\n"
                + "Elle Teo | A0567890U | Not recorded\n"
                + "Alice Tan | A0123456X | Present");
        assertEquals(ALICE.withAttendanceRecord(13, "PRESENT"),
                model.getGroup(TypicalGroups.NAME_T02).findStudent(ALICE.getStudentId()).orElseThrow());
    }

    @Test
    public void execute_invalidValuesAndStudent_preservesModelAndFile() throws Exception {
        logic.execute("attendance mark id/A0123456X w/3 s/present");
        for (String week : new String[] {"0", "14", "-1", "3.5", "three", "9".repeat(1000)}) {
            assertFailureUnchanged("attendance mark id/A0123456X w/" + week + " s/absent",
                    ParseException.class, AttendanceCommandParserUtil.MESSAGE_INVALID_WEEK);
            assertFailureUnchanged("attendance list w/" + week,
                    ParseException.class, AttendanceCommandParserUtil.MESSAGE_INVALID_WEEK);
        }
        assertFailureUnchanged("attendance mark id/A0123456X w/3 s/late",
                ParseException.class, AttendanceStatus.MESSAGE_CONSTRAINTS);
        assertFailureUnchanged("attendance mark id/A0456789W w/3 s/present",
                CommandException.class, MESSAGE_UNKNOWN_STUDENT);
    }

    @Test
    public void execute_badFormat_preservesModelAndFile() throws Exception {
        logic.execute("attendance mark id/A0123456X w/3 s/present");
        assertFailureUnchanged("attendance mark id/A0123456X s/absent", ParseException.class,
                String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, "w/"));
        assertFailureUnchanged("attendance mark id/A0123456Xw/3 s/absent", ParseException.class,
                String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, "w/"));
        assertFailureUnchanged("attendance mark id/A0123456X w/3 s/absent s/present", ParseException.class,
                Messages.MESSAGE_DUPLICATE_FIELDS + "s/");
        assertFailureUnchanged("attendance list w/3 x/value", ParseException.class,
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "x/", "w/"));
    }

    @Test
    public void execute_noActiveGroup_preservesModelAndFile() throws Exception {
        model.setTAssist(TypicalGroups.getTypicalTAssist());
        storage.saveTAssist(model.getTAssist());
        assertFailureUnchanged("attendance mark id/A0123456X w/3 s/present",
                CommandException.class, MESSAGE_NO_ACTIVE_GROUP);
        assertFailureUnchanged("attendance list w/3", CommandException.class, MESSAGE_NO_ACTIVE_GROUP);
    }

    private Logic createLogic() {
        CommandRegistry registry = new CommandRegistry();
        GroupCommandRegistration.registerCommands(registry);
        AttendanceCommandRegistration.registerCommands(registry);
        return new LogicManager(model, storage, registry);
    }

    private void restart() throws Exception {
        model = new ModelManager(storage.readTAssist().orElseThrow(), new UserPrefs());
        logic = createLogic();
    }

    private void assertFeedback(String command, String feedback) throws Exception {
        assertEquals(feedback, logic.execute(command).getFeedbackToUser());
    }

    private void assertFailureUnchanged(String command, Class<? extends Throwable> exceptionType,
            String message) throws Exception {
        TAssist before = new TAssist(model.getTAssist());
        String savedData = Files.readString(dataFile);
        assertThrows(exceptionType, message, () -> logic.execute(command));
        assertEquals(before, model.getTAssist());
        assertEquals(savedData, Files.readString(dataFile));
    }
}
