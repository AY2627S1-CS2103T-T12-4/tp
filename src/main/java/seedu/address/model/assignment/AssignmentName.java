package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Represents the name of an assignment.
 * Guarantees: immutable; is valid as declared in {@link #isValidAssignmentName(String)}.
 * Leading and trailing spaces are removed and repeated internal spaces are treated as one space.
 * Assignment names are compared case-insensitively (e.g. {@code PS1} and {@code ps1} are the same assignment name),
 * but the capitalization entered by the user is kept for display.
 */
public class AssignmentName {

    public static final int MAX_LENGTH = 60;

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid assignment name. Use 1-" + MAX_LENGTH
                    + " letters, digits, spaces, hyphens, periods, colons, or parentheses.";

    /*
     * Only letters, digits, spaces, hyphens, periods, colons and parentheses, with at least one letter or digit,
     * so that names such as "-" or "()" are rejected.
     */
    public static final String VALIDATION_REGEX = "(?=.*[\\p{L}\\p{Nd}])[\\p{L}\\p{Nd} .():-]+";

    public final String fullName;

    /**
     * Constructs an {@code AssignmentName}.
     *
     * @param name A valid assignment name.
     */
    public AssignmentName(String name) {
        requireNonNull(name);
        checkArgument(isValidAssignmentName(name), MESSAGE_CONSTRAINTS);
        fullName = StringUtil.normalizeSpaces(name);
    }

    /**
     * Returns true if a given string is a valid assignment name after its spaces are normalized.
     */
    public static boolean isValidAssignmentName(String test) {
        requireNonNull(test);
        String normalized = StringUtil.normalizeSpaces(test);
        return normalized.length() <= MAX_LENGTH && normalized.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AssignmentName otherAssignmentName)) {
            return false;
        }

        return fullName.equalsIgnoreCase(otherAssignmentName.fullName);
    }

    @Override
    public int hashCode() {
        return StringUtil.hashCodeIgnoreCase(fullName);
    }

}
