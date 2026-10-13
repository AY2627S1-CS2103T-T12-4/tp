package seedu.address.model;

import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
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
     * Removes the student with {@code studentId} from the group named {@code groupName},
     * and returns the removed student.
     * The group must exist, and the student must be in it.
     */
    Student removeStudent(GroupName groupName, StudentId studentId);

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
}
