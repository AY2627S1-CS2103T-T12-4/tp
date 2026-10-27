package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.testutil.TypicalGroups;

public class RedoCommandTest {
    private final Model model = new ModelManager();

    @Test
    public void execute_nothingToRedo_reportsNothingWithoutChangingData() {
        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_NOTHING_TO_REDO, new ModelManager());
    }

    @Test
    public void execute_changeUndone_reappliesChangeAndAllowsUndo() {
        model.addGroup(new Group(TypicalGroups.NAME_T01));
        model.commitTAssist(new TAssist(), "group add g/T01");
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        model.undoTAssist();

        assertCommandSuccess(new RedoCommand(), model, "Redone: group add g/T01", expected);
        assertTrue(model.canUndoTAssist());
        assertFalse(model.canRedoTAssist());
    }

    @Test
    public void isUndoable_returnsFalse() {
        assertFalse(new RedoCommand().isUndoable());
    }

    @Test
    public void equals() {
        RedoCommand redoCommand = new RedoCommand();
        assertEquals(redoCommand, new RedoCommand());
        assertEquals(redoCommand.hashCode(), new RedoCommand().hashCode());
        assertNotEquals(redoCommand, new UndoCommand());
        assertNotEquals(redoCommand, null);
    }
}
