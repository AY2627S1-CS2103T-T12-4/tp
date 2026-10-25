package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import seedu.address.model.assignment.exceptions.AssignmentNotFoundException;
import seedu.address.model.assignment.exceptions.DuplicateAssignmentException;
import seedu.address.model.util.UniqueList;

/**
 * A list of assignments that enforces unique assignment names, compared case-insensitively.
 *
 * @see Assignment#isSameAssignment(Assignment)
 */
public class UniqueAssignmentList extends UniqueList<Assignment> {

    @Override
    protected boolean isSameItem(Assignment first, Assignment second) {
        return first.isSameAssignment(second);
    }

    @Override
    protected RuntimeException createDuplicateException() {
        return new DuplicateAssignmentException();
    }

    @Override
    protected RuntimeException createNotFoundException() {
        return new AssignmentNotFoundException();
    }

    /**
     * Returns true if the list contains an assignment named {@code name}.
     */
    public boolean contains(AssignmentName name) {
        return find(name).isPresent();
    }

    /**
     * Returns the assignment named {@code name}, or an empty {@code Optional} if there is no such assignment.
     */
    public Optional<Assignment> find(AssignmentName name) {
        requireNonNull(name);
        return findFirst(assignment -> assignment.getName().equals(name));
    }

    /**
     * Replaces the contents of this list with {@code assignments}.
     * {@code assignments} must not contain assignments with the same name.
     */
    public void setAssignments(List<Assignment> assignments) {
        setAll(assignments);
    }

    /**
     * Replaces the assignment with the same name as {@code assignment}.
     * The assignment must already exist in the list.
     */
    public void setAssignment(Assignment assignment) {
        set(assignment);
    }
}
