package seedu.address.logic.parser;

import seedu.address.logic.commands.GroupDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses group deletion arguments into a command.
 */
public class GroupDeleteCommandParser implements Parser<GroupDeleteCommand> {
    @Override
    public GroupDeleteCommand parse(String arguments) throws ParseException {
        return new GroupDeleteCommand(GroupCommandParserUtil.parseName(arguments, GroupDeleteCommand.MESSAGE_USAGE));
    }
}
