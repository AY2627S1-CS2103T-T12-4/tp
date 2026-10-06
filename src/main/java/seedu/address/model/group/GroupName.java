package seedu.address.model.group;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

import seedu.address.commons.util.StringUtil;

/**
 * Represents the name of a tutorial group.
 * Guarantees: immutable; is valid as declared in {@link #isValidGroupName(String)}.
 * Leading and trailing spaces are removed and repeated internal spaces are treated as one space.
 * Group names are compared case-insensitively (e.g. {@code T01} and {@code t01} are the same group name),
 * but the capitalization entered by the user is kept for display.
 */
public class GroupName {

    public static final int MAX_LENGTH = 60;

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid group name. Use 1-" + MAX_LENGTH + " letters, digits, spaces, or hyphens.";

    /*
     * Only letters, digits, spaces and hyphens, with at least one letter or digit,
     * so that names such as "-" or "--" are rejected.
     */
    public static final String VALIDATION_REGEX = "(?=.*[\\p{L}\\p{Nd}])[\\p{L}\\p{Nd} -]+";

    public final String fullName;

    /**
     * Constructs a {@code GroupName}.
     *
     * @param name A valid group name.
     */
    public GroupName(String name) {
        requireNonNull(name);
        checkArgument(isValidGroupName(name), MESSAGE_CONSTRAINTS);
        fullName = StringUtil.normalizeSpaces(name);
    }

    /**
     * Returns true if a given string is a valid group name after its spaces are normalized.
     */
    public static boolean isValidGroupName(String test) {
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
        if (!(other instanceof GroupName otherGroupName)) {
            return false;
        }

        return fullName.equalsIgnoreCase(otherGroupName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.toLowerCase(Locale.ROOT).hashCode();
    }

}
