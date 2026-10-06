package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a student's name.
 * Guarantees: immutable; is valid as declared in {@link #isValidStudentName(String)}.
 * Leading and trailing spaces are removed and repeated internal spaces are treated as one space.
 * The capitalization entered by the user is kept.
 */
public class StudentName {

    public static final int MAX_LENGTH = 60;

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid student name. Use 1-" + MAX_LENGTH + " letters, spaces, apostrophes, or hyphens.";

    /*
     * Letters (including accented letters), spaces, apostrophes (straight or curly) and hyphens,
     * with at least one letter.
     */
    public static final String VALIDATION_REGEX = "(?=.*\\p{L})[\\p{L}\\p{M} '’-]+";

    public final String fullName;

    /**
     * Constructs a {@code StudentName}.
     *
     * @param name A valid student name.
     */
    public StudentName(String name) {
        requireNonNull(name);
        checkArgument(isValidStudentName(name), MESSAGE_CONSTRAINTS);
        fullName = StringUtil.normalizeSpaces(name);
    }

    /**
     * Returns true if a given string is a valid student name after its spaces are normalized.
     */
    public static boolean isValidStudentName(String test) {
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
        if (!(other instanceof StudentName otherStudentName)) {
            return false;
        }

        return fullName.equals(otherStudentName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
