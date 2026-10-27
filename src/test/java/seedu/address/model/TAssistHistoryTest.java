package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T04;

import org.junit.jupiter.api.Test;

import seedu.address.model.group.Group;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class TAssistHistoryTest {
    private final TAssist empty = new TAssist();
    private final TAssist typical = TypicalGroups.getTypicalTAssist();
    private final TAssist typicalWithT01Active = new TAssistBuilder(TypicalGroups.getTypicalTAssist())
            .withActiveGroup(NAME_T01).build();

    private final TAssistHistory history = new TAssistHistory();

    @Test
    public void constructor_newHistory_hasNothingToUndoOrRedo() {
        assertFalse(history.canUndo());
        assertFalse(history.canRedo());
        assertThrows(IllegalStateException.class, () -> history.undo(empty));
        assertThrows(IllegalStateException.class, () -> history.redo(empty));
    }

    @Test
    public void undoAndRedo_twoChanges_moveThroughVersionsInOrder() {
        history.record(empty, "first");
        history.record(typical, "second");

        assertEquals(new TAssistHistory.Version(typical, "second"), history.undo(typicalWithT01Active));
        assertEquals(new TAssistHistory.Version(empty, "first"), history.undo(typical));
        assertFalse(history.canUndo());

        assertEquals(new TAssistHistory.Version(typical, "first"), history.redo(empty));
        assertEquals(new TAssistHistory.Version(typicalWithT01Active, "second"), history.redo(typical));
        assertFalse(history.canRedo());
        assertTrue(history.canUndo());
    }

    @Test
    public void record_afterUndo_discardsUndoneChanges() {
        history.record(empty, "first");
        history.undo(typical);
        assertTrue(history.canRedo());

        history.record(empty, "another");

        assertFalse(history.canRedo());
        assertEquals("another", history.undo(typicalWithT01Active).description());
    }

    @Test
    public void record_moreThanMaxChanges_forgetsOldestChange() {
        for (int i = 0; i <= TAssistHistory.MAX_CHANGES; i++) {
            history.record(empty, "change " + i);
        }

        for (int i = TAssistHistory.MAX_CHANGES; i > 0; i--) {
            assertEquals("change " + i, history.undo(empty).description());
        }
        assertFalse(history.canUndo());
    }

    @Test
    public void version_sourceChangedLater_keepsCopy() {
        TAssist source = new TAssist(typical);
        TAssistHistory.Version version = new TAssistHistory.Version(source, "change");

        source.addGroup(new Group(NAME_T04));

        assertEquals(typical, version.data());
        assertThrows(NullPointerException.class, () -> new TAssistHistory.Version(null, "change"));
        assertThrows(NullPointerException.class, () -> new TAssistHistory.Version(typical, null));
    }

    @Test
    public void copyConstructor_copyChanged_originalUnchanged() {
        history.record(empty, "first");
        TAssistHistory copy = new TAssistHistory(history);
        assertEquals(history, copy);
        assertEquals(history.hashCode(), copy.hashCode());

        copy.undo(typical);

        assertNotEquals(history, copy);
        assertTrue(history.canUndo());
        assertFalse(history.canRedo());
    }

    @Test
    public void equals() {
        assertTrue(history.equals(history));
        assertTrue(history.equals(new TAssistHistory()));
        assertFalse(history.equals(null));
        assertFalse(history.equals(empty));

        TAssistHistory other = new TAssistHistory();
        other.record(empty, "change");
        assertFalse(history.equals(other));
    }

    @Test
    public void nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TAssistHistory(null));
        assertThrows(NullPointerException.class, () -> history.record(null, "change"));
        assertThrows(NullPointerException.class, () -> history.record(empty, null));
        history.record(empty, "change");
        assertThrows(NullPointerException.class, () -> history.undo(null));
    }
}
