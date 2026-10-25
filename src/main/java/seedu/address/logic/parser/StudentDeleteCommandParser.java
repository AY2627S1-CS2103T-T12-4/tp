package seedu.address.logic.parser;

import static seedu.address.logic.parser.StudentCommandParserUtil.PREFIX_ID;

import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.StudentDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a student ID and optional fast-delete choice into a deletion command.
 */
public class StudentDeleteCommandParser implements Parser<StudentDeleteCommand> {
    public static final String MESSAGE_INVALID_FAST_DELETE_OPTION =
            "Invalid fast-delete option. Use f/yes to skip confirmation.";

    private static final Prefix PREFIX_FAST = new Prefix("f/");
    private static final String FAST_DELETE_VALUE = "yes";

    @Override
    public StudentDeleteCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = new TAssistArgumentParser(List.of(), List.of(PREFIX_ID, PREFIX_FAST))
                .parse(arguments);
        if (values.getValue(PREFIX_ID).isEmpty()) {
            throw new ParseException("Missing required parameter. Usage: " + StudentDeleteCommand.MESSAGE_USAGE);
        }
        Optional<String> fastDeleteValue = values.getValue(PREFIX_FAST);
        if (fastDeleteValue.isPresent() && !FAST_DELETE_VALUE.equalsIgnoreCase(fastDeleteValue.get())) {
            throw new ParseException(MESSAGE_INVALID_FAST_DELETE_OPTION);
        }
        return new StudentDeleteCommand(TAssistParserUtil.parseStudentId(values.getValue(PREFIX_ID).orElseThrow()),
                fastDeleteValue.isPresent());
    }
}
