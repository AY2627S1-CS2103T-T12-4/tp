package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Remark;

/** Tests parsing of a {@code remark} command. */
public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validRemark_returnsCommand() throws Exception {
        assertEquals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes baseball")),
                parser.parse("1 r/Likes baseball"));
    }

    @Test
    public void parse_missingRemark_clearsRemark() throws Exception {
        assertEquals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")), parser.parse("1"));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse("zero r/Likes baseball"));
    }
}
