package seedu.address.model.student.exceptions;

/**
 * Signals that the operation will result in two students with the same student ID in one tutorial group.
 */
public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException() {
        super("Operation would result in duplicate students");
    }
}
