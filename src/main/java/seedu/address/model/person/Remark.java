package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents an optional remark attached to a person.
 * An empty value means that the person has no remark.
 */
public class Remark {

    public final String value;

    /** Creates a remark; empty text is allowed to represent no remark. */
    public Remark(String value) {
        requireNonNull(value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof Remark otherRemark && value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
