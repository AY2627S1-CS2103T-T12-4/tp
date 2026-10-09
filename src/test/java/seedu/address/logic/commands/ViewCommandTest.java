package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.WorkspaceView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.TypicalGroups;

public class ViewCommandTest {
    @Test
    public void execute_showScreen_preservesData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        Model expected = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
        expected.setActiveGroup(TypicalGroups.NAME_T01);
        CommandResult result = new ViewCommand(WorkspaceView.GROUPS).execute(model);
        assertEquals(expected, model);
        assertEquals(WorkspaceView.GROUPS, result.getView().orElseThrow());
        assertFalse(result.isExit());
        assertFalse(result.isShowHelp());
    }

    @Test
    public void constructor_nullScreen_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ViewCommand(null));
    }
}
