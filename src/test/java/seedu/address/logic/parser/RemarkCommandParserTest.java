package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_invalidPreamble_failure() {
        assertParseFailure(parser, "-1 r/Likes to swim", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0 r/Likes to swim", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 extra text r/Likes to swim", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_validRemark_success() {
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes to swim"));
        assertParseSuccess(parser, "1 r/Likes to swim", expectedCommand);
    }

    @Test
    public void parse_emptyRemark_success() {
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        assertParseSuccess(parser, "1 r/", expectedCommand);
    }

    @Test
    public void parse_duplicateRemarkPrefix_failure() {
        assertParseFailure(parser, "1 r/First r/Second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }
}
