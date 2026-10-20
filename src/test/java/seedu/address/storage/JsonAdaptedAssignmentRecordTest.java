package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.AssignmentRecord;
import seedu.address.model.student.StudentId;

public class JsonAdaptedAssignmentRecordTest {
    @Test
    public void getAssignmentName_validAndMissingOrInvalid_values() throws Exception {
        assertEquals(new AssignmentName("PS1"),
                new JsonAdaptedAssignmentRecord("PS1", "A0123456X", true, null).getAssignmentName());
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedAssignmentRecord.MISSING_FIELD_MESSAGE_FORMAT, "assignmentName"),
                () -> new JsonAdaptedAssignmentRecord(null, "A0123456X", true, null).getAssignmentName());
        assertThrows(IllegalValueException.class, AssignmentName.MESSAGE_CONSTRAINTS,
                () -> new JsonAdaptedAssignmentRecord("PS1!", "A0123456X", true, null).getAssignmentName());
    }

    @Test
    public void getStudentId_validAndMissingOrInvalid_values() throws Exception {
        assertEquals(new StudentId("A0123456X"),
                new JsonAdaptedAssignmentRecord("PS1", "a0123456x", true, null).getStudentId());
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedAssignmentRecord.MISSING_FIELD_MESSAGE_FORMAT, "studentId"),
                () -> new JsonAdaptedAssignmentRecord("PS1", null, true, null).getStudentId());
        assertThrows(IllegalValueException.class, StudentId.MESSAGE_CONSTRAINTS,
                () -> new JsonAdaptedAssignmentRecord("PS1", "ID!", true, null).getStudentId());
    }

    @Test
    public void toModelType_validStatusAndGrade_returnsRecord() throws Exception {
        assertEquals(new AssignmentRecord(true, Optional.of(87)),
                new JsonAdaptedAssignmentRecord("PS1", "A0123456X", true, 87).toModelType());
        assertEquals(AssignmentRecord.notSubmitted(),
                new JsonAdaptedAssignmentRecord("PS1", "A0123456X", false, null).toModelType());
    }

    @Test
    public void toModelType_missingStatusOrInvalidGrade_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedAssignmentRecord.MISSING_FIELD_MESSAGE_FORMAT, "submitted"),
                () -> new JsonAdaptedAssignmentRecord("PS1", "A0123456X", null, null).toModelType());
        assertThrows(IllegalValueException.class, AssignmentRecord.MESSAGE_INVALID_GRADE,
                () -> new JsonAdaptedAssignmentRecord("PS1", "A0123456X", true, 101).toModelType());
        assertThrows(IllegalValueException.class, "An unsubmitted assignment cannot have a grade.",
                () -> new JsonAdaptedAssignmentRecord("PS1", "A0123456X", false, 87).toModelType());
    }
}
