package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.GroupAddCommand;
import seedu.address.model.group.GroupName;

public class GroupAddCommandParserTest {
    private final GroupAddCommandParser parser = new GroupAddCommandParser();

    @Test
    public void parse_validNames_normalizesSpacesAndPreservesCase() {
        for (String name : new String[] {"T01", " t01 ", "  Tutorial   1-Thu ", "教室 1", "a".repeat(60)}) {
            assertParseSuccess(parser, " g/" + name, new GroupAddCommand(new GroupName(name)));
        }
    }

    @Test
    public void parse_missingOrBlankName_reportsUsage() {
        for (String input : new String[] {"", "  ", "g/", "g/   "}) {
            assertParseFailure(parser, input, "Missing group name. Usage: group add g/GROUP_NAME");
        }
    }

    @Test
    public void parse_invalidName_reportsConstraints() {
        for (String name : new String[] {"T_01", "T01!", "--", "a".repeat(61), "T01/T02"}) {
            assertParseFailure(parser, "g/" + name, GroupName.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_duplicatePrefix_reportsDuplicate() {
        assertParseFailure(parser, "g/T01 g/T02",
                Messages.getErrorMessageForDuplicatePrefixes(new Prefix("g/")));
    }

    @Test
    public void parse_unknownPrefix_reportsAllowedPrefix() {
        String[][] cases = {
            {"n/T01", "n/"},
            {"g/T01 x/value", "x/"},
            {"G/T01", "G/"},
            {"T01g/T02", "T01g/"}
        };
        for (String[] testCase : cases) {
            assertParseFailure(parser, testCase[0],
                    String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, testCase[1], "g/"));
        }
    }

    @Test
    public void parse_unexpectedText_reportsFormatProblem() {
        assertParseFailure(parser, "T01", String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "T01", "g/"));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
