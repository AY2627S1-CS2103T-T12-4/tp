package seedu.address.logic.parser;

import java.util.ArrayList;
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
        return parseNameValue(parseValues(arguments), PREFIX_GROUP, "Missing group name.", usage);
    }

    /**
     * Parses {@code arguments} with {@code g/} and the given extra prefixes, each allowed at most once.
     */
    static ArgumentMultimap parseValues(String arguments, Prefix... extraPrefixes) throws ParseException {
        return parseValues(arguments, List.of(), extraPrefixes);
    }

    /**
     * Parses {@code arguments} as {@link #parseValues(String, Prefix...)} does, and also rejects spaces in the
     * values of {@code singleWordPrefixes}, which must be among the extra prefixes.
     */
    static ArgumentMultimap parseValues(String arguments, List<Prefix> singleWordPrefixes, Prefix... extraPrefixes)
            throws ParseException {
        // Declare g/ optional here so missing and empty values get the same feature-specific message.
        List<Prefix> prefixes = new ArrayList<>(List.of(PREFIX_GROUP));
        prefixes.addAll(List.of(extraPrefixes));
        return new TAssistArgumentParser(List.of(), prefixes, singleWordPrefixes).parse(arguments);
    }

    /**
     * Parses the group name given by {@code prefix}, reporting {@code missingMessage} and the usage when it is
     * missing or blank.
     */
    static GroupName parseNameValue(ArgumentMultimap values, Prefix prefix, String missingMessage, String usage)
            throws ParseException {
        String name = values.getValue(prefix).orElse("");
        if (name.isBlank()) {
            throw new ParseException(missingMessage + " Usage: " + usage);
        }
        return TAssistParserUtil.parseGroupName(name);
    }
}
