package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.AttendanceClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the student and week whose attendance mark is to be cleared.
 */
public class AttendanceClearCommandParser implements Parser<AttendanceClearCommand> {
    private static final Prefix PREFIX_STUDENT_ID = new Prefix("id/");
    private static final Prefix PREFIX_WEEK = new Prefix("w/");

    private final TAssistArgumentParser argumentParser = new TAssistArgumentParser(
            List.of(PREFIX_STUDENT_ID, PREFIX_WEEK), List.of());

    @Override
    public AttendanceClearCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = argumentParser.parse(arguments);
        return new AttendanceClearCommand(
                TAssistParserUtil.parseStudentId(values.getValue(PREFIX_STUDENT_ID).orElseThrow()),
                AttendanceCommandParserUtil.parseWeek(values.getValue(PREFIX_WEEK).orElseThrow()));
    }
}
