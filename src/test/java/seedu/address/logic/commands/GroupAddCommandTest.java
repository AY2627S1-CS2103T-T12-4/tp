package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.testutil.TypicalGroups;

public class GroupAddCommandTest {
    @Test
    public void execute_firstGroup_createsAndActivatesGroup() {
        Model model = new ModelManager();
        Model expected = new ModelManager();
        expected.addGroup(new Group(TypicalGroups.NAME_T01));
        expected.setActiveGroup(TypicalGroups.NAME_T01);

        assertCommandSuccess(new GroupAddCommand(TypicalGroups.NAME_T01), model,
                "Created tutorial group: T01.\nActive group is now T01.", expected);
        assertTrue(model.getActiveGroupStudentList().isEmpty());
    }

    @Test
    public void execute_anotherGroup_preservesGroupsAndChangesActiveGroup() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.addGroup(new Group(TypicalGroups.NAME_T04));
        expected.setActiveGroup(TypicalGroups.NAME_T04);

        assertCommandSuccess(new GroupAddCommand(TypicalGroups.NAME_T04), model,
                "Created tutorial group: T04.\nActive group is now T04.", expected);
        assertTrue(model.getActiveGroupStudentList().isEmpty());
        assertEquals(TypicalGroups.getT01(), model.getGroup(TypicalGroups.NAME_T01));
    }

    @Test
    public void execute_duplicateIgnoringCaseAndSpaces_preservesData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T02);
        for (String name : new String[] {"T01", "t01", "  t01  "}) {
            assertCommandFailure(new GroupAddCommand(new GroupName(name)), model,
                    "A tutorial group named T01 already exists.");
        }
    }

    @Test
    public void nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupAddCommand(null));
        assertThrows(NullPointerException.class, () -> new GroupAddCommand(TypicalGroups.NAME_T01).execute(null));
    }

    @Test
    public void equals_comparesGroupNames() {
        GroupAddCommand command = new GroupAddCommand(TypicalGroups.NAME_T01);
        assertEquals(command, command);
        assertEquals(command, new GroupAddCommand(new GroupName("t01")));
        assertEquals(command.hashCode(), new GroupAddCommand(new GroupName("t01")).hashCode());
        assertNotEquals(command, new GroupAddCommand(TypicalGroups.NAME_T02));
        assertNotEquals(command, null);
        assertNotEquals(command, "T01");
    }
}
