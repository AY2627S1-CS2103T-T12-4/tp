package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.Student;

/**
 * Jackson-friendly version of {@link Group}.
 * A missing student list is read as a group with no students.
 */
class JsonAdaptedGroup {

    public static final String MISSING_NAME_MESSAGE = "Group's name field is missing.";
    public static final String MESSAGE_EMPTY_STUDENT = "Group %s has an empty entry in its student list.";
    public static final String MESSAGE_DUPLICATE_STUDENT = "Group %s has more than one student with ID %s.";

    private final String name;
    private final List<JsonAdaptedStudent> students = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedGroup} with the given group details.
     */
    @JsonCreator
    public JsonAdaptedGroup(@JsonProperty("name") String name,
            @JsonProperty("students") List<JsonAdaptedStudent> students) {
        this.name = name;
        if (students != null) {
            this.students.addAll(students);
        }
    }

    /**
     * Converts a given {@code Group} into this class for Jackson use.
     */
    public JsonAdaptedGroup(Group source) {
        name = source.getName().fullName;
        students.addAll(source.getStudentList().stream().map(JsonAdaptedStudent::new).toList());
    }

    /**
     * Converts this Jackson-friendly adapted group object into the model's {@code Group} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted group.
     */
    public Group toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(MISSING_NAME_MESSAGE);
        }
        if (!GroupName.isValidGroupName(name)) {
            throw new IllegalValueException(GroupName.MESSAGE_CONSTRAINTS);
        }
        final Group group = new Group(new GroupName(name));

        for (JsonAdaptedStudent jsonAdaptedStudent : students) {
            addStudent(group, jsonAdaptedStudent);
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

}
