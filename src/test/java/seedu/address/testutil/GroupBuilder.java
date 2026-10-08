package seedu.address.testutil;

import java.util.ArrayList;
import java.util.List;

import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.Student;

/**
 * A utility class to help with building Group objects.
 * Example usage: <br>
 *     {@code Group group = new GroupBuilder().withName("T02").withStudents(alice, ben).build();}
 */
public class GroupBuilder {

    public static final String DEFAULT_NAME = "T01";

    private GroupName name;
    private List<Student> students;

    /**
     * Creates a {@code GroupBuilder} with the default name and no students.
     */
    public GroupBuilder() {
        name = new GroupName(DEFAULT_NAME);
        students = new ArrayList<>();
    }

    /**
     * Initializes the GroupBuilder with the data of {@code groupToCopy}.
     */
    public GroupBuilder(Group groupToCopy) {
        name = groupToCopy.getName();
        students = new ArrayList<>(groupToCopy.getStudentList());
    }

    /**
     * Sets the {@code GroupName} of the {@code Group} that we are building.
     */
    public GroupBuilder withName(String name) {
        this.name = new GroupName(name);
        return this;
    }

    /**
     * Sets the {@code GroupName} of the {@code Group} that we are building.
     */
    public GroupBuilder withName(GroupName name) {
        this.name = name;
        return this;
    }

    /**
     * Adds {@code students} to the {@code Group} that we are building, after any students added earlier.
     * Their student IDs must be unique, or {@link #build()} will fail.
     */
    public GroupBuilder withStudents(Student... students) {
        this.students.addAll(List.of(students));
        return this;
    }

    /**
     * Returns a new {@code Group} each time, so that tests never share a mutable group.
     */
    public Group build() {
        Group group = new Group(name);
        students.forEach(group::addStudent);
        return group;
    }

}
