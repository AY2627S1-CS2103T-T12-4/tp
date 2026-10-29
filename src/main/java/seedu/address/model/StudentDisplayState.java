package seedu.address.model;

import java.util.Comparator;
import java.util.function.Predicate;

import seedu.address.model.student.Student;

/**
 * Captures the transient student display settings so a failed command can restore them.
 * A null predicate includes every student; a null comparator preserves roster order.
 *
 * @param predicate The current search predicate, or null for no search.
 * @param comparator The current sort comparator, or null for roster order.
 */
public record StudentDisplayState(Predicate<? super Student> predicate, Comparator<? super Student> comparator) {
}
