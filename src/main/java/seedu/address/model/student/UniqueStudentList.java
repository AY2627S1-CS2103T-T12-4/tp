package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;
import seedu.address.model.util.UniqueList;

/**
 * A list of students that enforces unique student IDs.
 *
 * @see Student#isSameStudent(Student)
 */
public class UniqueStudentList extends UniqueList<Student> {

    @Override
    protected boolean isSameItem(Student first, Student second) {
        return first.isSameStudent(second);
    }

    @Override
    protected RuntimeException createDuplicateException() {
        return new DuplicateStudentException();
    }

    @Override
    protected RuntimeException createNotFoundException() {
        return new StudentNotFoundException();
    }

    /**
     * Returns true if the list contains a student with {@code studentId}.
     */
    public boolean contains(StudentId studentId) {
        return find(studentId).isPresent();
    }

    /**
     * Returns the student with {@code studentId}, or an empty {@code Optional} if there is no such student.
     */
    public Optional<Student> find(StudentId studentId) {
        requireNonNull(studentId);
        return findFirst(student -> student.getStudentId().equals(studentId));
    }

    /**
     * Removes the student with {@code studentId} from the list and returns the removed student.
     * The student must exist in the list.
     */
    public Student remove(StudentId studentId) {
        Student toRemove = find(studentId).orElseThrow(StudentNotFoundException::new);
        remove(toRemove);
        return toRemove;
    }

    /**
     * Replaces the contents of this list with {@code students}.
     * {@code students} must not contain students with the same student ID.
     */
    public void setStudents(List<Student> students) {
        setAll(students);
    }
}
