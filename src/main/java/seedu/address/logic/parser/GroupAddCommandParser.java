package seedu.address.logic.parser;

import seedu.address.logic.commands.GroupAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses group creation arguments into a command.
 */
public class GroupAddCommandParser implements Parser<GroupAddCommand> {
    @Override
    public GroupAddCommand parse(String arguments) throws ParseException {
        return new GroupAddCommand(GroupCommandParserUtil.parseName(arguments, GroupAddCommand.MESSAGE_USAGE));
    }
}
