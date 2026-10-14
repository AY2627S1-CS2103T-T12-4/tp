package seedu.address.logic.parser;

import static seedu.address.logic.parser.StudentCommandParserUtil.PREFIX_ID;
import static seedu.address.logic.parser.StudentCommandParserUtil.PREFIX_NAME;

import seedu.address.logic.commands.StudentAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Student;

/**
 * Parses a student's name and ID into an add command.
 */
public class StudentAddCommandParser implements Parser<StudentAddCommand> {
    @Override
    public StudentAddCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = StudentCommandParserUtil.parse(arguments, StudentAddCommand.MESSAGE_USAGE,
                PREFIX_NAME, PREFIX_ID);
        return new StudentAddCommand(new Student(
                TAssistParserUtil.parseStudentName(values.getValue(PREFIX_NAME).orElseThrow()),
                TAssistParserUtil.parseStudentId(values.getValue(PREFIX_ID).orElseThrow())));
    }
}
