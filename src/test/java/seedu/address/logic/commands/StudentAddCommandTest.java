package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T02;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.FIONA;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TypicalGroups;

public class StudentAddCommandTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T01);
    }

    @Test
    public void execute_newStudent_updatesActiveRosterOnly() {
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.addStudent(NAME_T01, FIONA);
        assertCommandSuccess(new StudentAddCommand(FIONA), model, "Added Fiona Lee to T01.", expected);
        assertEquals(expected.getActiveGroupStudentList(), model.getActiveGroupStudentList());
    }

    @Test
    public void execute_sameNameOrIdInAnotherGroup_succeeds() {
        Student sameName = new StudentBuilder(FIONA).withName("Alice Tan").build();
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.addStudent(NAME_T01, sameName);
        assertCommandSuccess(new StudentAddCommand(sameName), model, "Added Alice Tan to T01.", expected);

        model.setActiveGroup(NAME_T02);
        expected.setActiveGroup(NAME_T02);
        expected.addStudent(NAME_T02, ALICE);
        assertCommandSuccess(new StudentAddCommand(ALICE), model, "Added Alice Tan to T02.", expected);
    }

    @Test
    public void execute_duplicateIdOrNoActiveGroup_preservesData() {
        Student duplicate = new StudentBuilder(FIONA).withStudentId(" a0123456x ").build();
        assertCommandFailure(new StudentAddCommand(duplicate), model, "Student ID A0123456X already exists in T01.");
        assertCommandFailure(new StudentAddCommand(FIONA), new ModelManager(), Messages.MESSAGE_NO_ACTIVE_GROUP);
    }

    @Test
    public void nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentAddCommand(null));
        assertThrows(NullPointerException.class, () -> new StudentAddCommand(ALICE).execute(null));
    }

    @Test
    public void equals_comparesStudentDetails() {
        StudentAddCommand command = new StudentAddCommand(ALICE);
        assertEquals(command, command);
        assertEquals(command, new StudentAddCommand(new StudentBuilder(ALICE).build()));
        assertEquals(command.hashCode(), new StudentAddCommand(ALICE).hashCode());
        assertNotEquals(command, new StudentAddCommand(FIONA));
        assertNotEquals(command, new StudentAddCommand(new StudentBuilder(ALICE).withName("Alicia").build()));
        assertNotEquals(command, null);
        assertNotEquals(command, "student");
    }
}
