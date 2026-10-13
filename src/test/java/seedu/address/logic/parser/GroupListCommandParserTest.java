package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.GroupListCommand;

public class GroupListCommandParserTest {
    private final GroupListCommandParser parser = new GroupListCommandParser();

    @Test
    public void parse_noArguments_returnsListCommand() throws Exception {
        assertTrue(parser.parse("") instanceof GroupListCommand);
        assertTrue(parser.parse(" \t ") instanceof GroupListCommand);
    }

    @Test
    public void parse_extraInput_reportsNoParameters() {
        for (String input : new String[] {"T01", "g/T01", "list", "x/value"}) {
            assertParseFailure(parser, input, TAssistArgumentParser.MESSAGE_NO_PARAMETERS);
        }
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
