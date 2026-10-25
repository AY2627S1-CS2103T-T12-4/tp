package seedu.address.logic.parser;

import java.util.Optional;

import seedu.address.logic.commands.GroupDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;

/**
 * Parses a group name and optional fast-delete choice into a deletion command.
 */
public class GroupDeleteCommandParser implements Parser<GroupDeleteCommand> {
    public static final String MESSAGE_INVALID_FAST_DELETE_OPTION =
            "Invalid fast-delete option. Use f/yes to skip confirmation.";

    private static final Prefix PREFIX_FAST = new Prefix("f/");
    private static final String FAST_DELETE_VALUE = "yes";

    @Override
    public GroupDeleteCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = GroupCommandParserUtil.parseValues(arguments, PREFIX_FAST);
        GroupName name = GroupCommandParserUtil.parseNameValue(values, GroupCommandParserUtil.PREFIX_GROUP,
                "Missing group name.", GroupDeleteCommand.MESSAGE_USAGE);
        Optional<String> fastDeleteValue = values.getValue(PREFIX_FAST);
        if (fastDeleteValue.isPresent() && !FAST_DELETE_VALUE.equalsIgnoreCase(fastDeleteValue.get())) {
            throw new ParseException(MESSAGE_INVALID_FAST_DELETE_OPTION);
        }
        return new GroupDeleteCommand(name, fastDeleteValue.isPresent());
    }
}
