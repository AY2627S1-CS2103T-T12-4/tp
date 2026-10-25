package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Keeps earlier versions of TAssist data so that changes can be undone and redone.
 * Each version is stored with a description of the change made to it. At most {@link #MAX_CHANGES} changes are
 * kept, and recording a new change discards the changes that were undone.
 */
public class TAssistHistory {
    /** Number of changes that can be undone, newest first. */
    public static final int MAX_CHANGES = 20;

    private final Deque<Version> undoVersions = new ArrayDeque<>();
    private final Deque<Version> redoVersions = new ArrayDeque<>();

    /**
     * Creates an empty history, with nothing to undo or redo.
     */
    public TAssistHistory() {}

    /**
     * Creates a copy of {@code toBeCopied}. Later changes to either history do not affect the other.
     */
    public TAssistHistory(TAssistHistory toBeCopied) {
        requireNonNull(toBeCopied);
        undoVersions.addAll(toBeCopied.undoVersions);
        redoVersions.addAll(toBeCopied.redoVersions);
    }

    /**
     * Records that the change described by {@code description} was made to {@code previousData}.
     * Changes that were undone can no longer be redone, and the oldest change is forgotten once there are more
     * than {@link #MAX_CHANGES}.
     */
    public void record(ReadOnlyTAssist previousData, String description) {
        requireAllNonNull(previousData, description);
        undoVersions.push(new Version(previousData, description));
        if (undoVersions.size() > MAX_CHANGES) {
            undoVersions.removeLast();
        }
        redoVersions.clear();
    }

    public boolean canUndo() {
        return !undoVersions.isEmpty();
    }

    public boolean canRedo() {
        return !redoVersions.isEmpty();
    }

    /**
     * Returns the version from before the latest change, and keeps {@code currentData} so the change can be redone.
     *
     * @throws IllegalStateException if there is no change to undo.
     */
    public Version undo(ReadOnlyTAssist currentData) {
        requireNonNull(currentData);
        if (!canUndo()) {
            throw new IllegalStateException("There is no change to undo.");
        }
        Version undone = undoVersions.pop();
        redoVersions.push(new Version(currentData, undone.description()));
        assert undoVersions.size() + redoVersions.size() <= MAX_CHANGES : "History must stay within its limit";
        return undone;
    }

    /**
     * Returns the version from after the latest undone change, and keeps {@code currentData} so the change can be
     * undone again.
     *
     * @throws IllegalStateException if there is no undone change to redo.
     */
    public Version redo(ReadOnlyTAssist currentData) {
        requireNonNull(currentData);
        if (!canRedo()) {
            throw new IllegalStateException("There is no change to redo.");
        }
        Version redone = redoVersions.pop();
        undoVersions.push(new Version(currentData, redone.description()));
        assert undoVersions.size() + redoVersions.size() <= MAX_CHANGES : "History must stay within its limit";
        return redone;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TAssistHistory otherHistory)) {
            return false;
        }

        return List.copyOf(undoVersions).equals(List.copyOf(otherHistory.undoVersions))
                && List.copyOf(redoVersions).equals(List.copyOf(otherHistory.redoVersions));
    }

    @Override
    public int hashCode() {
        return Objects.hash(List.copyOf(undoVersions), List.copyOf(redoVersions));
    }

    /**
     * A copy of TAssist data, with the description of the change made to or from it.
     *
     * @param data A copy of the data, which is never changed.
     * @param description The command that changed the data, as the user entered it.
     */
    public record Version(ReadOnlyTAssist data, String description) {
        /**
         * Creates a version that holds a copy of {@code data}, so later changes to {@code data} do not affect it.
         */
        public Version {
            requireAllNonNull(data, description);
            data = new TAssist(data);
        }
    }
}
