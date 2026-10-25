package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AssignmentListCommand;
import seedu.address.model.assignment.AssignmentName;

public class AssignmentListCommandParserTest {
    private final AssignmentListCommandParser parser = new AssignmentListCommandParser();

    @Test
    public void parse_validName_success() {
        assertParseSuccess(parser, "a/PS1", new AssignmentListCommand(NAME_PS1));
        assertParseSuccess(parser, " a/  ps1 ", new AssignmentListCommand(NAME_PS1));
        assertParseSuccess(parser, "a/Problem   Set-1",
                new AssignmentListCommand(new AssignmentName("Problem Set-1")));
    }

    @Test
    public void parse_invalidName_reportsConstraints() {
        for (String name : new String[] {"", "   ", "-", "PS1_A", "PS1/PS2", "a".repeat(61)}) {
            assertParseFailure(parser, "a/" + name, AssignmentName.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_malformedParameters_reportsSpecificError() {
        assertParseFailure(parser, "", String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, "a/"));
        assertParseFailure(parser, "a/PS1 a/PS2", Messages.getErrorMessageForDuplicatePrefixes(new Prefix("a/")));
        assertParseFailure(parser, "a/PS1 w/3",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "w/", "a/"));
        assertParseFailure(parser, "PS1", String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "PS1", "a/"));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
