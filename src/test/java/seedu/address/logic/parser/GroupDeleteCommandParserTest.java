package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.GroupDeleteCommand;
import seedu.address.model.group.GroupName;

public class GroupDeleteCommandParserTest {
    private final GroupDeleteCommandParser parser = new GroupDeleteCommandParser();

    @Test
    public void parse_validName_returnsDeleteCommand() {
        assertParseSuccess(parser, " g/ Tutorial   1-Thu ", new GroupDeleteCommand(new GroupName("Tutorial 1-Thu")));
    }

    @Test
    public void parse_missingOrBlankName_reportsDeleteUsage() {
        for (String input : new String[] {"", "  ", "g/", "g/   "}) {
            assertParseFailure(parser, input, "Missing group name. Usage: group delete g/GROUP_NAME [f/yes]");
        }
    }

    @Test
    public void parse_fastDeleteOption_skipsConfirmation() {
        assertParseSuccess(parser, "g/T01 f/yes", new GroupDeleteCommand(new GroupName("T01"), true));
        assertParseSuccess(parser, "f/YES g/T01", new GroupDeleteCommand(new GroupName("T01"), true));
        assertParseFailure(parser, "g/T01 f/no", GroupDeleteCommandParser.MESSAGE_INVALID_FAST_DELETE_OPTION);
        assertParseFailure(parser, "g/T01 f/yes now",
                String.format(TAssistArgumentParser.MESSAGE_EXTRA_TEXT, "f/yes", "now"));
    }

    @Test
    public void parse_invalidName_reportsConstraints() {
        assertParseFailure(parser, "g/T_01", GroupName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
