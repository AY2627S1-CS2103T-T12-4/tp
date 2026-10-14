package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.StudentListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a student listing command, rejecting extra input.
 */
public class StudentListCommandParser implements Parser<StudentListCommand> {
    @Override
    public StudentListCommand parse(String arguments) throws ParseException {
        new TAssistArgumentParser(List.of(), List.of()).parse(arguments);
        return new StudentListCommand();
    }
}
