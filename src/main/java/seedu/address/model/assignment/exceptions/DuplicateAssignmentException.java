package seedu.address.model.assignment.exceptions;

/**
 * Signals that the operation will result in two assignments with the same name in one group.
 */
public class DuplicateAssignmentException extends RuntimeException {
    public DuplicateAssignmentException() {
        super("Operation would result in duplicate assignments");
    }
}
