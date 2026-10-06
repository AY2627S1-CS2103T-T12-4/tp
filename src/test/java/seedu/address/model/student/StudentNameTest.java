package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentName(null));
    }

    @Test
    public void constructor_invalidStudentName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, StudentName.MESSAGE_CONSTRAINTS, () -> new StudentName(" "));
    }

    @Test
    public void constructor_extraSpaces_normalizesSpacesAndKeepsCapitalization() {
        assertEquals("Alice Tan", new StudentName("  Alice    Tan ").fullName);
    }

    @Test
    public void isValidStudentName() {
        // null name
        assertThrows(NullPointerException.class, () -> StudentName.isValidStudentName(null));

        // invalid names
        assertFalse(StudentName.isValidStudentName("")); // empty string
        assertFalse(StudentName.isValidStudentName("   ")); // spaces only
        assertFalse(StudentName.isValidStudentName("'-")); // no letters
        assertFalse(StudentName.isValidStudentName("Alice 2")); // contains a digit
        assertFalse(StudentName.isValidStudentName("Alice*")); // contains a symbol
        assertFalse(StudentName.isValidStudentName("a".repeat(StudentName.MAX_LENGTH + 1))); // too long

        // valid names
        assertTrue(StudentName.isValidStudentName("A")); // one letter (boundary value)
        assertTrue(StudentName.isValidStudentName("Alice Tan")); // letters and a space
        assertTrue(StudentName.isValidStudentName("Mary-Jane O'Neil")); // hyphen and apostrophe
        assertTrue(StudentName.isValidStudentName("Mary O’Neil")); // curly apostrophe
        assertTrue(StudentName.isValidStudentName("José Ng")); // accented letter
        assertTrue(StudentName.isValidStudentName("a".repeat(StudentName.MAX_LENGTH))); // maximum length (boundary)
    }

    @Test
    public void equals() {
        StudentName name = new StudentName("Alice Tan");

        // same values -> returns true
        assertTrue(name.equals(new StudentName("Alice Tan")));

        // same values after normalizing spaces -> returns true
        assertTrue(name.equals(new StudentName(" Alice  Tan ")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new StudentName("Ben Lim")));
    }
}
