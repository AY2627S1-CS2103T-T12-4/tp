package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;
import static seedu.address.testutil.TypicalAssignments.PS1;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalStudents.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.AssignmentRecord;
import seedu.address.model.student.StudentId;
import seedu.address.testutil.TypicalGroups;

public class AssignmentGradeCommandTest {
    private static final StudentId ALICE_ID = ALICE.getStudentId();
    private static final String UNKNOWN_STUDENT_ID = "A0999999Z";

    private Model modelWithSubmittedAssignment() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T01);
        model.addAssignment(NAME_T01, PS1.withSubmissionStatus(ALICE_ID, true));
        return model;
    }

    @Test
    public void execute_validGrade_recordsAndReplacesGrade() {
        Model model = modelWithSubmittedAssignment();
        Model expected = modelWithSubmittedAssignment();
        expected.setAssignment(NAME_T01, PS1.withSubmissionStatus(ALICE_ID, true).withGrade(ALICE_ID, 87));

        assertCommandSuccess(new AssignmentGradeCommand(NAME_PS1, ALICE_ID, 87), model,
                "Recorded grade 87 for Alice Tan's PS1.", expected);

        Model updatedExpected = modelWithSubmittedAssignment();
        updatedExpected.setAssignment(NAME_T01, PS1.withSubmissionStatus(ALICE_ID, true).withGrade(ALICE_ID, 91));
        assertCommandSuccess(new AssignmentGradeCommand(NAME_PS1, ALICE_ID, 91), model,
                "Recorded grade 91 for Alice Tan's PS1.", updatedExpected);
    }

    @Test
    public void execute_unsubmittedAssignment_reportsErrorWithoutChanges() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T01);
        model.addAssignment(NAME_T01, PS1);

        assertCommandFailure(new AssignmentGradeCommand(NAME_PS1, ALICE_ID, 87), model,
                AssignmentGradeCommand.MESSAGE_NOT_SUBMITTED);
        assertEquals(AssignmentRecord.notSubmitted(), model.getGroup(NAME_T01).findAssignment(NAME_PS1)
                .orElseThrow().getStudentRecord(ALICE_ID));
    }

    @Test
    public void execute_unknownAssignmentOrStudent_reportsErrorsWithoutChanges() {
        Model model = modelWithSubmittedAssignment();
        assertCommandFailure(new AssignmentGradeCommand(new AssignmentName("PS2"), ALICE_ID, 87), model,
                "Assignment PS2 does not exist in T01. Check the name, or create it with: assignment add a/PS2");
        assertCommandFailure(new AssignmentGradeCommand(NAME_PS1, new StudentId(UNKNOWN_STUDENT_ID), 87), model,
                AssignmentGradeCommand.MESSAGE_UNKNOWN_STUDENT);
        assertEquals(PS1.withSubmissionStatus(ALICE_ID, true), model.getGroup(NAME_T01).getAssignmentList().getFirst());
    }

    @Test
    public void execute_noActiveGroup_reportsError() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        assertCommandFailure(new AssignmentGradeCommand(NAME_PS1, ALICE_ID, 87), model,
                Messages.MESSAGE_NO_ACTIVE_GROUP);
    }

    @Test
    public void constructorAndExecute_invalidInputs_reportErrors() {
        assertThrows(NullPointerException.class, () -> new AssignmentGradeCommand(null, ALICE_ID, 87));
        assertThrows(NullPointerException.class, () -> new AssignmentGradeCommand(NAME_PS1, null, 87));
        assertThrows(IllegalArgumentException.class, AssignmentRecord.MESSAGE_INVALID_GRADE,
                () -> new AssignmentGradeCommand(NAME_PS1, ALICE_ID, 101));
        assertThrows(NullPointerException.class,
                () -> new AssignmentGradeCommand(NAME_PS1, ALICE_ID, 87).execute(null));
    }

    @Test
    public void equals_comparesAssignmentStudentAndGrade() {
        AssignmentGradeCommand command = new AssignmentGradeCommand(NAME_PS1, ALICE_ID, 87);
        assertEquals(command, new AssignmentGradeCommand(new AssignmentName("ps1"), ALICE_ID, 87));
        assertEquals(command.hashCode(), new AssignmentGradeCommand(NAME_PS1, ALICE_ID, 87).hashCode());
        assertNotEquals(command, new AssignmentGradeCommand(NAME_PS1, ALICE_ID, 91));
        assertNotEquals(command, new AssignmentGradeCommand(NAME_PS1, new StudentId(UNKNOWN_STUDENT_ID), 87));
        assertNotEquals(command, new AssignmentGradeCommand(new AssignmentName("PS2"), ALICE_ID, 87));
        assertNotEquals(command, null);
    }
}
