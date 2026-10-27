package seedu.address.model;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * The API of the Model component.
 */
public interface Model {
    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces TAssist data with the data in {@code tAssist}, including which group is active.
     */
    void setTAssist(ReadOnlyTAssist tAssist);

    /** Returns the TAssist data. */
    ReadOnlyTAssist getTAssist();

    /**
     * Returns true if a group named {@code name} exists, ignoring case.
     */
    boolean hasGroup(GroupName name);

    /**
     * Adds the given group.
     * No existing group may have the same name, ignoring case. Adding a group does not make it active.
     */
    void addGroup(Group group);

    /**
     * Returns the group named {@code name}, ignoring case.
     *
     * @throws seedu.address.model.group.exceptions.GroupNotFoundException if there is no such group.
     */
    Group getGroup(GroupName name);

    /**
     * Removes the group named {@code name}, with its students, and returns the removed group.
     * If it is the active group, no group is active afterwards.
     *
     * @throws seedu.address.model.group.exceptions.GroupNotFoundException if there is no such group.
     */
    Group removeGroup(GroupName name);

    /**
     * Renames the group named {@code name} to {@code newName}, keeping its students and active status.
     * No other group may have {@code newName}, ignoring case.
     *
     * @throws seedu.address.model.group.exceptions.GroupNotFoundException if there is no group named {@code name}.
     * @throws seedu.address.model.group.exceptions.DuplicateGroupException if another group has {@code newName}.
     */
    void renameGroup(GroupName name, GroupName newName);

    /**
     * Makes the group named {@code name} the active group.
     *
     * @throws seedu.address.model.group.exceptions.GroupNotFoundException if there is no such group.
     */
    void setActiveGroup(GroupName name);

    /**
     * Returns the active group, or an empty {@code Optional} if no group is active.
     */
    Optional<Group> getActiveGroup();

    /**
     * Adds {@code student} to the group named {@code groupName}.
     * The group must exist, and no student in it may have the same student ID.
     */
    void addStudent(GroupName groupName, Student student);

    /**
     * Replaces the student with the same ID in the named group.
     * The group and student must already exist.
     */
    void setStudent(GroupName groupName, Student student);

    /**
     * Removes the student with {@code studentId} from the group named {@code groupName},
     * and returns the removed student.
     * The group must exist, and the student must be in it.
     */
    Student removeStudent(GroupName groupName, StudentId studentId);

    /**
     * Adds {@code assignment} to the group named {@code groupName}.
     * The group must exist, and no assignment in it may have the same name, ignoring case.
     */
    void addAssignment(GroupName groupName, Assignment assignment);

    /**
     * Replaces the assignment with the same name in the group named {@code groupName}.
     * The group and assignment must already exist.
     */
    void setAssignment(GroupName groupName, Assignment assignment);

    /**
     * Returns the active group as an observable value, which is empty when no group is active.
     * It changes whenever another group becomes active, but not when the new active group is equal to the old one,
     * for example after {@code setTAssist} with the same data. Read the students from
     * {@link #getActiveGroupStudentList()} rather than from a {@code Group} taken from this value earlier.
     */
    ReadOnlyObjectProperty<Optional<Group>> activeGroupProperty();

    /**
     * Returns an unmodifiable view of the students in the active group.
     * It follows the active group, and is empty when no group is active.
     */
    ObservableList<Student> getActiveGroupStudentList();

    /** Returns the active group's students in the current filtered and sorted display order. */
    ObservableList<Student> getDisplayedStudentList();

    /** Returns whether a student search is active, including when the roster is empty. */
    ReadOnlyBooleanProperty studentFilterActiveProperty();

    /** Replaces the display filter without changing the stored roster. */
    void filterStudents(Predicate<Student> predicate);

    /** Sorts the displayed students without changing the stored roster. */
    void sortStudents(Comparator<Student> comparator);

    /** Restores the complete student display in roster order. */
    void resetStudentDisplay();

    /** Returns the transient display settings for command rollback. */
    StudentDisplayState getStudentDisplayState();

    /** Restores display settings captured before a failed command. */
    void setStudentDisplayState(StudentDisplayState state);
}
