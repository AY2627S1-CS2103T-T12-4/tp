package seedu.address.logic.parser;

import java.util.List;
import java.util.Locale;

import seedu.address.logic.commands.StudentSortCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudentSortField;

/**
 * Parses a required name or ID sort field.
 */
public class StudentSortCommandParser implements Parser<StudentSortCommand> {
    public static final String MESSAGE_INVALID_FIELD = "Invalid sort field. Use by/name or by/id.";
    private static final Prefix PREFIX_BY = new Prefix("by/");

    @Override
    public StudentSortCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = new TAssistArgumentParser(List.of(PREFIX_BY), List.of(), List.of(PREFIX_BY))
                .parse(arguments);
        String field = values.getValue(PREFIX_BY).orElseThrow().toLowerCase(Locale.ROOT);
        return switch (field) {
            case "name" -> new StudentSortCommand(StudentSortField.NAME);
            case "id" -> new StudentSortCommand(StudentSortField.ID);
            default -> throw new ParseException(MESSAGE_INVALID_FIELD);
        };
    }
}
