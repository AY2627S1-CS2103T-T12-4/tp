package seedu.address.model.group.exceptions;

/**
 * Signals that the operation will result in two tutorial groups with the same name.
 */
public class DuplicateGroupException extends RuntimeException {
    public DuplicateGroupException() {
        super("Operation would result in duplicate tutorial groups");
    }
}
