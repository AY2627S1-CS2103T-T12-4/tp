package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentId(null));
    }

    @Test
    public void constructor_invalidStudentId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, StudentId.MESSAGE_CONSTRAINTS, () -> new StudentId("ABC"));
    }

    @Test
    public void constructor_lowerCaseWithSpaces_storesTrimmedUpperCase() {
        assertEquals("A0123456X", new StudentId("  a0123456x ").value);
    }

    @Test
    public void isValidStudentId() {
        // null student ID
        assertThrows(NullPointerException.class, () -> StudentId.isValidStudentId(null));

        // invalid student IDs
        assertFalse(StudentId.isValidStudentId("")); // empty string
        assertFalse(StudentId.isValidStudentId("  ")); // spaces only
        assertFalse(StudentId.isValidStudentId("ABCDE")); // no digit
        assertFalse(StudentId.isValidStudentId("A012 3456X")); // internal space
        assertFalse(StudentId.isValidStudentId("A0123456-X")); // symbol
        assertFalse(StudentId.isValidStudentId("A" + "1".repeat(StudentId.MAX_LENGTH))); // too long

        // valid student IDs
        assertTrue(StudentId.isValidStudentId("1")); // one digit (boundary value)
        assertTrue(StudentId.isValidStudentId("A0123456X")); // typical NUS student ID
        assertTrue(StudentId.isValidStudentId("e1234567")); // lowercase letters
        assertTrue(StudentId.isValidStudentId(" A0123456X ")); // surrounding spaces are ignored
        assertTrue(StudentId.isValidStudentId("1".repeat(StudentId.MAX_LENGTH))); // maximum length (boundary value)
    }

    @Test
    public void equals() {
        StudentId studentId = new StudentId("A0123456X");

        // same values -> returns true
        assertTrue(studentId.equals(new StudentId("A0123456X")));

        // same object -> returns true
        assertTrue(studentId.equals(studentId));

        // null -> returns false
        assertFalse(studentId.equals(null));

        // different types -> returns false
        assertFalse(studentId.equals(5.0f));

        // different values -> returns false
        assertFalse(studentId.equals(new StudentId("A0234567Y")));

        // different case -> returns true
        assertTrue(studentId.equals(new StudentId("a0123456x")));
    }
}
