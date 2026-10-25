package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Represents the in-memory model of the TAssist data and the user prefs.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TAssist tAssist;
    private final UserPrefs userPrefs;
    private final ActiveGroupTracker activeGroupTracker = new ActiveGroupTracker();
    private final FilteredList<Student> filteredStudents =
            new FilteredList<>(activeGroupTracker.getStudentList());
    private final SortedList<Student> displayedStudents = new SortedList<>(filteredStudents);
    private final ObservableList<Student> unmodifiableDisplayedStudents =
            FXCollections.unmodifiableObservableList(displayedStudents);

    /**
     * Initializes a ModelManager with copies of the given tAssist and userPrefs.
     */
    public ModelManager(ReadOnlyTAssist tAssist, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(tAssist, userPrefs);

        logger.fine("Initializing with TAssist data: " + tAssist + " and user prefs " + userPrefs);

        this.tAssist = new TAssist(tAssist);
        this.userPrefs = new UserPrefs(userPrefs);
        trackActiveGroup();
    }

    /**
     * Initializes a ModelManager with empty TAssist data and default user prefs.
     */
    public ModelManager() {
        this(new TAssist(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== TAssist ===================================================================================

    @Override
    public void setTAssist(ReadOnlyTAssist tAssist) {
        requireNonNull(tAssist);
        this.tAssist.resetData(tAssist);
        trackActiveGroup();
    }

    @Override
    public ReadOnlyTAssist getTAssist() {
        return tAssist;
    }

    @Override
    public boolean hasGroup(GroupName name) {
        requireNonNull(name);
        return tAssist.hasGroup(name);
    }

    @Override
    public void addGroup(Group group) {
        requireNonNull(group);
        tAssist.addGroup(group);
    }

    @Override
    public Group getGroup(GroupName name) {
        requireNonNull(name);
        return tAssist.getGroup(name);
    }

    @Override
    public void setActiveGroup(GroupName name) {
        requireNonNull(name);
        tAssist.setActiveGroup(name);
        logger.info("Active group is now " + name);
        trackActiveGroup();
    }

    @Override
    public Optional<Group> getActiveGroup() {
        return tAssist.getActiveGroup();
    }

    @Override
    public void addStudent(GroupName groupName, Student student) {
        requireAllNonNull(groupName, student);
        tAssist.addStudent(groupName, student);
    }

    @Override
    public void setStudent(GroupName groupName, Student student) {
        requireAllNonNull(groupName, student);
        tAssist.setStudent(groupName, student);
    }

    @Override
    public Student removeStudent(GroupName groupName, StudentId studentId) {
        requireAllNonNull(groupName, studentId);
        return tAssist.removeStudent(groupName, studentId);
    }

    @Override
    public void addAssignment(GroupName groupName, Assignment assignment) {
        requireAllNonNull(groupName, assignment);
        tAssist.addAssignment(groupName, assignment);
    }

    @Override
    public void setAssignment(GroupName groupName, Assignment assignment) {
        requireAllNonNull(groupName, assignment);
        tAssist.setAssignment(groupName, assignment);
    }

    //=========== Active Group Accessors =====================================================================

    @Override
    public ReadOnlyObjectProperty<Optional<Group>> activeGroupProperty() {
        return activeGroupTracker.activeGroupProperty();
    }

    @Override
    public ObservableList<Student> getActiveGroupStudentList() {
        return activeGroupTracker.getStudentList();
    }

    @Override
    public ObservableList<Student> getDisplayedStudentList() {
        return unmodifiableDisplayedStudents;
    }

    @Override
    public void filterStudents(Predicate<Student> predicate) {
        filteredStudents.setPredicate(requireNonNull(predicate));
        logger.fine("Updated the student display filter");
    }

    @Override
    public void sortStudents(Comparator<Student> comparator) {
        displayedStudents.setComparator(requireNonNull(comparator));
        logger.fine("Updated the student display order");
    }

    @Override
    public void resetStudentDisplay() {
        filteredStudents.setPredicate(null);
        displayedStudents.setComparator(null);
    }

    /**
     * Points the active group views at the group that is active in {@code tAssist}.
     */
    private void trackActiveGroup() {
        if (!activeGroupTracker.activeGroupProperty().get().map(Group::getName)
                .equals(tAssist.getActiveGroup().map(Group::getName))) {
            resetStudentDisplay();
        }
        activeGroupTracker.track(tAssist.getActiveGroup());
        assert activeGroupTracker.activeGroupProperty().get().equals(tAssist.getActiveGroup())
                : "The active group views must show the active group";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return tAssist.equals(otherModelManager.tAssist)
                && userPrefs.equals(otherModelManager.userPrefs);
    }

}
