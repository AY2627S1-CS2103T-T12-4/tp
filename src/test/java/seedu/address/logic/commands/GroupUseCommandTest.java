package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.GroupName;
import seedu.address.testutil.TypicalGroups;

public class GroupUseCommandTest {
    @Test
    public void execute_existingGroups_switchesStudentViewAndPreservesRosters() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        for (String name : new String[] {"t01", " T02 ", "T03", "T01"}) {
            GroupName groupName = new GroupName(name);
            Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
            expected.setActiveGroup(groupName);
            String storedName = expected.getActiveGroup().orElseThrow().getName().toString();

            assertCommandSuccess(new GroupUseCommand(groupName), model,
                    "Switched active group to " + storedName + ".", expected);
            assertEquals(model.getGroup(groupName).getStudentList(), model.getActiveGroupStudentList());
            assertEquals(TypicalGroups.getTypicalGroups(), model.getTAssist().getGroupList());
        }
    }

    @Test
    public void execute_alreadyActiveGroup_successAndPreservesData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());

        assertCommandSuccess(new GroupUseCommand(new GroupName("t01")), model,
                "Switched active group to T01.", expected);
    }

    @Test
    public void execute_unknownGroup_preservesData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        assertCommandFailure(new GroupUseCommand(TypicalGroups.NAME_T04), model,
                "Tutorial group T04 does not exist.");
        model.setActiveGroup(TypicalGroups.NAME_T01);
        assertCommandFailure(new GroupUseCommand(TypicalGroups.NAME_T04), model,
                "Tutorial group T04 does not exist.");
        assertEquals(TypicalGroups.getT01().getStudentList(), model.getActiveGroupStudentList());
        assertCommandFailure(new GroupUseCommand(TypicalGroups.NAME_T03), new ModelManager(),
                "Tutorial group T03 does not exist.");
    }

    @Test
    public void nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupUseCommand(null));
        assertThrows(NullPointerException.class, () -> new GroupUseCommand(TypicalGroups.NAME_T01).execute(null));
    }

    @Test
    public void equals_comparesGroupNames() {
        GroupUseCommand command = new GroupUseCommand(TypicalGroups.NAME_T01);
        assertEquals(command, command);
        assertEquals(command, new GroupUseCommand(new GroupName("t01")));
        assertEquals(command.hashCode(), new GroupUseCommand(new GroupName("t01")).hashCode());
        assertNotEquals(command, new GroupUseCommand(TypicalGroups.NAME_T02));
        assertNotEquals(command, null);
        assertNotEquals(command, "T01");
    }
}
