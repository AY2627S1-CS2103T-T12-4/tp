package seedu.address.model.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;
import static seedu.address.testutil.TypicalAssignments.PS1;
import static seedu.address.testutil.TypicalAssignments.PS2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.student.StudentId;

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

    @Test
    public void studentRecords_immutableCopiesReplaceRecordsById() {
        StudentId aliceId = new StudentId("A0123456X");
        Assignment original = new Assignment(NAME_PS1);
        Assignment submitted = original.withSubmissionStatus(aliceId, true);
        Assignment graded = submitted.withGrade(aliceId, 87);

        assertEquals(AssignmentRecord.notSubmitted(), original.getStudentRecord(aliceId));
        assertEquals(new AssignmentRecord(true, Optional.empty()), submitted.getStudentRecord(aliceId));
        assertEquals(Optional.of(87), graded.getStudentRecord(aliceId).getGrade());
        assertEquals(Optional.of(87), graded.withGrade(aliceId, 90).getStudentRecord(aliceId).getGrade());
        assertEquals(AssignmentRecord.notSubmitted(), graded.withSubmissionStatus(aliceId, false)
                .getStudentRecord(aliceId));
        assertThrows(NullPointerException.class, () -> original.getStudentRecord(null));
        assertThrows(IllegalArgumentException.class, () -> original.withGrade(aliceId, 87));
    }

    @Test
    public void constructor_defensivelyCopiesRecords() {
        StudentId aliceId = new StudentId("A0123456X");
        Map<StudentId, AssignmentRecord> source = new HashMap<>();
        source.put(aliceId, new AssignmentRecord(true, Optional.of(87)));
        Assignment assignment = new Assignment(NAME_PS1, source);
        source.clear();

        assertTrue(assignment.getStudentRecords().containsKey(aliceId));
        assertThrows(UnsupportedOperationException.class,
                () -> assignment.getStudentRecords().clear());
        assertThrows(NullPointerException.class, () -> new Assignment(NAME_PS1, null));
        Map<StudentId, AssignmentRecord> invalidRecords = new HashMap<>();
        invalidRecords.put(null, AssignmentRecord.notSubmitted());
        assertThrows(NullPointerException.class, () -> new Assignment(NAME_PS1, invalidRecords));
        invalidRecords.clear();
        invalidRecords.put(aliceId, null);
        assertThrows(NullPointerException.class, () -> new Assignment(NAME_PS1, invalidRecords));
    }

    @Test
    public void equality_includesRecordsButUniquenessUsesName() {
        StudentId aliceId = new StudentId("A0123456X");
        Assignment submitted = PS1.withSubmissionStatus(aliceId, true);

        assertNotEquals(PS1, submitted);
        assertTrue(PS1.isSameAssignment(submitted));
    }
}
