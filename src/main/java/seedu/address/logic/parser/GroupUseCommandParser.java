package seedu.address.logic.parser;

import seedu.address.logic.commands.GroupUseCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses active-group switching arguments into a command.
 */
public class GroupUseCommandParser implements Parser<GroupUseCommand> {
    @Override
    public GroupUseCommand parse(String arguments) throws ParseException {
        return new GroupUseCommand(GroupCommandParserUtil.parseName(arguments, GroupUseCommand.MESSAGE_USAGE));
    }
}
