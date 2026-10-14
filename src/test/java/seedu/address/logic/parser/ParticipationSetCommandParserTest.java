package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ParticipationSetCommand;
import seedu.address.model.student.StudentId;

public class ParticipationSetCommandParserTest {
    private final ParticipationSetCommandParser parser = new ParticipationSetCommandParser();

    @Test
    public void parse_validValues_acceptsEitherOrderCaseAndBoundaries() {
        assertParseSuccess(parser, " id/a0123456x w/03 s/02 ",
                new ParticipationSetCommand(ALICE.getStudentId(), 3, 2));
        assertParseSuccess(parser, "s/0 w/1 id/A0123456X", new ParticipationSetCommand(ALICE.getStudentId(), 1, 0));
        assertParseSuccess(parser, "w/13 id/A0123456X s/5", new ParticipationSetCommand(ALICE.getStudentId(), 13, 5));
    }

    @Test
    public void parse_invalidValues_reportsFieldSpecificError() {
        for (String week : new String[] {"", "0", "14", "-1", "+1", "1.5", "three", "9".repeat(1000)}) {
            assertParseFailure(parser, "id/A0123456X w/" + week + " s/2", TAssistParserUtil.MESSAGE_WEEK);
        }
        for (String score : new String[] {"", "-1", "6", "+1", "2.5", "two", "9".repeat(1000)}) {
            assertParseFailure(parser, "id/A0123456X w/3 s/" + score, TAssistParserUtil.MESSAGE_PARTICIPATION_SCORE);
        }
        for (String id : new String[] {"", "ABC", "A 1", "A1!", "1".repeat(21)}) {
            assertParseFailure(parser, "id/" + id + " w/3 s/2", StudentId.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_missingParameters_identifiesMissingPrefixes() {
        String[][] cases = {{"", "id/ w/ s/"}, {"w/3 s/2", "id/"}, {"id/A1 s/2", "w/"}, {"id/A1 w/3", "s/"}};
        for (String[] testCase : cases) {
            assertParseFailure(parser, testCase[0],
                    String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, testCase[1]));
        }
    }

    @Test
    public void parse_malformedParameters_reportsSpecificError() {
        for (String parameter : new String[] {"id/A1", "w/3", "s/2"}) {
            Prefix prefix = new Prefix(parameter.substring(0, parameter.indexOf('/') + 1));
            assertParseFailure(parser, "id/A0123456X w/3 s/2 " + parameter,
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
        assertParseFailure(parser, "id/A1 w/3 s/2 x/extra",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "x/", "id/ w/ s/"));
        assertParseFailure(parser, "Alice id/A1 w/3 s/2",
                String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "Alice", "id/ w/ s/"));
        assertParseFailure(parser, "id/A1 w/3s/2",
                String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS, "s/"));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
