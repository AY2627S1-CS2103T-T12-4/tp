package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;
import static seedu.address.testutil.TypicalStudents.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AssignmentGradeCommand;

public class AssignmentGradeCommandParserTest {
    private final AssignmentGradeCommandParser parser = new AssignmentGradeCommandParser();

    @Test
    public void parse_validGrades_success() {
        assertParseSuccess(parser, "a/PS1 id/A0123456X g/0",
                new AssignmentGradeCommand(NAME_PS1, ALICE.getStudentId(), 0));
        assertParseSuccess(parser, "g/ 100 id/a0123456x a/ps1",
                new AssignmentGradeCommand(NAME_PS1, ALICE.getStudentId(), 100));
    }

    @Test
    public void parse_invalidGrades_reportsRequiredMessage() {
        for (String grade : new String[] {"-1", "101", "1.5", "one", "9".repeat(100)}) {
            assertParseFailure(parser, "a/PS1 id/A0123456X g/" + grade, TAssistParserUtil.MESSAGE_GRADE);
        }
    }

    @Test
    public void parse_missingDuplicateAndUnknownParameters_reportErrors() {
        assertParseFailure(parser, "a/PS1 id/A0123456X",
                String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, "g/"));
        assertParseFailure(parser, "a/PS1 id/A0123456X g/80 g/90",
                "Multiple values specified for the following single-valued field(s): g/");
        assertParseFailure(parser, "a/PS1 id/A0123456X g/80 s/yes",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "s/", "a/ id/ g/"));
    }
}
