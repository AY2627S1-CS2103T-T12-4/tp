package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.StudentAddCommand;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.testutil.StudentBuilder;

public class StudentAddCommandParserTest {
    private final StudentAddCommandParser parser = new StudentAddCommandParser();

    @Test
    public void parse_validInput_normalizesValuesInEitherOrder() {
        assertParseSuccess(parser, " n/ Alice   Tan id/a0123456x ", new StudentAddCommand(ALICE));
        assertParseSuccess(parser, "id/A0123456X n/Alice Tan", new StudentAddCommand(ALICE));
        assertParseSuccess(parser, "n/Élodie O'Neil id/1", new StudentAddCommand(new StudentBuilder()
                .withName("Élodie O'Neil").withStudentId("1").build()));
        assertParseSuccess(parser, "n/" + "A".repeat(60) + " id/" + "1".repeat(20),
                new StudentAddCommand(new StudentBuilder().withName("A".repeat(60))
                        .withStudentId("1".repeat(20)).build()));
    }

    @Test
    public void parse_missingParameter_reportsUsage() {
        for (String input : new String[] {"", "n/Alice Tan", "id/A0123456X", "n/Aliceid/A0123456X"}) {
            assertParseFailure(parser, input, "Missing required parameter. Usage: " + StudentAddCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_missingNameBeforeExtraText_reportsMissingParameter() {
        assertParseFailure(parser, "id/A0123456X Alice Tan",
                "Missing required parameter. Usage: " + StudentAddCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidValues_reportsFieldConstraints() {
        for (String name : new String[] {"", "123", "--", "A".repeat(61), "Alice!"}) {
            assertParseFailure(parser, "n/" + name + " id/A0123456X", StudentName.MESSAGE_CONSTRAINTS);
        }
        for (String id : new String[] {"", "ABC", "A1!", "1".repeat(21), "A1n/Alice"}) {
            assertParseFailure(parser, "n/Alice id/" + id, StudentId.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_malformedParameters_reportsSpecificError() {
        assertParseFailure(parser, "n/Alice Tanid/A0123456X",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "Tanid/", "n/ id/"));
        assertParseFailure(parser, "n/Alice n/Ben id/A1",
                Messages.getErrorMessageForDuplicatePrefixes(new Prefix("n/")));
        assertParseFailure(parser, "n/Alice id/A1 id/A2",
                Messages.getErrorMessageForDuplicatePrefixes(new Prefix("id/")));
        assertParseFailure(parser, "n/Alice id/A1 x/extra",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "x/", "n/ id/"));
        assertParseFailure(parser, "Alice id/A1",
                String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "Alice", "n/ id/"));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
