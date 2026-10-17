package seedu.address.model.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;
import static seedu.address.testutil.TypicalAssignments.PS1;
import static seedu.address.testutil.TypicalAssignments.PS2;

import org.junit.jupiter.api.Test;

public class AssignmentTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Assignment(null));
    }

    @Test
    public void getName_returnsName() {
        assertEquals(NAME_PS1, PS1.getName());
    }

    @Test
    public void isSameAssignment() {
        // same object -> returns true
        assertTrue(PS1.isSameAssignment(PS1));

        // null -> returns false
        assertFalse(PS1.isSameAssignment(null));

        // same name in a different case -> returns true
        assertTrue(PS1.isSameAssignment(new Assignment(new AssignmentName("ps1"))));

        // different name -> returns false
        assertFalse(PS1.isSameAssignment(PS2));
    }

    @Test
    public void equals() {
        Assignment copy = new Assignment(new AssignmentName("PS1"));

        // same values -> returns true
        assertTrue(PS1.equals(copy));
        assertEquals(PS1.hashCode(), copy.hashCode());

        // same object -> returns true
        assertTrue(PS1.equals(PS1));

        // null -> returns false
        assertFalse(PS1.equals(null));

        // different type -> returns false
        assertFalse(PS1.equals("PS1"));

        // different name -> returns false
        assertFalse(PS1.equals(PS2));
    }

    @Test
    public void toString_containsName() {
        assertTrue(PS1.toString().contains("PS1"));
    }
}
