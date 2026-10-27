package seedu.address.logic.parser;

import static seedu.address.logic.parser.GroupCommandParserUtil.PREFIX_GROUP;
import static seedu.address.logic.parser.GroupCommandParserUtil.PREFIX_NEW_NAME;

import seedu.address.logic.commands.GroupRenameCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;

/**
 * Parses group renaming arguments into a command.
 */
public class GroupRenameCommandParser implements Parser<GroupRenameCommand> {
    @Override
    public GroupRenameCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = GroupCommandParserUtil.parseValues(arguments, PREFIX_NEW_NAME);
        GroupName name = GroupCommandParserUtil.parseNameValue(values, PREFIX_GROUP, "Missing group name.",
                GroupRenameCommand.MESSAGE_USAGE);
        GroupName newName = GroupCommandParserUtil.parseNameValue(values, PREFIX_NEW_NAME, "Missing new group name.",
                GroupRenameCommand.MESSAGE_USAGE);
        return new GroupRenameCommand(name, newName);
    }
}
