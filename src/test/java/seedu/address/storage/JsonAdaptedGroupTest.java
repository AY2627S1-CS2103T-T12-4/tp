package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedGroup.MESSAGE_DUPLICATE_STUDENT;
import static seedu.address.storage.JsonAdaptedGroup.MESSAGE_EMPTY_STUDENT;
import static seedu.address.storage.JsonAdaptedGroup.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.StudentId;
import seedu.address.testutil.GroupBuilder;
import seedu.address.testutil.TypicalGroups;

public class JsonAdaptedGroupTest {
    private static final String INVALID_NAME = "T01!";

    private static final Group T01 = TypicalGroups.getT01();
    private static final String VALID_NAME = T01.getName().toString();
    private static final List<JsonAdaptedStudent> VALID_STUDENTS = T01.getStudentList().stream()
            .map(JsonAdaptedStudent::new)
            .toList();

    @Test
    public void toModelType_validGroupDetails_returnsGroup() throws Exception {
        JsonAdaptedGroup group = new JsonAdaptedGroup(T01);
        assertEquals(T01, group.toModelType());
    }

    @Test
    public void toModelType_nullStudents_returnsEmptyGroup() throws Exception {
        JsonAdaptedGroup group = new JsonAdaptedGroup(VALID_NAME, null);
        assertEquals(new GroupBuilder().withName(VALID_NAME).build(), group.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedGroup group = new JsonAdaptedGroup(INVALID_NAME, VALID_STUDENTS);
        assertThrows(IllegalValueException.class, GroupName.MESSAGE_CONSTRAINTS, group::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedGroup group = new JsonAdaptedGroup(null, VALID_STUDENTS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "name");
        assertThrows(IllegalValueException.class, expectedMessage, group::toModelType);
    }

    @Test
    public void toModelType_invalidStudent_throwsIllegalValueException() {
        List<JsonAdaptedStudent> students = new ArrayList<>(VALID_STUDENTS);
        students.add(new JsonAdaptedStudent("Fiona Lee", null));
        JsonAdaptedGroup group = new JsonAdaptedGroup(VALID_NAME, students);
        String expectedMessage = String.format(JsonAdaptedStudent.MISSING_FIELD_MESSAGE_FORMAT, "studentId");
        assertThrows(IllegalValueException.class, expectedMessage, group::toModelType);
    }

    @Test
    public void toModelType_nullStudent_throwsIllegalValueException() {
        List<JsonAdaptedStudent> students = new ArrayList<>(VALID_STUDENTS);
        students.add(null);
        JsonAdaptedGroup group = new JsonAdaptedGroup(VALID_NAME, students);
        String expectedMessage = String.format(MESSAGE_EMPTY_STUDENT, VALID_NAME);
        assertThrows(IllegalValueException.class, expectedMessage, group::toModelType);
    }

    @Test
    public void toModelType_duplicateStudentIds_throwsIllegalValueException() {
        String sameIdInLowercase = ALICE.getStudentId().toString().toLowerCase();
        JsonAdaptedStudent duplicate = new JsonAdaptedStudent("Alice Ng", sameIdInLowercase);
        JsonAdaptedGroup group = new JsonAdaptedGroup(VALID_NAME,
                Arrays.asList(new JsonAdaptedStudent(ALICE), duplicate));
        String expectedMessage = String.format(MESSAGE_DUPLICATE_STUDENT, VALID_NAME,
                new StudentId(sameIdInLowercase));
        assertThrows(IllegalValueException.class, expectedMessage, group::toModelType);
    }
}
