package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalStudents.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AttendanceMarkCommand;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.StudentId;

public class AttendanceMarkCommandParserTest {
    private final AttendanceMarkCommandParser parser = new AttendanceMarkCommandParser();

    @Test
    public void parse_validArguments_acceptsAnyOrderAndNormalizesValues() {
        assertParseSuccess(parser, " s/ Present   w/ 3 id/ a0123456x ",
                new AttendanceMarkCommand(ALICE.getStudentId(), 3, AttendanceStatus.PRESENT));
        assertParseSuccess(parser, "id/A0123456X w/13 s/absent",
                new AttendanceMarkCommand(ALICE.getStudentId(), 13, AttendanceStatus.ABSENT));
    }

    @Test
    public void parse_missingParameters_reportsMissingPrefixes() {
        String[][] cases = {
            {"", "id/ w/ s/"},
            {"w/3 s/present", "id/"},
            {"id/A0123456X s/present", "w/"},
            {"id/A0123456X w/3", "s/"}
        };
        for (String[] testCase : cases) {
            assertParseFailure(parser, testCase[0],
                    String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, testCase[1]));
        }
    }

    @Test
    public void parse_invalidValues_reportsValueConstraints() {
        assertParseFailure(parser, "id/ w/3 s/present", StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "id/invalid! w/3 s/present", StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "id/A0123456X w/ s/present", AttendanceCommandParserUtil.MESSAGE_INVALID_WEEK);
        assertParseFailure(parser, "id/A0123456X w/14 s/present", AttendanceCommandParserUtil.MESSAGE_INVALID_WEEK);
        assertParseFailure(parser, "id/A0123456X w/3 s/", AttendanceStatus.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "id/A0123456X w/3 s/late", AttendanceStatus.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedOrUnknownPrefixes_reportsFormatError() {
        String validArguments = "id/A0123456X w/3 s/present";
        for (String prefix : new String[] {"id/", "w/", "s/"}) {
            assertParseFailure(parser, validArguments + " " + prefix + "value",
                    Messages.getErrorMessageForDuplicatePrefixes(new Prefix(prefix)));
        }
        assertParseFailure(parser, validArguments + " x/value",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "x/", "id/ w/ s/"));
        assertParseFailure(parser, "extra " + validArguments,
                String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "extra", "id/ w/ s/"));
    }
}
