package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.AssignmentGradeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the assignment, student, and whole-number grade for a grade update.
 */
public class AssignmentGradeCommandParser implements Parser<AssignmentGradeCommand> {
    private static final Prefix PREFIX_ASSIGNMENT = new Prefix("a/");
    private static final Prefix PREFIX_STUDENT_ID = new Prefix("id/");
    private static final Prefix PREFIX_GRADE = new Prefix("g/");

    private final TAssistArgumentParser argumentParser = new TAssistArgumentParser(
            List.of(PREFIX_ASSIGNMENT, PREFIX_STUDENT_ID, PREFIX_GRADE), List.of());

    @Override
    public AssignmentGradeCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = argumentParser.parse(arguments);
        return new AssignmentGradeCommand(
                TAssistParserUtil.parseAssignmentName(values.getValue(PREFIX_ASSIGNMENT).orElseThrow()),
                TAssistParserUtil.parseStudentId(values.getValue(PREFIX_STUDENT_ID).orElseThrow()),
                TAssistParserUtil.parseGrade(values.getValue(PREFIX_GRADE).orElseThrow()));
    }
}
