package seedu.address.logic.parser;

import static seedu.address.logic.parser.FastDeleteOption.PREFIX_FAST;

import java.util.List;

import seedu.address.logic.commands.GroupDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;

/**
 * Parses a group name and optional fast-delete choice into a deletion command.
 */
public class GroupDeleteCommandParser implements Parser<GroupDeleteCommand> {
    public static final String MESSAGE_INVALID_FAST_DELETE_OPTION = FastDeleteOption.MESSAGE_INVALID;

    @Override
    public GroupDeleteCommand parse(String arguments) throws ParseException {
        TAssistArgumentParser parser = GroupCommandParserUtil.createParser(List.of(PREFIX_FAST), PREFIX_FAST);
        ArgumentMultimap values = parser.parseWithoutExtraTextCheck(arguments);
        GroupCommandParserUtil.verifyNoMissingSpace(parser, values, GroupCommandParserUtil.PREFIX_GROUP,
                PREFIX_FAST);
        GroupName name = GroupCommandParserUtil.parseNameValue(values, GroupCommandParserUtil.PREFIX_GROUP,
                "Missing group name.", GroupDeleteCommand.MESSAGE_USAGE);
        parser.verifyNoExtraText(values);
        return new GroupDeleteCommand(name, FastDeleteOption.isPresent(values));
    }
}
