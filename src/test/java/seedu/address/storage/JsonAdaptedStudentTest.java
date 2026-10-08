package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedStudent.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.BEN;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;

public class JsonAdaptedStudentTest {
    private static final String INVALID_NAME = "B3n";
    private static final String INVALID_STUDENT_ID = "A012-3456X";

    private static final String VALID_NAME = BEN.getName().toString();
    private static final String VALID_STUDENT_ID = BEN.getStudentId().toString();

    @Test
    public void toModelType_validStudentDetails_returnsStudent() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(BEN);
        assertEquals(BEN, student.toModelType());
    }

    @Test
    public void toModelType_unnormalizedDetails_returnsNormalizedStudent() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent("  Ben   Lim ", " a0234567y ");
        assertEquals(BEN, student.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(INVALID_NAME, VALID_STUDENT_ID);
        assertThrows(IllegalValueException.class, StudentName.MESSAGE_CONSTRAINTS, student::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(null, VALID_STUDENT_ID);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "name");
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidStudentId_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, INVALID_STUDENT_ID);
        assertThrows(IllegalValueException.class, StudentId.MESSAGE_CONSTRAINTS, student::toModelType);
    }

    @Test
    public void toModelType_nullStudentId_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "studentId");
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }
}
