package seedu.address.logic.parser;

import static seedu.address.logic.parser.StudentCommandParserUtil.PREFIX_ID;
import static seedu.address.logic.parser.StudentCommandParserUtil.PREFIX_NAME;

import seedu.address.logic.commands.StudentAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;

/**
 * Parses a student's name and ID into an add command.
 * Name particles such as {@code s/o} are part of the name, not prefixes.
 */
public class StudentAddCommandParser implements Parser<StudentAddCommand> {
    @Override
    public StudentAddCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = StudentCommandParserUtil.parse(arguments, StudentAddCommand.MESSAGE_USAGE,
                StudentName.SLASH_PARTICLES, PREFIX_NAME, PREFIX_ID);
        return new StudentAddCommand(new Student(
                TAssistParserUtil.parseStudentName(values.getValue(PREFIX_NAME).orElseThrow()),
                TAssistParserUtil.parseStudentId(values.getValue(PREFIX_ID).orElseThrow())));
    }
}
