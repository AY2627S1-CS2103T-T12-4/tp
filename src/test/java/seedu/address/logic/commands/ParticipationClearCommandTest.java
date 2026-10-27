package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_STUDENT;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;
import static seedu.address.testutil.TypicalStudents.DANIEL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.testutil.TypicalGroups;

public class ParticipationClearCommandTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
    }

    @Test
    public void execute_recordedWeek_clearsOnlyThatWeekAndShowsOldScore() {
        Student recorded = ALICE.withParticipationRecord(3, 5).withParticipationRecord(4, 0)
                .withAttendanceRecord(4, "PRESENT");
        model.setStudent(TypicalGroups.NAME_T01, recorded);
        model.setStudent(TypicalGroups.NAME_T01, BEN.withParticipationRecord(4, 3));
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.setStudent(TypicalGroups.NAME_T01, recorded.withoutParticipationRecord(4));

        assertCommandSuccess(new ParticipationClearCommand(ALICE.getStudentId(), 4), model,
                "Cleared Alice Tan's participation score for Week 4: 0 to Not recorded.", expected);
    }

    @Test
    public void execute_scoreAtWeekBoundaries_clearsScore() {
        for (int week : new int[] {1, 13}) {
            setUp();
            model.setStudent(TypicalGroups.NAME_T01, ALICE.withParticipationRecord(week, 5));
            Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
            expected.setStudent(TypicalGroups.NAME_T01, ALICE);

            assertCommandSuccess(new ParticipationClearCommand(ALICE.getStudentId(), week), model,
                    "Cleared Alice Tan's participation score for Week " + week + ": 5 to Not recorded.", expected);
        }
    }

    @Test
    public void execute_unrecordedWeek_changesNothing() {
        model.setStudent(TypicalGroups.NAME_T01, ALICE.withParticipationRecord(3, 2));
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        ParticipationClearCommand command = new ParticipationClearCommand(ALICE.getStudentId(), 4);

        assertCommandSuccess(command, model,
                "Alice Tan has no participation score recorded for Week 4. Nothing changed.", expected);
    }

    @Test
    public void execute_twice_secondChangesNothing() {
        model.setStudent(TypicalGroups.NAME_T01, ALICE.withParticipationRecord(3, 4));
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.setStudent(TypicalGroups.NAME_T01, ALICE);
        ParticipationClearCommand command = new ParticipationClearCommand(ALICE.getStudentId(), 3);

        assertCommandSuccess(command, model,
                "Cleared Alice Tan's participation score for Week 3: 4 to Not recorded.", expected);
        assertCommandSuccess(command, model,
                "Alice Tan has no participation score recorded for Week 3. Nothing changed.", expected);
    }

    @Test
    public void execute_studentInAnotherGroup_failsWithoutChangingData() {
        assertCommandFailure(new ParticipationClearCommand(DANIEL.getStudentId(), 3), model,
                String.format(MESSAGE_UNKNOWN_STUDENT, DANIEL.getStudentId(), TypicalGroups.NAME_T01));
    }

    @Test
    public void execute_noActiveGroup_failsWithoutChangingData() {
        Model inactive = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        assertCommandFailure(new ParticipationClearCommand(ALICE.getStudentId(), 3), inactive, MESSAGE_NO_ACTIVE_GROUP);
    }

    @Test
    public void invalidConstruction_throwsException() {
        assertThrows(NullPointerException.class, () -> new ParticipationClearCommand(null, 3));
        assertThrows(IllegalArgumentException.class, () -> new ParticipationClearCommand(ALICE.getStudentId(), 0));
        assertThrows(IllegalArgumentException.class, () -> new ParticipationClearCommand(ALICE.getStudentId(), 14));
    }

    @Test
    public void equals_comparesStudentAndWeek() {
        ParticipationClearCommand command = new ParticipationClearCommand(ALICE.getStudentId(), 3);
        ParticipationClearCommand equal = new ParticipationClearCommand(ALICE.getStudentId(), 3);
        assertEquals(command, command);
        assertEquals(command, equal);
        assertEquals(command.hashCode(), equal.hashCode());
        assertNotEquals(command, new ParticipationClearCommand(DANIEL.getStudentId(), 3));
        assertNotEquals(command, new ParticipationClearCommand(ALICE.getStudentId(), 4));
        assertNotEquals(command, null);
        assertNotEquals(command, "participation clear");
    }
}
