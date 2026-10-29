package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.GroupRenameCommand;
import seedu.address.model.group.GroupName;

public class GroupRenameCommandParserTest {
    private static final String USAGE = " Usage: group rename g/GROUP_NAME n/NEW_NAME";

    private final GroupRenameCommandParser parser = new GroupRenameCommandParser();

    @Test
    public void parse_validNames_returnsRenameCommand() {
        GroupRenameCommand expected = new GroupRenameCommand(new GroupName("T 01"), new GroupName("T01"));
        assertParseSuccess(parser, " g/ T 01 n/ T01 ", expected);
        assertParseSuccess(parser, " n/T01 g/T 01", expected);
    }

    @Test
    public void parse_missingOrBlankName_reportsRenameUsage() {
        for (String input : new String[] {"", "n/T02", "g/ n/T02"}) {
            assertParseFailure(parser, input, "Missing group name." + USAGE);
        }
        for (String input : new String[] {"g/T01", "g/T01 n/  "}) {
            assertParseFailure(parser, input, "Missing new group name." + USAGE);
        }
    }

    @Test
    public void parse_invalidName_reportsConstraints() {
        assertParseFailure(parser, "g/T_01 n/T02", GroupName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "g/T01 n/T_02", GroupName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_missingSpaceBeforePrefix_reportsMissingSpace() {
        assertParseFailure(parser, "g/T01n/T02",
                String.format(TAssistArgumentParser.MESSAGE_MISSING_SPACE, "n/", "g/T01n/T02"));
        assertParseFailure(parser, "n/T02g/T01",
                String.format(TAssistArgumentParser.MESSAGE_MISSING_SPACE, "g/", "n/T02g/T01"));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
