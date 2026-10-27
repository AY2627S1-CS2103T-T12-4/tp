package seedu.address.logic.parser;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the {@code f/yes} option that lets a deletion command skip its confirmation prompt.
 */
final class FastDeleteOption {
    static final Prefix PREFIX_FAST = new Prefix("f/");
    static final String MESSAGE_INVALID = "Invalid fast-delete option. Use f/yes to skip confirmation.";

    private static final String FAST_DELETE_VALUE = "yes";

    private FastDeleteOption() {}

    /**
     * Returns true if {@code values} has {@code f/yes}, and false if it has no {@code f/}.
     *
     * @throws ParseException if {@code f/} has any value other than {@code yes}, ignoring case.
     */
    static boolean isPresent(ArgumentMultimap values) throws ParseException {
        if (values.getValue(PREFIX_FAST).isEmpty()) {
            return false;
        }
        if (!FAST_DELETE_VALUE.equalsIgnoreCase(values.getValue(PREFIX_FAST).get())) {
            throw new ParseException(MESSAGE_INVALID);
        }
        return true;
    }
}
