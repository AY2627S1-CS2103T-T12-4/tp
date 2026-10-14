package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.AttendanceListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the teaching week for an attendance listing.
 */
public class AttendanceListCommandParser implements Parser<AttendanceListCommand> {
    private static final Prefix PREFIX_WEEK = new Prefix("w/");

    private final TAssistArgumentParser argumentParser =
            new TAssistArgumentParser(List.of(PREFIX_WEEK), List.of());

    @Override
    public AttendanceListCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = argumentParser.parse(arguments);
        return new AttendanceListCommand(
                AttendanceCommandParserUtil.parseWeek(values.getValue(PREFIX_WEEK).orElseThrow()));
    }
}
