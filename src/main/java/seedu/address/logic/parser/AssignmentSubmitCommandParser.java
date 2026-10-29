package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.AssignmentSubmitCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the assignment, one or more students, and a yes-or-no status for a submission update.
 */
public class AssignmentSubmitCommandParser implements Parser<AssignmentSubmitCommand> {
    private static final Prefix PREFIX_ASSIGNMENT = new Prefix("a/");
    private static final Prefix PREFIX_STUDENT_ID = new Prefix("id/");
    private static final Prefix PREFIX_STATUS = new Prefix("s/");

    private final TAssistArgumentParser argumentParser = new TAssistArgumentParser(
            List.of(PREFIX_ASSIGNMENT, PREFIX_STUDENT_ID, PREFIX_STATUS), List.of(),
            List.of(PREFIX_STUDENT_ID, PREFIX_STATUS)).allowRepeated(PREFIX_STUDENT_ID);

    @Override
    public AssignmentSubmitCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = argumentParser.parse(arguments, AssignmentSubmitCommand.MESSAGE_USAGE);
        return new AssignmentSubmitCommand(
                TAssistParserUtil.parseAssignmentName(values.getValue(PREFIX_ASSIGNMENT).orElseThrow()),
                TAssistParserUtil.parseStudentIds(values.getAllValues(PREFIX_STUDENT_ID)),
                TAssistParserUtil.parseSubmissionStatus(values.getValue(PREFIX_STATUS).orElseThrow()));
    }
}
