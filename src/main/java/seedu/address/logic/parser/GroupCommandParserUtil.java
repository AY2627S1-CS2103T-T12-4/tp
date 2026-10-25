package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;

/**
 * Parses the group-name parameter shared by group creation, switching and deletion.
 */
final class GroupCommandParserUtil {
    static final Prefix PREFIX_GROUP = new Prefix("g/");
    static final Prefix PREFIX_NEW_NAME = new Prefix("n/");

    private GroupCommandParserUtil() {}

    /**
     * Parses exactly one group name, reporting the command's usage when it is missing or blank.
     */
    static GroupName parseName(String arguments, String usage) throws ParseException {
        // Declare g/ optional here so missing and empty values get the same feature-specific message.
        TAssistArgumentParser parser = new TAssistArgumentParser(List.of(), List.of(PREFIX_GROUP));
        ArgumentMultimap values = parser.parse(arguments);
        String name = values.getValue(PREFIX_GROUP).orElse("");
        if (name.isBlank()) {
            throw new ParseException("Missing group name. Usage: " + usage);
        }
        return TAssistParserUtil.parseGroupName(name);
    }
}
