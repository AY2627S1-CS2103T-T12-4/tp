package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.AssignmentAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an assignment name into an assignment creation command.
 */
public class AssignmentAddCommandParser implements Parser<AssignmentAddCommand> {
    private static final Prefix PREFIX_ASSIGNMENT = new Prefix("a/");

    @Override
    public AssignmentAddCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = new TAssistArgumentParser(List.of(PREFIX_ASSIGNMENT), List.of()).parse(arguments);
        return new AssignmentAddCommand(
                TAssistParserUtil.parseAssignmentName(values.getValue(PREFIX_ASSIGNMENT).orElseThrow()));
    }
}
