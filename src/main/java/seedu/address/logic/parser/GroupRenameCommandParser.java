package seedu.address.logic.parser;

import static seedu.address.logic.parser.GroupCommandParserUtil.PREFIX_GROUP;
import static seedu.address.logic.parser.GroupCommandParserUtil.PREFIX_NEW_NAME;

import java.util.List;

import seedu.address.logic.commands.GroupRenameCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses group renaming arguments into a command.
 */
public class GroupRenameCommandParser implements Parser<GroupRenameCommand> {
    @Override
    public GroupRenameCommand parse(String arguments) throws ParseException {
        // Declare both prefixes optional here so a missing one gets the command's own usage message.
        ArgumentMultimap values = new TAssistArgumentParser(List.of(), List.of(PREFIX_GROUP, PREFIX_NEW_NAME))
                .parse(arguments);
        String name = values.getValue(PREFIX_GROUP).orElse("");
        String newName = values.getValue(PREFIX_NEW_NAME).orElse("");
        if (name.isBlank() || newName.isBlank()) {
            throw new ParseException("Missing group name or new name. Usage: " + GroupRenameCommand.MESSAGE_USAGE);
        }
        return new GroupRenameCommand(TAssistParserUtil.parseGroupName(name),
                TAssistParserUtil.parseGroupName(newName));
    }
}
