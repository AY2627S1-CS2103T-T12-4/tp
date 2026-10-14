package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.StudentListCommand;

public class StudentListCommandParserTest {
    private final StudentListCommandParser parser = new StudentListCommandParser();

    @Test
    public void parse_noArguments_returnsCommand() throws Exception {
        assertInstanceOf(StudentListCommand.class, parser.parse(""));
        assertInstanceOf(StudentListCommand.class, parser.parse(" \t "));
    }

    @Test
    public void parse_extraInput_reportsNoParameters() {
        for (String input : new String[] {"extra", "id/A1", "n/Alice"}) {
            assertParseFailure(parser, input, TAssistArgumentParser.MESSAGE_NO_PARAMETERS);
        }
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
