package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.GroupRenameCommand;
import seedu.address.model.group.GroupName;

public class GroupRenameCommandParserTest {
    private static final String MESSAGE_MISSING =
            "Missing group name or new name. Usage: group rename g/GROUP_NAME n/NEW_NAME";

    private final GroupRenameCommandParser parser = new GroupRenameCommandParser();

    @Test
    public void parse_validNames_returnsRenameCommand() {
        GroupRenameCommand expected = new GroupRenameCommand(new GroupName("T 01"), new GroupName("T01"));
        assertParseSuccess(parser, " g/ T 01 n/ T01 ", expected);
        assertParseSuccess(parser, " n/T01 g/T 01", expected);
    }

    @Test
    public void parse_missingOrBlankName_reportsRenameUsage() {
        for (String input : new String[] {"", "g/T01", "n/T02", "g/ n/T02", "g/T01 n/  "}) {
            assertParseFailure(parser, input, MESSAGE_MISSING);
        }
    }

    @Test
    public void parse_invalidName_reportsConstraints() {
        assertParseFailure(parser, "g/T_01 n/T02", GroupName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "g/T01 n/T_02", GroupName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
