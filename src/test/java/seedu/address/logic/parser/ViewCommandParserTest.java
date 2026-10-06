package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.WorkspaceView;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;

public class ViewCommandParserTest {
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_allScreens_returnsRequestedView() throws Exception {
        for (WorkspaceView view : WorkspaceView.values()) {
            assertEquals(view, parser.parseCommand("view " + view.getKeyword())
                    .execute(new ModelManager()).getView().orElseThrow());
        }
    }

    @Test
    public void parseCommand_mixedCaseAndWhitespace_returnsRequestedView() throws Exception {
        assertEquals(WorkspaceView.ATTENDANCE, parser.parseCommand("  view   AtTenDancE  ")
                .execute(new ModelManager()).getView().orElseThrow());
    }

    @Test
    public void parseCommand_missingOrMultipleScreens_throwsParseException() {
        String[] inputs = {"view", "view students help", "view students students", "view students\thelp"};
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        for (String input : inputs) {
            assertThrows(ParseException.class, expected, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_unknownScreen_throwsParseException() {
        String expected = String.format(ViewCommand.MESSAGE_UNKNOWN_SCREEN, "unknown");
        assertThrows(ParseException.class, expected, () -> parser.parseCommand("view unknown"));
    }
}
