package seedu.address.testutil;

import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;

/**
 * A utility class to help with building Student objects.
 */
public class StudentBuilder {

    public static final String DEFAULT_NAME = "Alice Tan";
    public static final String DEFAULT_STUDENT_ID = "A0123456X";

    private StudentName name;
    private StudentId studentId;

    /**
     * Creates a {@code StudentBuilder} with the default details.
     */
    public StudentBuilder() {
        name = new StudentName(DEFAULT_NAME);
        studentId = new StudentId(DEFAULT_STUDENT_ID);
    }

    /**
     * Initializes the StudentBuilder with the data of {@code studentToCopy}.
     */
    public StudentBuilder(Student studentToCopy) {
        name = studentToCopy.getName();
        studentId = studentToCopy.getStudentId();
    }

    /**
     * Sets the {@code StudentName} of the {@code Student} that we are building.
     */
    public StudentBuilder withName(String name) {
        this.name = new StudentName(name);
        return this;
    }

    /**
     * Sets the {@code StudentId} of the {@code Student} that we are building.
     */
    public StudentBuilder withStudentId(String studentId) {
        this.studentId = new StudentId(studentId);
        return this;
    }

    public Student build() {
        return new Student(name, studentId);
    }

}
