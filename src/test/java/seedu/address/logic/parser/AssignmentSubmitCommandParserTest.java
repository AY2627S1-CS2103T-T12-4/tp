package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;
import static seedu.address.testutil.TypicalStudents.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AssignmentSubmitCommand;

public class AssignmentSubmitCommandParserTest {
    private final AssignmentSubmitCommandParser parser = new AssignmentSubmitCommandParser();

    @Test
    public void parse_validStatus_success() {
        assertParseSuccess(parser, "a/PS1 id/A0123456X s/yes",
                new AssignmentSubmitCommand(NAME_PS1, ALICE.getStudentId(), true));
        assertParseSuccess(parser, "s/NO id/a0123456x a/ ps1 ",
                new AssignmentSubmitCommand(NAME_PS1, ALICE.getStudentId(), false));
    }

    @Test
    public void parse_invalidStatus_reportsFeatureError() {
        assertParseFailure(parser, "a/PS1 id/A0123456X s/maybe", TAssistParserUtil.MESSAGE_SUBMISSION_STATUS);
    }

    @Test
    public void parse_missingDuplicateAndUnknownParameters_reportErrors() {
        assertParseFailure(parser, "a/PS1 id/A0123456X",
                String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, "s/"));
        assertParseFailure(parser, "a/PS1 id/A0123456X s/yes s/no",
                "Repeated parameter(s): s/. Include each only once.");
        assertParseFailure(parser, "a/PS1 id/A0123456X s/yes n/no",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "n/", "a/ id/ s/"));
    }
}
