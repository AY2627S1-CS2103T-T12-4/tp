package seedu.address.model.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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

        // same name in a different case -> returns false
        assertFalse(PS1.equals(copy.withName(new AssignmentName("ps1"))));
    }

    @Test
    public void withName_returnsCopyWithNewNameAndSameRecords() {
        StudentId aliceId = new StudentId("A0123456X");
        Assignment graded = PS1.withSubmissionStatus(aliceId, true).withGrade(aliceId, 87);

        Assignment renamed = graded.withName(new AssignmentName("Problem Set 1"));

        assertEquals(new AssignmentName("Problem Set 1"), renamed.getName());
        assertEquals(graded.getStudentRecords(), renamed.getStudentRecords());
        assertEquals(NAME_PS1, graded.getName());
        assertThrows(NullPointerException.class, () -> graded.withName(null));
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
        assertEquals(Optional.of(90), graded.withGrade(aliceId, 90).getStudentRecord(aliceId).getGrade());
        assertEquals(original, graded.withoutStudentRecord(aliceId));
        assertEquals(original, original.withoutStudentRecord(aliceId));
        assertEquals(AssignmentRecord.notSubmitted(), graded.withSubmissionStatus(aliceId, false)
                .getStudentRecord(aliceId));
        assertThrows(NullPointerException.class, () -> original.getStudentRecord(null));
        assertThrows(NullPointerException.class, () -> original.withoutStudentRecord(null));
        assertThrows(IllegalArgumentException.class, () -> original.withGrade(aliceId, 87));
    }

    @Test
    public void withStudentIdChanged_movesRecordToNewId() {
        StudentId oldId = new StudentId("A0123456X");
        StudentId newId = new StudentId("A0123457X");
        StudentId otherId = new StudentId("A0234567Y");
        Assignment graded = PS1.withSubmissionStatus(oldId, true).withGrade(oldId, 87)
                .withSubmissionStatus(otherId, true);

        Assignment moved = graded.withStudentIdChanged(oldId, newId);

        assertEquals(new AssignmentRecord(true, Optional.of(87)), moved.getStudentRecord(newId));
        assertEquals(AssignmentRecord.notSubmitted(), moved.getStudentRecord(oldId));
        assertEquals(graded.getStudentRecord(otherId), moved.getStudentRecord(otherId));
        assertEquals(2, moved.getStudentRecords().size());
        assertEquals(87, graded.getStudentRecord(oldId).getGrade().orElseThrow());
    }

    @Test
    public void withStudentIdChanged_noRecordToMove_discardsRecordAlreadyUnderNewId() {
        StudentId oldId = new StudentId("A0123456X");
        StudentId newId = new StudentId("A0123457X");
        Assignment stale = PS1.withSubmissionStatus(newId, true);

        assertEquals(PS1, stale.withStudentIdChanged(oldId, newId));
        assertSame(PS1, PS1.withStudentIdChanged(oldId, newId));
        assertSame(stale, stale.withStudentIdChanged(newId, newId));
        assertThrows(NullPointerException.class, () -> PS1.withStudentIdChanged(null, newId));
        assertThrows(NullPointerException.class, () -> PS1.withStudentIdChanged(oldId, null));
    }

    @Test
    public void constructor_defensivelyCopiesRecords() {
        StudentId aliceId = new StudentId("A0123456X");
        Map<StudentId, AssignmentRecord> source = new HashMap<>();
        source.put(aliceId, new AssignmentRecord(true, Optional.of(87)));
        Assignment assignment = new Assignment(NAME_PS1, source);
        source.clear();

        assertTrue(assignment.getStudentRecords().containsKey(aliceId));
        assertThrows(UnsupportedOperationException.class, () -> assignment.getStudentRecords().clear());
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
