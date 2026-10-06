package seedu.address.model.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of items that enforces uniqueness between its elements and does not allow nulls.
 * Two items are considered the same if {@link #isSameItem(Object, Object)} returns true. Subclasses define what
 * "the same" means for their item type, e.g., two students with the same student ID.
 * Adding and replacing items uses this identity check, while removing an item uses {@code equals}.
 *
 * Supports a minimal set of list operations.
 *
 * @param <T> Type of the items in the list.
 */
public abstract class UniqueList<T> implements Iterable<T> {

    private final ObservableList<T> internalList = FXCollections.observableArrayList();
    private final ObservableList<T> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if {@code first} and {@code second} have the same identity.
     */
    protected abstract boolean isSameItem(T first, T second);

    /**
     * Creates the exception thrown when an operation would result in duplicate items.
     */
    protected abstract RuntimeException createDuplicateException();

    /**
     * Creates the exception thrown when an item cannot be found in the list.
     */
    protected abstract RuntimeException createNotFoundException();

    /**
     * Returns true if the list contains an item with the same identity as {@code toCheck}.
     */
    public boolean contains(T toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(item -> isSameItem(item, toCheck));
    }

    /**
     * Adds an item to the list.
     * The item must not already exist in the list.
     */
    public void add(T toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw createDuplicateException();
        }
        internalList.add(toAdd);
    }

    /**
     * Removes the item that is equal to {@code toRemove} from the list.
     * The item must exist in the list.
     */
    public void remove(T toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw createNotFoundException();
        }
    }

    /**
     * Replaces the contents of this list with {@code items}.
     * {@code items} must not contain duplicate items.
     */
    public void setAll(List<T> items) {
        requireAllNonNull(items);
        if (!areUnique(items)) {
            throw createDuplicateException();
        }
        internalList.setAll(items);
    }

    /**
     * Returns the first item that matches {@code predicate}, or an empty {@code Optional} if there is none.
     */
    protected Optional<T> findFirst(Predicate<T> predicate) {
        requireNonNull(predicate);
        return internalList.stream().filter(predicate).findFirst();
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<T> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<T> iterator() {
        return internalUnmodifiableList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // handles nulls, and lists of different item types
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        UniqueList<?> otherUniqueList = (UniqueList<?>) other;
        return internalList.equals(otherUniqueList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    /**
     * Returns true if {@code items} contains only items with different identities.
     */
    private boolean areUnique(List<T> items) {
        for (int i = 0; i < items.size() - 1; i++) {
            for (int j = i + 1; j < items.size(); j++) {
                if (isSameItem(items.get(i), items.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
