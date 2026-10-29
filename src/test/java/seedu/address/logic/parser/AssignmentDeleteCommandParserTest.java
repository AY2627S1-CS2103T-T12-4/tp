package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AssignmentDeleteCommand;
import seedu.address.model.assignment.AssignmentName;

public class AssignmentDeleteCommandParserTest {
    private final AssignmentDeleteCommandParser parser = new AssignmentDeleteCommandParser();

    @Test
    public void parse_validName_returnsDeleteCommand() {
        assertParseSuccess(parser, " a/ PS1 ", new AssignmentDeleteCommand(NAME_PS1));
        assertParseSuccess(parser, "a/Lab   2 (Graphs)",
                new AssignmentDeleteCommand(new AssignmentName("Lab 2 (Graphs)")));
    }

    @Test
    public void parse_fastDeleteOption_skipsConfirmation() {
        assertParseSuccess(parser, "a/PS1 f/yes", new AssignmentDeleteCommand(NAME_PS1, true));
        assertParseSuccess(parser, "f/YES a/PS1", new AssignmentDeleteCommand(NAME_PS1, true));
        assertParseFailure(parser, "a/PS1 f/no", FastDeleteOption.MESSAGE_INVALID);
        assertParseFailure(parser, "a/PS1 f/yes now",
                String.format(TAssistArgumentParser.MESSAGE_EXTRA_TEXT, "f/yes", "now"));
    }

    @Test
    public void parse_invalidName_reportsConstraints() {
        for (String input : new String[] {"a/", "a/   ", "a/PS_1", "a/" + "a".repeat(61)}) {
            assertParseFailure(parser, input, AssignmentName.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_malformedParameters_reportsSpecificError() {
        assertParseFailure(parser, "", String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, "a/"));
        assertParseFailure(parser, "f/yes", String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, "a/"));
        assertParseFailure(parser, "a/PS1 a/PS2", Messages.getErrorMessageForDuplicatePrefixes(new Prefix("a/")));
        assertParseFailure(parser, "a/PS1 n/PS2",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "n/", "a/ f/"));
        assertParseFailure(parser, "PS1", String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "PS1", "a/ f/"));
    }

    @Test
    public void parse_missingSpaceBeforeName_reportsMissingSpace() {
        assertParseFailure(parser, "f/yesa/PS1",
                String.format(TAssistArgumentParser.MESSAGE_MISSING_SPACE, "a/", "f/yesa/PS1"));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
