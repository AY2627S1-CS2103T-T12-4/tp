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
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.student.StudentId;
import seedu.address.testutil.TypicalGroups;

public class AssignmentSubmitCommandTest {
    private static final StudentId ALICE_ID = ALICE.getStudentId();

    private Model modelWithAssignment() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T01);
        model.addAssignment(NAME_T01, PS1);
        return model;
    }

    @Test
    public void execute_yes_recordsSubmissionAndNamesStudent() {
        Model model = modelWithAssignment();
        Model expected = modelWithAssignment();
        expected.setAssignment(NAME_T01, PS1.withSubmissionStatus(ALICE_ID, true));

        assertCommandSuccess(new AssignmentSubmitCommand(NAME_PS1, ALICE_ID, true), model,
                "Updated Alice Tan's PS1 submission status to submitted.", expected);
    }

    @Test
    public void execute_no_replacesSubmissionAndClearsGrade() {
        Assignment prior = PS1.withSubmissionStatus(ALICE_ID, true).withGrade(ALICE_ID, 87);
        Model model = modelWithAssignment();
        model.setAssignment(NAME_T01, prior);
        Model expected = modelWithAssignment();
        expected.setAssignment(NAME_T01, prior.withSubmissionStatus(ALICE_ID, false));

        assertCommandSuccess(new AssignmentSubmitCommand(NAME_PS1, ALICE_ID, false), model,
                "Updated Alice Tan's PS1 submission status to not submitted.", expected);
    }

    @Test
    public void execute_unknownStudent_reportsErrorWithoutChanges() {
        Model model = modelWithAssignment();
        Model expected = modelWithAssignment();
        assertCommandFailure(new AssignmentSubmitCommand(NAME_PS1, new StudentId("A0999999Z"), true), model,
                AssignmentSubmitCommand.MESSAGE_UNKNOWN_STUDENT);
        assertEquals(expected, model);
    }

    @Test
    public void execute_unknownAssignment_reportsErrorWithoutChanges() {
        Model model = modelWithAssignment();
        assertCommandFailure(new AssignmentSubmitCommand(new AssignmentName("PS2"), ALICE_ID, true), model,
                "Assignment PS2 does not exist in T01. Check the name, or create it with: assignment add a/PS2");
    }

    @Test
    public void execute_noActiveGroup_reportsError() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        assertCommandFailure(new AssignmentSubmitCommand(NAME_PS1, ALICE_ID, true), model,
                Messages.MESSAGE_NO_ACTIVE_GROUP);
    }

    @Test
    public void constructorAndExecute_nullInputs_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AssignmentSubmitCommand(null, ALICE_ID, true));
        assertThrows(NullPointerException.class, () -> new AssignmentSubmitCommand(NAME_PS1, null, true));
        assertThrows(NullPointerException.class, () ->
                new AssignmentSubmitCommand(NAME_PS1, ALICE_ID, true).execute(null));
    }

    @Test
    public void equals_comparesAssignmentStudentAndStatus() {
        AssignmentSubmitCommand command = new AssignmentSubmitCommand(NAME_PS1, ALICE_ID, true);
        assertEquals(command, new AssignmentSubmitCommand(new AssignmentName("ps1"), ALICE_ID, true));
        assertEquals(command.hashCode(), new AssignmentSubmitCommand(NAME_PS1, ALICE_ID, true).hashCode());
        assertNotEquals(command, new AssignmentSubmitCommand(NAME_PS1, ALICE_ID, false));
        assertNotEquals(command, new AssignmentSubmitCommand(NAME_PS1, new StudentId("A0999999Z"), true));
        assertNotEquals(command, new AssignmentSubmitCommand(new AssignmentName("PS2"), ALICE_ID, true));
        assertNotEquals(command, null);
    }
}
