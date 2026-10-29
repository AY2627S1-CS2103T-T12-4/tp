package seedu.address.logic.parser;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Checks the required parameters of student commands before validating their values.
 * Every value except the name, such as an ID or a sort field, is a single word.
 */
final class StudentCommandParserUtil {
    static final Prefix PREFIX_NAME = new Prefix("n/");
    static final Prefix PREFIX_ID = new Prefix("id/");

    private StudentCommandParserUtil() {}

    /**
     * Parses the allowed prefixes and reports missing parameters with the command's usage.
     */
    static ArgumentMultimap parse(String arguments, String usage, Prefix... prefixes) throws ParseException {
        return parse(arguments, usage, List.of(), prefixes);
    }

    /**
     * Parses the allowed prefixes, keeping {@code literalWords} such as {@code s/o} in values, and reports missing
     * parameters with the command's usage.
     */
    static ArgumentMultimap parse(String arguments, String usage, Collection<String> literalWords, Prefix... prefixes)
            throws ParseException {
        // Check presence here so the error includes this command's complete format.
        List<Prefix> singleWordPrefixes = Stream.of(prefixes)
                .filter(prefix -> !prefix.equals(PREFIX_NAME))
                .toList();
        TAssistArgumentParser parser = new TAssistArgumentParser(List.of(), List.of(prefixes), singleWordPrefixes,
                literalWords);
        ArgumentMultimap values = parser.parseWithoutExtraTextCheck(arguments);
        for (Prefix prefix : prefixes) {
            if (values.getValue(prefix).isEmpty()) {
                throw new ParseException("Missing required parameter. Usage: " + usage);
            }
        }
        parser.verifyNoExtraText(values);
        return values;
    }
}
