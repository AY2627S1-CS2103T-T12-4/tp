package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.GroupName;
import seedu.address.testutil.TypicalGroups;

public class GroupDeleteCommandTest {
    @Test
    public void execute_inactiveGroup_deletesGroupAndKeepsActiveGroup() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T02);
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.removeGroup(TypicalGroups.NAME_T01);

        assertCommandSuccess(new GroupDeleteCommand(new GroupName("t01")), model,
                "Deleted tutorial group T01 and its 3 student(s).", expected);
        assertEquals(TypicalGroups.NAME_T02, model.getActiveGroup().orElseThrow().getName());
    }

    @Test
    public void execute_activeGroup_deletesGroupAndSaysNoGroupIsActive() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T02);
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.removeGroup(TypicalGroups.NAME_T02);

        assertCommandSuccess(new GroupDeleteCommand(TypicalGroups.NAME_T02), model,
                "Deleted tutorial group T02 and its 2 student(s).\n"
                        + "No group is active now. Select one with: group use g/GROUP_NAME", expected);
        assertEquals(0, model.getActiveGroupStudentList().size());
    }

    @Test
    public void getConfirmationPrompt_namesGroupAndCanBeSkipped() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());

        assertEquals(Optional.of("Delete tutorial group T01 and all 3 student(s) in it, with their attendance, "
                        + "participation, submission, and grade records?"),
                new GroupDeleteCommand(new GroupName("t01")).getConfirmationPrompt(model));
        assertEquals(Optional.empty(), new GroupDeleteCommand(TypicalGroups.NAME_T01, true)
                .getConfirmationPrompt(model));
        assertEquals(Optional.empty(), new GroupDeleteCommand(TypicalGroups.NAME_T04)
                .getConfirmationPrompt(model));
    }

    @Test
    public void execute_unknownGroup_preservesData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        assertCommandFailure(new GroupDeleteCommand(TypicalGroups.NAME_T04), model,
                "Tutorial group T04 does not exist. See groups with: group list");
        assertEquals(TypicalGroups.getTypicalGroups(), model.getTAssist().getGroupList());
    }

    @Test
    public void nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupDeleteCommand(null));
        assertThrows(NullPointerException.class, () -> new GroupDeleteCommand(TypicalGroups.NAME_T01).execute(null));
    }

    @Test
    public void equals_comparesGroupNames() {
        GroupDeleteCommand command = new GroupDeleteCommand(TypicalGroups.NAME_T01);
        assertEquals(command, command);
        assertEquals(command, new GroupDeleteCommand(new GroupName("t01")));
        assertEquals(command.hashCode(), new GroupDeleteCommand(new GroupName("t01")).hashCode());
        assertNotEquals(command, new GroupDeleteCommand(TypicalGroups.NAME_T02));
        assertNotEquals(command, new GroupDeleteCommand(TypicalGroups.NAME_T01, true));
        assertNotEquals(command, new GroupUseCommand(TypicalGroups.NAME_T01));
        assertNotEquals(command, null);
    }
}
