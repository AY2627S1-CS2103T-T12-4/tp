package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalStudents.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AttendanceClearCommand;
import seedu.address.model.student.StudentId;

public class AttendanceClearCommandParserTest {
    private final AttendanceClearCommandParser parser = new AttendanceClearCommandParser();

    @Test
    public void parse_validArguments_acceptsAnyOrderAndNormalizesValues() {
        assertParseSuccess(parser, "  w/ 03   id/ a0123456x ", new AttendanceClearCommand(ALICE.getStudentId(), 3));
        assertParseSuccess(parser, "id/A0123456X w/1", new AttendanceClearCommand(ALICE.getStudentId(), 1));
        assertParseSuccess(parser, "id/A0123456X w/13", new AttendanceClearCommand(ALICE.getStudentId(), 13));
    }

    @Test
    public void parse_missingParameters_reportsMissingPrefixes() {
        String[][] cases = {{"", "id/ w/"}, {"w/3", "id/"}, {"id/A0123456X", "w/"}};
        for (String[] testCase : cases) {
            assertParseFailure(parser, testCase[0],
                    String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, testCase[1]));
        }
    }

    @Test
    public void parse_invalidValues_reportsValueConstraints() {
        for (String week : new String[] {"", "0", "14", "-1", "1.5", "three", "9".repeat(1000)}) {
            assertParseFailure(parser, "id/A0123456X w/" + week, AttendanceCommandParserUtil.MESSAGE_INVALID_WEEK);
        }
        for (String id : new String[] {"", "ABC", "A1!", "1".repeat(21)}) {
            assertParseFailure(parser, "id/" + id + " w/3", StudentId.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedOrUnknownPrefixes_reportsFormatError() {
        String validArguments = "id/A0123456X w/3";
        for (String prefix : new String[] {"id/", "w/"}) {
            assertParseFailure(parser, validArguments + " " + prefix + "value",
                    Messages.getErrorMessageForDuplicatePrefixes(new Prefix(prefix)));
        }
        // A status copied from attendance mark is not accepted, so a typo never clears the wrong record.
        assertParseFailure(parser, validArguments + " s/present",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "s/", "id/ w/"));
        assertParseFailure(parser, "extra " + validArguments,
                String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "extra", "id/ w/"));
    }
}
