package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class GroupListCommandTest {
    @Test
    public void execute_noGroups_showsCreationHintAndPreservesData() {
        assertCommandSuccess(new GroupListCommand(), new ModelManager(),
                "No tutorial groups exist. Create one using: group add g/GROUP_NAME", new ModelManager());
    }

    @Test
    public void execute_oneGroup_showsNumberAndActiveGroup() {
        Model model = new ModelManager(new TAssistBuilder().withGroup(TypicalGroups.getT01())
                .withActiveGroup(TypicalGroups.NAME_T01).build(), new UserPrefs());
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());

        assertCommandSuccess(new GroupListCommand(), model, "1. T01\nActive group: T01", expected);
    }

    @Test
    public void execute_multipleGroups_showsCreationOrderAndPreservesData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T02);
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());

        assertCommandSuccess(new GroupListCommand(), model,
                "1. T01\n2. T02\n3. T03\nActive group: T02", expected);
    }

    @Test
    public void execute_groupsWithoutActiveGroup_showsNoneAndPreservesData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());

        assertCommandSuccess(new GroupListCommand(), model,
                "1. T01\n2. T02\n3. T03\nActive group: None", expected);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupListCommand().execute(null));
    }
}
