package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a student in a tutorial group.
 * A student is identified by their student ID, which is unique within a group.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Student {

    private final StudentName name;
    private final StudentId studentId;

    /**
     * Every field must be present and not null.
     */
    public Student(StudentName name, StudentId studentId) {
        requireAllNonNull(name, studentId);
        this.name = name;
        this.studentId = studentId;
    }

    public StudentName getName() {
        return name;
    }

    public StudentId getStudentId() {
        return studentId;
    }

    /**
     * Returns true if both students have the same student ID.
     * This defines a weaker notion of equality between two students.
     */
    public boolean isSameStudent(Student otherStudent) {
        if (otherStudent == this) {
            return true;
        }

        return otherStudent != null
                && otherStudent.getStudentId().equals(getStudentId());
    }

    /**
     * Returns true if both students have the same name and student ID.
     * This defines a stronger notion of equality between two students.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Student otherStudent)) {
            return false;
        }

        return name.equals(otherStudent.name)
                && studentId.equals(otherStudent.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, studentId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("studentId", studentId)
                .toString();
    }

}
