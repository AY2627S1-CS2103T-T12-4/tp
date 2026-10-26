package seedu.address.model.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class AssignmentNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AssignmentName(null));
    }

    @Test
    public void constructor_invalidAssignmentName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, AssignmentName.MESSAGE_CONSTRAINTS, () ->
                new AssignmentName(""));
    }

    @Test
    public void constructor_extraSpaces_normalizesSpacesAndKeepsCapitalization() {
        assertEquals("Problem Set 1", new AssignmentName("  Problem   Set\t1 ").fullName);
    }

    @Test
    public void isValidAssignmentName() {
        // null assignment name
        assertThrows(NullPointerException.class, () -> AssignmentName.isValidAssignmentName(null));

        // invalid assignment names
        assertFalse(AssignmentName.isValidAssignmentName("")); // empty string
        assertFalse(AssignmentName.isValidAssignmentName("   ")); // spaces only
        assertFalse(AssignmentName.isValidAssignmentName("--")); // hyphens only
        assertFalse(AssignmentName.isValidAssignmentName("PS1/PS2")); // contains a slash
        assertFalse(AssignmentName.isValidAssignmentName("PS1_A")); // contains an underscore
        assertFalse(AssignmentName.isValidAssignmentName("PS1:")); // contains punctuation
        assertFalse(AssignmentName.isValidAssignmentName("a".repeat(AssignmentName.MAX_LENGTH + 1))); // too long

        // valid assignment names
        assertTrue(AssignmentName.isValidAssignmentName("P")); // one character (boundary value)
        assertTrue(AssignmentName.isValidAssignmentName("PS1")); // letters and digits
        assertTrue(AssignmentName.isValidAssignmentName("Mid-term")); // with a hyphen
        assertTrue(AssignmentName.isValidAssignmentName("Problem Set 1")); // with spaces
        assertTrue(AssignmentName.isValidAssignmentName("  PS1  ")); // surrounding spaces are ignored
        assertTrue(AssignmentName.isValidAssignmentName("a".repeat(AssignmentName.MAX_LENGTH))); // maximum length
        assertTrue(AssignmentName.isValidAssignmentName(" " + "a".repeat(AssignmentName.MAX_LENGTH) + " "));
    }

    @Test
    public void equals() {
        AssignmentName assignmentName = new AssignmentName("PS1");

        // same values -> returns true
        assertTrue(assignmentName.equals(new AssignmentName("PS1")));

        // same object -> returns true
        assertTrue(assignmentName.equals(assignmentName));

        // null -> returns false
        assertFalse(assignmentName.equals(null));

        // different type -> returns false
        assertFalse(assignmentName.equals("PS1"));

        // different name -> returns false
        assertFalse(assignmentName.equals(new AssignmentName("PS2")));

        // different case and extra spaces -> returns true, with the same hash code
        AssignmentName sameName = new AssignmentName(" ps1 ");
        assertTrue(assignmentName.equals(sameName));
        assertEquals(assignmentName.hashCode(), sameName.hashCode());
        assertNotEquals(assignmentName.fullName, sameName.fullName);
    }

    @Test
    public void hashCode_unicodeCaseVariant_preservesHashSetMembership() {
        AssignmentName ascii = new AssignmentName("PSI");
        AssignmentName dotlessI = new AssignmentName("psı");
        assertEquals(ascii, dotlessI);
        assertEquals(ascii.hashCode(), dotlessI.hashCode());

        Set<AssignmentName> assignmentNames = new HashSet<>(Set.of(ascii));
        assertTrue(assignmentNames.contains(dotlessI));
        assertFalse(assignmentNames.add(dotlessI));
    }

    @Test
    public void toString_returnsDisplayName() {
        assertEquals("Problem Set 1", new AssignmentName(" Problem  Set 1").toString());
    }
}
