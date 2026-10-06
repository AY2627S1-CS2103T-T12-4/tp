package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GroupNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupName(null));
    }

    @Test
    public void constructor_invalidGroupName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, GroupName.MESSAGE_CONSTRAINTS, () -> new GroupName(""));
    }

    @Test
    public void constructor_extraSpaces_normalizesSpaces() {
        assertEquals("T01 Thursday", new GroupName("  T01   Thursday ").fullName);
    }

    @Test
    public void isValidGroupName() {
        // null group name
        assertThrows(NullPointerException.class, () -> GroupName.isValidGroupName(null));

        // invalid group names
        assertFalse(GroupName.isValidGroupName("")); // empty string
        assertFalse(GroupName.isValidGroupName("   ")); // spaces only
        assertFalse(GroupName.isValidGroupName("--")); // hyphens only
        assertFalse(GroupName.isValidGroupName("T01/T02")); // contains a slash
        assertFalse(GroupName.isValidGroupName("T01_A")); // contains an underscore
        assertFalse(GroupName.isValidGroupName("a".repeat(GroupName.MAX_LENGTH + 1))); // too long

        // valid group names
        assertTrue(GroupName.isValidGroupName("T")); // one character (boundary value)
        assertTrue(GroupName.isValidGroupName("T01")); // letters and digits
        assertTrue(GroupName.isValidGroupName("T01-Thu")); // with a hyphen
        assertTrue(GroupName.isValidGroupName("Tuesday Group 2")); // with spaces
        assertTrue(GroupName.isValidGroupName("  T01  ")); // surrounding spaces are ignored
        assertTrue(GroupName.isValidGroupName("a".repeat(GroupName.MAX_LENGTH))); // maximum length (boundary value)
        assertTrue(GroupName.isValidGroupName(" " + "a".repeat(GroupName.MAX_LENGTH) + " ")); // length after trimming
    }

    @Test
    public void equals() {
        GroupName groupName = new GroupName("T01");

        // same values -> returns true
        assertTrue(groupName.equals(new GroupName("T01")));

        // same object -> returns true
        assertTrue(groupName.equals(groupName));

        // null -> returns false
        assertFalse(groupName.equals(null));

        // different types -> returns false
        assertFalse(groupName.equals(5.0f));

        // different values -> returns false
        assertFalse(groupName.equals(new GroupName("T02")));

        // different case -> returns true
        assertTrue(groupName.equals(new GroupName("t01")));

        // different spacing -> returns true
        assertTrue(new GroupName("T01 Thu").equals(new GroupName("T01   Thu")));
    }

    @Test
    public void hashCode_differentCase_sameHashCode() {
        assertEquals(new GroupName("T01").hashCode(), new GroupName("t01").hashCode());
    }

    @Test
    public void toString_keepsCapitalization() {
        assertEquals("Tue T01", new GroupName("Tue T01").toString());
    }
}
