package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.HelpCommand.MESSAGE_UNKNOWN_TOPIC;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.help.HelpEntry;
import seedu.address.testutil.HelpEntryBuilder;

public class HelpCommandParserTest {
    private final List<HelpEntry> entries = List.of(new HelpEntryBuilder().build());
    private final HelpCommandParser parser = new HelpCommandParser(entries);

    @Test
    public void parse_noTopic_returnsHelpCommandForEveryEntry() {
        assertParseSuccess(parser, "", new HelpCommand(entries));
        assertParseSuccess(parser, "   \t ", new HelpCommand(entries));
    }

    @Test
    public void parse_validTopic_returnsHelpCommandForTopic() {
        assertParseSuccess(parser, " group", new HelpCommand(entries, "group"));
        assertParseSuccess(parser, "  attendance  ", new HelpCommand(entries, "attendance"));
        assertParseSuccess(parser, " ASSIGNMENT", new HelpCommand(entries, "assignment"));
    }

    @Test
    public void parse_unknownTopic_throwsParseException() {
        assertParseFailure(parser, " 123", MESSAGE_UNKNOWN_TOPIC);
        assertParseFailure(parser, " xyz", MESSAGE_UNKNOWN_TOPIC);
        assertParseFailure(parser, " assignments", MESSAGE_UNKNOWN_TOPIC);
        assertParseFailure(parser, " group add", MESSAGE_UNKNOWN_TOPIC);
    }

    @Test
    public void parse_unknownTopic_messageMatchesSpecification() {
        assertEquals("Unknown help topic. Available topics: group, student, attendance, participation, "
                + "assignment.", MESSAGE_UNKNOWN_TOPIC);
    }

    @Test
    public void constructorAndParse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HelpCommandParser(null));
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
