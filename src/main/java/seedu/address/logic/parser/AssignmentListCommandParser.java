package seedu.address.logic.parser;

import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.AssignmentListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an optional assignment name into a command that lists the assignment's records, or the group's assignments
 * when no name is given.
 */
public class AssignmentListCommandParser implements Parser<AssignmentListCommand> {
    private static final Prefix PREFIX_ASSIGNMENT = new Prefix("a/");

    @Override
    public AssignmentListCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = new TAssistArgumentParser(List.of(), List.of(PREFIX_ASSIGNMENT)).parse(arguments);
        Optional<String> name = values.getValue(PREFIX_ASSIGNMENT);
        if (name.isEmpty()) {
            return new AssignmentListCommand();
        }
        return new AssignmentListCommand(TAssistParserUtil.parseAssignmentName(name.get()));
    }
}
