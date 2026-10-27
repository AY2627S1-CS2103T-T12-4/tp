package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ExitCommand;

public class ExitCommandParserTest {
    private final ExitCommandParser parser = new ExitCommandParser();

    @Test
    public void parse_noArguments_returnsExitCommand() throws Exception {
        assertTrue(parser.parse("") instanceof ExitCommand);
        assertTrue(parser.parse(" \t ") instanceof ExitCommand);
    }

    @Test
    public void parse_extraInput_reportsNoParameters() {
        for (String input : new String[] {"unexpected", "3", "now please", "g/T01"}) {
            assertParseFailure(parser, input, TAssistArgumentParser.MESSAGE_NO_PARAMETERS);
        }
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
