package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.Student;

/**
 * Jackson-friendly version of {@link Group}.
 * A missing student list is read as a group with no students, and a missing assignment list as no assignments.
 */
class JsonAdaptedGroup {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Group's %s field is missing.";
    public static final String MESSAGE_EMPTY_STUDENT = "Group %s has an empty entry in its student list.";
    public static final String MESSAGE_DUPLICATE_STUDENT = "Group %s has more than one student with ID %s.";
    public static final String MESSAGE_EMPTY_ASSIGNMENT = "Group %s has an empty entry in its assignment list.";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "Group %s has more than one assignment named %s.";

    private final String name;
    private final List<JsonAdaptedStudent> students = new ArrayList<>();
    private final List<String> assignments = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedGroup} with the given group details.
     */
    @JsonCreator
    public JsonAdaptedGroup(@JsonProperty("name") String name,
            @JsonProperty("students") List<JsonAdaptedStudent> students,
            @JsonProperty("assignments") List<String> assignments) {
        this.name = name;
        if (students != null) {
            this.students.addAll(students);
        }
        if (assignments != null) {
            this.assignments.addAll(assignments);
        }
    }

    /**
     * Constructs a {@code JsonAdaptedGroup} without assignments, for existing student-only data and tests.
     */
    public JsonAdaptedGroup(String name, List<JsonAdaptedStudent> students) {
        this(name, students, null);
    }

    /**
     * Converts a given {@code Group} into this class for Jackson use.
     */
    public JsonAdaptedGroup(Group source) {
        name = source.getName().fullName;
        students.addAll(source.getStudentList().stream().map(JsonAdaptedStudent::new).toList());
        assignments.addAll(source.getAssignmentList().stream()
                .map(assignment -> assignment.getName().fullName)
                .toList());
    }

    /**
     * Converts this Jackson-friendly adapted group object into the model's {@code Group} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted group.
     */
    public Group toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }
        if (!GroupName.isValidGroupName(name)) {
            throw new IllegalValueException(GroupName.MESSAGE_CONSTRAINTS);
        }
        final Group group = new Group(new GroupName(name));

        for (JsonAdaptedStudent jsonAdaptedStudent : students) {
            addStudent(group, jsonAdaptedStudent);
        }
        for (String assignmentName : assignments) {
            addAssignment(group, assignmentName);
        }
        return group;
    }

    private static void addStudent(Group group, JsonAdaptedStudent jsonAdaptedStudent)
            throws IllegalValueException {
        if (jsonAdaptedStudent == null) {
            throw new IllegalValueException(String.format(MESSAGE_EMPTY_STUDENT, group.getName()));
        }
        Student student = jsonAdaptedStudent.toModelType();
        if (group.hasStudent(student.getStudentId())) {
            throw new IllegalValueException(String.format(MESSAGE_DUPLICATE_STUDENT,
                    group.getName(), student.getStudentId()));
        }
        group.addStudent(student);
    }

    private static void addAssignment(Group group, String assignmentName) throws IllegalValueException {
        if (assignmentName == null) {
            throw new IllegalValueException(String.format(MESSAGE_EMPTY_ASSIGNMENT, group.getName()));
        }
        if (!AssignmentName.isValidAssignmentName(assignmentName)) {
            throw new IllegalValueException(AssignmentName.MESSAGE_CONSTRAINTS);
        }
        Assignment assignment = new Assignment(new AssignmentName(assignmentName));
        if (group.hasAssignment(assignment.getName())) {
            throw new IllegalValueException(String.format(MESSAGE_DUPLICATE_ASSIGNMENT,
                    group.getName(), assignment.getName()));
        }
        group.addAssignment(assignment);
    }

}
