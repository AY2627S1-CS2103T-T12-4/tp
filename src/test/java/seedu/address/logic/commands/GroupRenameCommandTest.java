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

public class GroupRenameCommandTest {
    private static final GroupName NEW_NAME = new GroupName("T02-Thu");

    @Test
    public void execute_activeGroup_renamesGroupAndKeepsItActive() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T02);
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.renameGroup(TypicalGroups.NAME_T02, NEW_NAME);

        assertCommandSuccess(new GroupRenameCommand(new GroupName("t02"), NEW_NAME), model,
                "Renamed tutorial group T02 to T02-Thu.", expected);
        assertEquals(NEW_NAME, model.getActiveGroup().orElseThrow().getName());
        assertEquals(TypicalGroups.getT02().getStudentList(), model.getActiveGroupStudentList());
    }

    @Test
    public void execute_onlyChangesCase_success() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        expected.renameGroup(TypicalGroups.NAME_T01, new GroupName("t01"));

        assertCommandSuccess(new GroupRenameCommand(TypicalGroups.NAME_T01, new GroupName("t01")), model,
                "Renamed tutorial group T01 to t01.", expected);
    }

    @Test
    public void execute_sameNameAfterNormalizing_reportsNoChanges() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());

        for (String newName : new String[] {"T01", "  T01 "}) {
            assertCommandSuccess(new GroupRenameCommand(new GroupName("t01"), new GroupName(newName)), model,
                    "Tutorial group T01 already has that name. Nothing changed.", expected);
        }
    }

    @Test
    public void execute_unknownGroup_preservesData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        assertCommandFailure(new GroupRenameCommand(TypicalGroups.NAME_T04, NEW_NAME), model,
                "Tutorial group T04 does not exist.");
        assertEquals(TypicalGroups.getTypicalGroups(), model.getTAssist().getGroupList());
    }

    @Test
    public void execute_nameOfAnotherGroup_preservesData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        assertCommandFailure(new GroupRenameCommand(TypicalGroups.NAME_T01, new GroupName(" t03 ")), model,
                "A tutorial group named T03 already exists.");
        assertEquals(TypicalGroups.getTypicalGroups(), model.getTAssist().getGroupList());
    }

    @Test
    public void nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupRenameCommand(null, NEW_NAME));
        assertThrows(NullPointerException.class, () -> new GroupRenameCommand(TypicalGroups.NAME_T01, null));
        assertThrows(NullPointerException.class, () ->
                new GroupRenameCommand(TypicalGroups.NAME_T01, NEW_NAME).execute(null));
    }

    @Test
    public void equals_comparesBothNames() {
        GroupRenameCommand command = new GroupRenameCommand(TypicalGroups.NAME_T01, NEW_NAME);
        assertEquals(command, command);
        assertEquals(command, new GroupRenameCommand(new GroupName("t01"), NEW_NAME));
        assertEquals(command.hashCode(), new GroupRenameCommand(new GroupName("t01"), NEW_NAME).hashCode());
        assertNotEquals(command, new GroupRenameCommand(TypicalGroups.NAME_T02, NEW_NAME));
        assertNotEquals(command, new GroupRenameCommand(TypicalGroups.NAME_T01, TypicalGroups.NAME_T04));
        assertNotEquals(command, new GroupUseCommand(TypicalGroups.NAME_T01));
        assertNotEquals(command, null);
    }
}
