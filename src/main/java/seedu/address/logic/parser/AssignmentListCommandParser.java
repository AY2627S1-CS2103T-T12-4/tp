package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.AssignmentListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an assignment name into a command that lists the assignment's records.
 */
public class AssignmentListCommandParser implements Parser<AssignmentListCommand> {
    private static final Prefix PREFIX_ASSIGNMENT = new Prefix("a/");

    @Override
    public AssignmentListCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = new TAssistArgumentParser(List.of(PREFIX_ASSIGNMENT), List.of()).parse(arguments);
        return new AssignmentListCommand(
                TAssistParserUtil.parseAssignmentName(values.getValue(PREFIX_ASSIGNMENT).orElseThrow()));
    }
}
