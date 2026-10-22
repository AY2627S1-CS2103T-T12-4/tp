package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    private static final String FIRST_COMMAND = "group use g/T01";
    private static final String SECOND_COMMAND = "attendance mark id/A0123456X w/3 s/present";

    private final CommandHistory history = new CommandHistory();

    @Test
    public void previous_emptyHistory_returnsEmpty() {
        assertEquals(Optional.empty(), history.previous("help"));
    }

    @Test
    public void next_notRecalling_returnsEmpty() {
        history.add(FIRST_COMMAND);
        assertEquals(Optional.empty(), history.next("help"));
    }

    @Test
    public void previous_repeated_returnsOlderCommandsAndStopsAtOldest() {
        addBothCommands();
        assertEquals(Optional.of(SECOND_COMMAND), history.previous(""));
        assertEquals(Optional.of(FIRST_COMMAND), history.previous(SECOND_COMMAND));
        assertEquals(Optional.empty(), history.previous(FIRST_COMMAND));
        assertEquals(Optional.of(SECOND_COMMAND), history.next(FIRST_COMMAND));
    }

    @Test
    public void next_pastNewestCommand_restoresTypedText() {
        addBothCommands();
        history.previous("student list");
        history.previous(SECOND_COMMAND);
        assertEquals(Optional.of(SECOND_COMMAND), history.next(FIRST_COMMAND));
        assertEquals(Optional.of("student list"), history.next(SECOND_COMMAND));
        assertEquals(Optional.empty(), history.next("student list"));
    }

    @Test
    public void previousAndNext_editedRecalledCommand_keepsEdit() {
        addBothCommands();
        history.previous("");
        String editedCommand = SECOND_COMMAND.replace("A0123456X", "A0234567Y");
        assertEquals(Optional.of(FIRST_COMMAND), history.previous(editedCommand));
        assertEquals(Optional.of(editedCommand), history.next(FIRST_COMMAND));
    }

    @Test
    public void add_whileRecalling_recallsNewCommandFirstAndClearsTypedText() {
        addBothCommands();
        history.previous("student list");
        history.previous("group use g/T02");
        history.add("attendance list w/3");
        assertEquals(Optional.of("attendance list w/3"), history.previous(""));
        assertEquals(Optional.of(SECOND_COMMAND), history.previous("attendance list w/3"),
                "Edits are discarded on add");
        assertEquals(Optional.of(FIRST_COMMAND), history.previous(SECOND_COMMAND));
        assertEquals(Optional.of(SECOND_COMMAND), history.next(FIRST_COMMAND));
        assertEquals(Optional.of("attendance list w/3"), history.next(SECOND_COMMAND));
        assertEquals(Optional.of(""), history.next("attendance list w/3"));
    }

    @Test
    public void previous_newestCommandStillShown_recallsCommandBeforeIt() {
        addBothCommands();
        assertEquals(Optional.of(FIRST_COMMAND), history.previous(SECOND_COMMAND));
        assertEquals(Optional.of(SECOND_COMMAND), history.next(FIRST_COMMAND));
        assertEquals(Optional.empty(), history.next(SECOND_COMMAND));
    }

    @Test
    public void previous_sameCommandTwice_skipsRepeat() {
        history.add(FIRST_COMMAND);
        history.add(FIRST_COMMAND);
        assertEquals(Optional.of(FIRST_COMMAND), history.previous(""));
        assertEquals(Optional.empty(), history.previous(FIRST_COMMAND));
        assertEquals(Optional.of(""), history.next(FIRST_COMMAND));
    }

    @Test
    public void add_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> history.add(null));
    }

    @Test
    public void previousAndNext_null_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> history.previous(null));
        assertThrows(NullPointerException.class, () -> history.next(null));
    }

    private void addBothCommands() {
        history.add(FIRST_COMMAND);
        history.add(SECOND_COMMAND);
    }
}
