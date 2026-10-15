package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents an assignment created for a tutorial group.
 * An assignment is identified by its name, which is unique within a group, ignoring case.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Assignment {

    private final AssignmentName name;

    /**
     * Creates an assignment with the given {@code name}.
     */
    public Assignment(AssignmentName name) {
        this.name = requireNonNull(name);
    }

    public AssignmentName getName() {
        return name;
    }

    /**
     * Returns true if both assignments have the same name, ignoring case.
     * Assignments are identified by name alone, so this is the same as {@link #equals(Object)}.
     */
    public boolean isSameAssignment(Assignment otherAssignment) {
        if (otherAssignment == this) {
            return true;
        }

        return otherAssignment != null
                && otherAssignment.getName().equals(getName());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Assignment otherAssignment)) {
            return false;
        }

        return name.equals(otherAssignment.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .toString();
    }

}
