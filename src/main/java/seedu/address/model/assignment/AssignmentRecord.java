package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Stores one student's submission status and optional grade for an assignment.
 */
public final class AssignmentRecord {
    public static final String MESSAGE_INVALID_GRADE = "Invalid grade. Enter a whole number from 0 to 100.";

    private final boolean isSubmitted;
    private final Optional<Integer> grade;

    /**
     * Creates an assignment record and checks that its grade is valid for its submission status.
     */
    public AssignmentRecord(boolean isSubmitted, Optional<Integer> grade) {
        requireNonNull(grade);
        checkArgument(isSubmitted || grade.isEmpty(), "An unsubmitted assignment cannot have a grade.");
        checkArgument(grade.isEmpty() || isValidGrade(grade.get()), MESSAGE_INVALID_GRADE);
        this.isSubmitted = isSubmitted;
        this.grade = grade;
    }

    /**
     * Returns a record for a student who has not submitted the assignment.
     */
    public static AssignmentRecord notSubmitted() {
        return new AssignmentRecord(false, Optional.empty());
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public Optional<Integer> getGrade() {
        return grade;
    }

    /**
     * Returns a copy with the given submission status; withdrawing submission clears its grade.
     */
    public AssignmentRecord withSubmissionStatus(boolean isSubmitted) {
        return new AssignmentRecord(isSubmitted, isSubmitted ? grade : Optional.empty());
    }

    /**
     * Returns a submitted copy with the given grade, since only a submitted assignment can have a grade.
     *
     * @throws IllegalArgumentException if {@code newGrade} is not from 0 through 100.
     */
    public AssignmentRecord withGrade(int newGrade) {
        return new AssignmentRecord(true, Optional.of(newGrade));
    }

    /**
     * Returns whether a grade is a whole number from 0 through 100, inclusive.
     */
    public static boolean isValidGrade(int grade) {
        return grade >= 0 && grade <= 100;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AssignmentRecord otherRecord)) {
            return false;
        }
        return isSubmitted == otherRecord.isSubmitted && grade.equals(otherRecord.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSubmitted, grade);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("submitted", isSubmitted).add("grade", grade).toString();
    }
}
