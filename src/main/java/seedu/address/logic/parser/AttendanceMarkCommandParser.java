package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.AttendanceMarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the student, week, and status for an attendance mark.
 */
public class AttendanceMarkCommandParser implements Parser<AttendanceMarkCommand> {
    private static final Prefix PREFIX_STUDENT_ID = new Prefix("id/");
    private static final Prefix PREFIX_WEEK = new Prefix("w/");
    private static final Prefix PREFIX_STATUS = new Prefix("s/");

    private final TAssistArgumentParser argumentParser = new TAssistArgumentParser(
            List.of(PREFIX_STUDENT_ID, PREFIX_WEEK, PREFIX_STATUS), List.of());

    @Override
    public AttendanceMarkCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = argumentParser.parse(arguments);
        return new AttendanceMarkCommand(
                TAssistParserUtil.parseStudentId(values.getValue(PREFIX_STUDENT_ID).orElseThrow()),
                AttendanceCommandParserUtil.parseWeek(values.getValue(PREFIX_WEEK).orElseThrow()),
                AttendanceCommandParserUtil.parseStatus(values.getValue(PREFIX_STATUS).orElseThrow()));
    }
}
