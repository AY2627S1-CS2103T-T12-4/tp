package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.GroupListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a group listing command, rejecting any parameters.
 */
public class GroupListCommandParser implements Parser<GroupListCommand> {
    @Override
    public GroupListCommand parse(String arguments) throws ParseException {
        new TAssistArgumentParser(List.of(), List.of()).parse(arguments);
        return new GroupListCommand();
    }
}
