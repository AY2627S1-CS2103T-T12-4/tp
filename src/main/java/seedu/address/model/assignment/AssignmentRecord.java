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
    public static final String MESSAGE_INVALID_GRADE = "Grade must be a whole number from 0 to 100.";

    private final boolean submitted;
    private final Optional<Integer> grade;

    /**
     * Creates an assignment record and checks that its grade is valid for its submission status.
     */
    public AssignmentRecord(boolean submitted, Optional<Integer> grade) {
        requireNonNull(grade);
        checkArgument(submitted || grade.isEmpty(), "An unsubmitted assignment cannot have a grade.");
        checkArgument(grade.isEmpty() || isValidGrade(grade.get()), MESSAGE_INVALID_GRADE);
        this.submitted = submitted;
        this.grade = grade;
    }

    /** Returns a record for a student who has not submitted the assignment. */
    public static AssignmentRecord notSubmitted() {
        return new AssignmentRecord(false, Optional.empty());
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Optional<Integer> getGrade() {
        return grade;
    }

    /** Returns a copy with the given submission status; withdrawing submission clears its grade. */
    public AssignmentRecord withSubmissionStatus(boolean newStatus) {
        return new AssignmentRecord(newStatus, newStatus ? grade : Optional.empty());
    }

    /** Returns a copy with the given grade if the student has submitted the assignment. */
    public AssignmentRecord withGrade(int newGrade) {
        checkArgument(submitted, "Cannot grade an unsubmitted assignment.");
        return new AssignmentRecord(submitted, Optional.of(newGrade));
    }

    private static boolean isValidGrade(int grade) {
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
        return submitted == otherRecord.submitted && grade.equals(otherRecord.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(submitted, grade);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("submitted", submitted).add("grade", grade).toString();
    }
}
