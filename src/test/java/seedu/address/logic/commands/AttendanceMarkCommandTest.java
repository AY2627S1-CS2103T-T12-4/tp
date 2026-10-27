package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.DANIEL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.Student;
import seedu.address.testutil.TypicalGroups;

public class AttendanceMarkCommandTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
    }

    @Test
    public void buildTemplate_studentId_endsAtWeekPrefix() {
        assertEquals("attendance mark id/A0123456X w/", AttendanceMarkCommand.buildTemplate(ALICE.getStudentId()));
        assertThrows(NullPointerException.class, () -> AttendanceMarkCommand.buildTemplate(null));
    }

    @Test
    public void execute_firstMark_recordsBothStatusesAtWeekBoundaries() {
        for (int week : new int[] {1, 13}) {
            for (AttendanceStatus status : AttendanceStatus.values()) {
                setUp();
                Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
                expected.setStudent(TypicalGroups.NAME_T01, ALICE.withAttendanceRecord(week, status.name()));
                assertCommandSuccess(new AttendanceMarkCommand(ALICE.getStudentId(), week, status), model,
                        "Recorded Alice Tan as " + status.getCommandValue() + " for Week " + week + ".", expected);
            }
        }
    }

    @Test
    public void execute_existingRecord_replacesOnlySelectedWeek() {
        Student recorded = ALICE.withAttendanceRecord(1, "PRESENT").withAttendanceRecord(3, "PRESENT")
                .withParticipationRecord(3, 4);
        model.setStudent(TypicalGroups.NAME_T01, recorded);
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.setStudent(TypicalGroups.NAME_T01, recorded.withAttendanceRecord(3, "ABSENT"));

        AttendanceMarkCommand command = new AttendanceMarkCommand(ALICE.getStudentId(), 3, AttendanceStatus.ABSENT);
        assertCommandSuccess(command, model, "Updated Alice Tan's attendance for Week 3 to absent.", expected);
        assertCommandSuccess(command, model, "Updated Alice Tan's attendance for Week 3 to absent.", expected);
    }

    @Test
    public void execute_studentInAnotherGroup_failsWithoutChangingData() {
        assertCommandFailure(new AttendanceMarkCommand(DANIEL.getStudentId(), 3, AttendanceStatus.PRESENT), model,
                AttendanceMarkCommand.MESSAGE_UNKNOWN_STUDENT);
    }

    @Test
    public void execute_noActiveGroup_failsWithoutChangingData() {
        Model inactive = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        assertCommandFailure(new AttendanceMarkCommand(ALICE.getStudentId(), 3, AttendanceStatus.PRESENT), inactive,
                MESSAGE_NO_ACTIVE_GROUP);
    }

    @Test
    public void invalidConstruction_throwsException() {
        assertThrows(NullPointerException.class, () -> new AttendanceMarkCommand(null, 3, AttendanceStatus.PRESENT));
        assertThrows(NullPointerException.class, () -> new AttendanceMarkCommand(ALICE.getStudentId(), 3, null));
        assertThrows(IllegalArgumentException.class, () -> {
            new AttendanceMarkCommand(ALICE.getStudentId(), 14, AttendanceStatus.PRESENT);
        });
    }

    @Test
    public void equals_comparesStudentWeekAndStatus() {
        AttendanceMarkCommand command = new AttendanceMarkCommand(ALICE.getStudentId(), 3, AttendanceStatus.PRESENT);
        AttendanceMarkCommand equal = new AttendanceMarkCommand(ALICE.getStudentId(), 3, AttendanceStatus.PRESENT);
        assertEquals(command, command);
        assertEquals(command, equal);
        assertEquals(command.hashCode(), equal.hashCode());
        assertNotEquals(command, new AttendanceMarkCommand(DANIEL.getStudentId(), 3, AttendanceStatus.PRESENT));
        assertNotEquals(command, new AttendanceMarkCommand(ALICE.getStudentId(), 4, AttendanceStatus.PRESENT));
        assertNotEquals(command, new AttendanceMarkCommand(ALICE.getStudentId(), 3, AttendanceStatus.ABSENT));
        assertNotEquals(command, null);
        assertNotEquals(command, "attendance mark");
    }
}
