package seedu.address.logic.parser;

import static seedu.address.logic.parser.StudentCommandParserUtil.PREFIX_ID;

import seedu.address.logic.commands.StudentDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a student ID into a deletion command.
 */
public class StudentDeleteCommandParser implements Parser<StudentDeleteCommand> {
    @Override
    public StudentDeleteCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = StudentCommandParserUtil.parse(arguments,
                StudentDeleteCommand.MESSAGE_USAGE, PREFIX_ID);
        return new StudentDeleteCommand(TAssistParserUtil.parseStudentId(values.getValue(PREFIX_ID).orElseThrow()));
    }
}
