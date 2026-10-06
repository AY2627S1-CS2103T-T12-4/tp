package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentTest {

    private static final StudentName NAME_ALICE = new StudentName("Alice Tan");
    private static final StudentName NAME_BEN = new StudentName("Ben Lim");
    private static final StudentId ID_ALICE = new StudentId("A0123456X");
    private static final StudentId ID_BEN = new StudentId("A0234567Y");

    private final Student alice = new Student(NAME_ALICE, ID_ALICE);

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Student(null, ID_ALICE));
        assertThrows(NullPointerException.class, () -> new Student(NAME_ALICE, null));
    }

    @Test
    public void isSameStudent() {
        // same object -> returns true
        assertTrue(alice.isSameStudent(alice));

        // null -> returns false
        assertFalse(alice.isSameStudent(null));

        // same student ID, different name -> returns true
        assertTrue(alice.isSameStudent(new Student(NAME_BEN, ID_ALICE)));

        // same student ID in a different case -> returns true
        assertTrue(alice.isSameStudent(new Student(NAME_ALICE, new StudentId("a0123456x"))));

        // same name, different student ID -> returns false
        assertFalse(alice.isSameStudent(new Student(NAME_ALICE, ID_BEN)));
    }

    @Test
    public void equals() {
        // same values -> returns true
        assertTrue(alice.equals(new Student(NAME_ALICE, ID_ALICE)));

        // same object -> returns true
        assertTrue(alice.equals(alice));

        // null -> returns false
        assertFalse(alice.equals(null));

        // different type -> returns false
        assertFalse(alice.equals(5));

        // different name -> returns false
        assertFalse(alice.equals(new Student(NAME_BEN, ID_ALICE)));

        // different student ID -> returns false
        assertFalse(alice.equals(new Student(NAME_ALICE, ID_BEN)));
    }

    @Test
    public void hashCode_sameValues_sameHashCode() {
        assertEquals(alice.hashCode(), new Student(NAME_ALICE, ID_ALICE).hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Student.class.getCanonicalName() + "{name=" + NAME_ALICE + ", studentId=" + ID_ALICE + "}";
        assertEquals(expected, alice.toString());
    }
}
