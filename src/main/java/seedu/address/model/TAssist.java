package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.UniqueGroupList;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Wraps all TAssist data: the tutorial groups, each holding its own students, and the active group.
 * Two groups cannot have the same name (compared case-insensitively), and a student ID is unique within a group.
 * Invariant: if there is an active group, it is one of the groups held.
 */
public class TAssist implements ReadOnlyTAssist {

    private final UniqueGroupList groups = new UniqueGroupList();

    /** Name of the active group, or null if no group is active. */
    private GroupName activeGroupName;

    public TAssist() {}

    /**
     * Creates a {@code TAssist} with a copy of the data in {@code toBeCopied}.
     */
    public TAssist(ReadOnlyTAssist toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// whole-data operations

    /**
     * Replaces the existing data of this {@code TAssist} with a copy of {@code newData}.
     * Groups are copied, so later changes to this {@code TAssist} do not affect {@code newData}.
     */
    public void resetData(ReadOnlyTAssist newData) {
        requireNonNull(newData);

        List<Group> copiedGroups = newData.getGroupList().stream().map(Group::new).toList();
        groups.setGroups(copiedGroups);
        activeGroupName = newData.getActiveGroup().map(Group::getName).orElse(null);
        assert isActiveGroupHeld() : "The active group must be one of the groups held";
    }

    //// group-level operations

    /**
     * Returns true if a group named {@code name} exists, ignoring case.
     */
    public boolean hasGroup(GroupName name) {
        return groups.contains(name);
    }

    /**
     * Adds {@code group}.
     * No existing group may have the same name, ignoring case.
     */
    public void addGroup(Group group) {
        groups.add(group);
    }

    /**
     * Returns the group named {@code name}, ignoring case.
     *
     * @throws GroupNotFoundException if there is no such group.
     */
    public Group getGroup(GroupName name) {
        return groups.find(name).orElseThrow(GroupNotFoundException::new);
    }

    /**
     * Makes the group named {@code name} the active group.
     *
     * @throws GroupNotFoundException if there is no such group.
     */
    public void setActiveGroup(GroupName name) {
        activeGroupName = getGroup(name).getName();
    }

    @Override
    public Optional<Group> getActiveGroup() {
        if (activeGroupName == null) {
            return Optional.empty();
        }
        Optional<Group> activeGroup = groups.find(activeGroupName);
        assert activeGroup.isPresent() : "The active group must be one of the groups held";
        return activeGroup;
    }

    //// student-level operations

    /**
     * Adds {@code student} to the group named {@code groupName}.
     * The group must exist, and no student in it may have the same student ID.
     */
    public void addStudent(GroupName groupName, Student student) {
        requireAllNonNull(groupName, student);
        getGroup(groupName).addStudent(student);
    }

    /**
     * Removes the student with {@code studentId} from the group named {@code groupName},
     * and returns the removed student.
     * The group must exist, and the student must be in it.
     */
    public Student removeStudent(GroupName groupName, StudentId studentId) {
        requireAllNonNull(groupName, studentId);
        return getGroup(groupName).removeStudent(studentId);
    }

    //// util methods

    @Override
    public ObservableList<Group> getGroupList() {
        return groups.asUnmodifiableObservableList();
    }

    private boolean isActiveGroupHeld() {
        return activeGroupName == null || groups.contains(activeGroupName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TAssist otherTAssist)) {
            return false;
        }

        return groups.equals(otherTAssist.groups)
                && Objects.equals(activeGroupName, otherTAssist.activeGroupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groups, activeGroupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("groups", groups)
                .add("activeGroup", activeGroupName)
                .toString();
    }
}
