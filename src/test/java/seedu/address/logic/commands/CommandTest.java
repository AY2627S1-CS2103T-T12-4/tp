package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_ASSIGNMENT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_STUDENT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;
import static seedu.address.testutil.TypicalAssignments.NAME_PS2;
import static seedu.address.testutil.TypicalAssignments.PS1;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.DANIEL;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.testutil.TypicalGroups;

public class CommandTest {

    private Group activeT01() throws CommandException {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.addAssignment(NAME_T01, PS1);
        model.setActiveGroup(NAME_T01);
        return Command.getActiveGroup(model);
    }

    @Test
    public void getActiveGroup_groupActive_returnsGroup() throws CommandException {
        assertEquals(NAME_T01, activeT01().getName());
    }

    @Test
    public void getActiveGroup_noActiveGroup_throwsCommandException() {
        assertThrows(CommandException.class, MESSAGE_NO_ACTIVE_GROUP, () -> Command.getActiveGroup(new ModelManager()));
    }

    @Test
    public void getStudent_studentInGroup_returnsStudent() throws CommandException {
        assertEquals(ALICE, Command.getStudent(activeT01(), ALICE.getStudentId()));
    }

    @Test
    public void getStudent_studentInAnotherGroup_throwsCommandException() throws CommandException {
        Group group = activeT01();
        assertThrows(CommandException.class, MESSAGE_UNKNOWN_STUDENT, () ->
                Command.getStudent(group, DANIEL.getStudentId()));
    }

    @Test
    public void getAssignment_assignmentInGroup_returnsAssignment() throws CommandException {
        assertEquals(NAME_PS1, Command.getAssignment(activeT01(), NAME_PS1).getName());
    }

    @Test
    public void getAssignment_unknownAssignment_throwsCommandException() throws CommandException {
        Group group = activeT01();
        String expectedMessage = String.format(MESSAGE_UNKNOWN_ASSIGNMENT, NAME_PS2, NAME_T01, NAME_PS2);
        assertThrows(CommandException.class, expectedMessage, () -> Command.getAssignment(group, NAME_PS2));
    }
}
