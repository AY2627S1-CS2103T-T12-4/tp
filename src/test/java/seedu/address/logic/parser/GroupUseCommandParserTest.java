package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.GroupUseCommand;
import seedu.address.model.group.GroupName;

public class GroupUseCommandParserTest {
    private final GroupUseCommandParser parser = new GroupUseCommandParser();

    @Test
    public void parse_validName_returnsSwitchCommand() {
        assertParseSuccess(parser, " g/ Tutorial   1-Thu ", new GroupUseCommand(new GroupName("Tutorial 1-Thu")));
    }

    @Test
    public void parse_missingOrBlankName_reportsSwitchUsage() {
        for (String input : new String[] {"", "  ", "g/", "g/   "}) {
            assertParseFailure(parser, input, "Missing group name. Usage: group use g/GROUP_NAME");
        }
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
