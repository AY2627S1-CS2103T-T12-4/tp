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
        assertEquals(Optional.empty(), history.next());
    }

    @Test
    public void previous_repeated_returnsOlderCommandsAndStopsAtOldest() {
        addBothCommands();
        assertEquals(Optional.of(SECOND_COMMAND), history.previous(""));
        assertEquals(Optional.of(FIRST_COMMAND), history.previous(SECOND_COMMAND));
        assertEquals(Optional.empty(), history.previous(FIRST_COMMAND));
        assertEquals(Optional.of(SECOND_COMMAND), history.next());
    }

    @Test
    public void next_pastNewestCommand_restoresTypedText() {
        addBothCommands();
        history.previous("student list");
        history.previous(SECOND_COMMAND);
        assertEquals(Optional.of(SECOND_COMMAND), history.next());
        assertEquals(Optional.of("student list"), history.next());
        assertEquals(Optional.empty(), history.next());
    }

    @Test
    public void add_whileRecalling_recallsNewCommandFirstAndClearsTypedText() {
        addBothCommands();
        history.previous("student list");
        history.add("attendance list w/3");
        assertEquals(Optional.of("attendance list w/3"), history.previous(""));
        assertEquals(Optional.of(""), history.next());
    }

    @Test
    public void add_sameCommandTwice_keepsBoth() {
        history.add(FIRST_COMMAND);
        history.add(FIRST_COMMAND);
        assertEquals(Optional.of(FIRST_COMMAND), history.previous(""));
        assertEquals(Optional.of(FIRST_COMMAND), history.previous(FIRST_COMMAND));
    }

    @Test
    public void add_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> history.add(null));
    }

    @Test
    public void previous_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> history.previous(null));
    }

    private void addBothCommands() {
        history.add(FIRST_COMMAND);
        history.add(SECOND_COMMAND);
    }
}
