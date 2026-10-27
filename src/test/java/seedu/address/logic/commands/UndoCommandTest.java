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
import seedu.address.model.group.Group;
import seedu.address.testutil.TypicalGroups;

public class UndoCommandTest {
    private final Model model = new ModelManager();

    @Test
    public void execute_nothingToUndo_reportsNothingWithoutChangingData() {
        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_NOTHING_TO_UNDO, new ModelManager());
    }

    @Test
    public void execute_changeRecorded_restoresPreviousDataAndAllowsRedo() {
        model.addGroup(new Group(TypicalGroups.NAME_T01));
        model.commitTAssist(new TAssist(), "group add g/T01");

        assertCommandSuccess(new UndoCommand(), model, "Undone: group add g/T01", new ModelManager());
        assertFalse(model.canUndoTAssist());
        assertTrue(model.canRedoTAssist());
    }

    @Test
    public void isUndoable_returnsFalse() {
        assertFalse(new UndoCommand().isUndoable());
    }

    @Test
    public void equals() {
        UndoCommand undoCommand = new UndoCommand();
        assertEquals(undoCommand, new UndoCommand());
        assertEquals(undoCommand.hashCode(), new UndoCommand().hashCode());
        assertNotEquals(undoCommand, new RedoCommand());
        assertNotEquals(undoCommand, null);
    }
}
