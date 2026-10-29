package seedu.address.logic.parser;

import java.util.Collection;
import java.util.List;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudentId;

/**
 * Checks the required parameters of student commands before validating their values.
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
        List<Prefix> singleWordPrefixes = List.of(prefixes).contains(PREFIX_ID) ? List.of(PREFIX_ID) : List.of();
        TAssistArgumentParser parser = new TAssistArgumentParser(List.of(), List.of(prefixes), singleWordPrefixes,
                literalWords);
        ArgumentMultimap values = parser.parseWithoutExtraTextCheck(arguments);
        for (Prefix prefix : prefixes) {
            if (values.getValue(prefix).isEmpty()) {
                if (!PREFIX_ID.equals(prefix) || hasJoinedStudentId(values)) {
                    parser.verifyNoMissingSpaceBefore(values, prefix);
                }
                throw new ParseException("Missing required parameter. Usage: " + usage);
            }
        }
        parser.verifyNoExtraText(values);
        return values;
    }

    /**
     * Returns true if the name's first word hides an id/ prefix followed by a valid student ID.
     * A name fragment such as David/Tan has no valid ID after id/, so it needs the missing-parameter error.
     */
    private static boolean hasJoinedStudentId(ArgumentMultimap values) {
        String firstWord = values.getValue(PREFIX_NAME).orElse("").split("\\s+", 2)[0];
        int prefixStart = firstWord.lastIndexOf(PREFIX_ID.getPrefix());
        return prefixStart > 0
                && StudentId.isValidStudentId(firstWord.substring(prefixStart + PREFIX_ID.getPrefix().length()));
    }
}
