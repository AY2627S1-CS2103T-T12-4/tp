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
     * Parses {@code arguments} with {@code g/} and the given extra prefixes, each allowed at most once. The command
     * needs all of them, so one that is missing is reported if it was typed right after another value.
     */
    static ArgumentMultimap parseValues(String arguments, Prefix... extraPrefixes) throws ParseException {
        TAssistArgumentParser parser = createParser(List.of(), extraPrefixes);
        ArgumentMultimap values = parser.parse(arguments);
        verifyNoMissingSpace(parser, values, PREFIX_GROUP);
        verifyNoMissingSpace(parser, values, extraPrefixes);
        return values;
    }

    /**
     * Checks that each of {@code prefixes} that has no value in {@code values} was not typed right after another
     * value without a space.
     *
     * @throws ParseException if a missing prefix is joined to another value.
     */
    static void verifyNoMissingSpace(TAssistArgumentParser parser, ArgumentMultimap values, Prefix... prefixes)
            throws ParseException {
        for (Prefix prefix : prefixes) {
            if (values.getValue(prefix).isEmpty()) {
                parser.verifyNoMissingSpaceBefore(values, prefix);
            }
        }
    }

    /**
     * Creates a parser for {@code g/} and the given extra prefixes, each allowed at most once, that also rejects
     * spaces in the values of {@code singleWordPrefixes}, which must be among the extra prefixes.
     */
    static TAssistArgumentParser createParser(List<Prefix> singleWordPrefixes, Prefix... extraPrefixes) {
        // Declare g/ optional here so missing and empty values get the same feature-specific message.
        List<Prefix> prefixes = new ArrayList<>(List.of(PREFIX_GROUP));
        prefixes.addAll(List.of(extraPrefixes));
        return new TAssistArgumentParser(List.of(), prefixes, singleWordPrefixes);
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
