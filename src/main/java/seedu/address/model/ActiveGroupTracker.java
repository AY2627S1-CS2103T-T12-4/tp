package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;

/**
 * Keeps observable views of the active group and its students for the UI to bind to.
 * The student list follows the active group: it shows the students of whichever group is active,
 * and changes when students are added to or removed from that group.
 */
class ActiveGroupTracker {

    private final ReadOnlyObjectWrapper<Optional<Group>> activeGroup = new ReadOnlyObjectWrapper<>(Optional.empty());
    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final ObservableList<Student> unmodifiableStudents = FXCollections.unmodifiableObservableList(students);
    private final ListChangeListener<Student> studentListListener = change -> students.setAll(change.getList());

    /** Student list of the group being tracked, or null if no group is active. */
    private ObservableList<Student> trackedStudents;

    /**
     * Starts tracking {@code newActiveGroup}, or no group if it is empty.
     * The previously tracked group is no longer followed, so later changes to it do not show.
     */
    void track(Optional<Group> newActiveGroup) {
        requireNonNull(newActiveGroup);

        if (trackedStudents != null) {
            trackedStudents.removeListener(studentListListener);
        }
        trackedStudents = newActiveGroup.map(Group::getStudentList).orElse(null);
        if (trackedStudents != null) {
            trackedStudents.addListener(studentListListener);
            students.setAll(trackedStudents);
        } else {
            students.clear();
        }
        activeGroup.set(newActiveGroup);
    }

    /**
     * Returns the active group, which is empty when no group is active.
     */
    ReadOnlyObjectProperty<Optional<Group>> activeGroupProperty() {
        return activeGroup.getReadOnlyProperty();
    }

    /**
     * Returns an unmodifiable view of the students in the active group.
     * The list is empty when no group is active.
     */
    ObservableList<Student> getStudentList() {
        return unmodifiableStudents;
    }
}
