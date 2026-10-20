package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.student.StudentId;

/**
 * Represents an assignment created for a tutorial group.
 * An assignment is identified by its name, which is unique within a group, ignoring case.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Assignment {

    private final AssignmentName name;
    private final Map<StudentId, AssignmentRecord> studentRecords;

    /**
     * Creates an assignment with the given {@code name}.
     */
    public Assignment(AssignmentName name) {
        this(name, Map.of());
    }

    /**
     * Creates an assignment with the given records, defensively copying the provided map.
     */
    public Assignment(AssignmentName name, Map<StudentId, AssignmentRecord> studentRecords) {
        this.name = requireNonNull(name);
        requireNonNull(studentRecords);
        Map<StudentId, AssignmentRecord> copiedRecords = new HashMap<>();
        studentRecords.forEach((studentId, record) ->
                copiedRecords.put(requireNonNull(studentId), requireNonNull(record)));
        this.studentRecords = Collections.unmodifiableMap(copiedRecords);
    }

    public AssignmentName getName() {
        return name;
    }

    /** Returns a student's assignment record, using an unsubmitted record when none has been entered. */
    public AssignmentRecord getStudentRecord(StudentId studentId) {
        requireNonNull(studentId);
        return studentRecords.getOrDefault(studentId, AssignmentRecord.notSubmitted());
    }

    /** Returns all explicitly entered student records as an unmodifiable map. */
    public Map<StudentId, AssignmentRecord> getStudentRecords() {
        return studentRecords;
    }

    /** Returns a copy with the student's submission status added or replaced. */
    public Assignment withSubmissionStatus(StudentId studentId, boolean submitted) {
        return withRecord(studentId, getStudentRecord(studentId).withSubmissionStatus(submitted));
    }

    /** Returns a copy with the student's grade added or replaced. */
    public Assignment withGrade(StudentId studentId, int grade) {
        return withRecord(studentId, getStudentRecord(studentId).withGrade(grade));
    }

    /** Returns a copy with the student's complete assignment record added or replaced. */
    public Assignment withRecord(StudentId studentId, AssignmentRecord record) {
        requireNonNull(studentId);
        requireNonNull(record);
        Map<StudentId, AssignmentRecord> updatedRecords = new HashMap<>(studentRecords);
        updatedRecords.put(studentId, record);
        return new Assignment(name, updatedRecords);
    }

    /**
     * Returns true if both assignments have the same name, ignoring case.
     * This check is used to enforce unique assignment names within a group.
     */
    public boolean isSameAssignment(Assignment otherAssignment) {
        if (otherAssignment == this) {
            return true;
        }

        return otherAssignment != null
                && otherAssignment.getName().equals(getName());
    }

    /**
     * Returns true if both assignments have the same name and student records.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Assignment otherAssignment)) {
            return false;
        }

        return name.equals(otherAssignment.name) && studentRecords.equals(otherAssignment.studentRecords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, studentRecords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("studentRecords", studentRecords)
                .toString();
    }

}
