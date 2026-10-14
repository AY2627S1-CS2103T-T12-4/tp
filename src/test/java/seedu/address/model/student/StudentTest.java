package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.StudentBuilder;

public class StudentTest {

    private static final StudentName NAME_ALICE = ALICE.getName();
    private static final StudentName NAME_BEN = BEN.getName();
    private static final StudentId ID_ALICE = ALICE.getStudentId();
    private static final StudentId ID_BEN = BEN.getStudentId();

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Student(null, ID_ALICE));
        assertThrows(NullPointerException.class, () -> new Student(NAME_ALICE, null));
        assertThrows(NullPointerException.class, () -> new Student(
                NAME_ALICE, ID_ALICE, null, new WeeklyRecord<>()));
        assertThrows(NullPointerException.class, () -> new Student(
                NAME_ALICE, ID_ALICE, new WeeklyRecord<>(), null));
    }

    @Test
    public void withWeeklyRecord_addsAndReplacesWithoutChangingOriginal() {
        Student withAttendance = ALICE.withAttendanceRecord(1, "PRESENT");
        Student withBothRecords = withAttendance.withParticipationRecord(1, 4);
        Student replaced = withBothRecords.withAttendanceRecord(1, "EXCUSED");

        assertTrue(ALICE.getAttendanceRecord(1).isEmpty());
        assertTrue(ALICE.getParticipationRecord(1).isEmpty());
        assertEquals("PRESENT", withBothRecords.getAttendanceRecord(1).orElseThrow());
        assertEquals(4, withBothRecords.getParticipationRecord(1).orElseThrow());
        assertEquals("EXCUSED", replaced.getAttendanceRecord(1).orElseThrow());
        assertEquals(4, replaced.getParticipationRecord(1).orElseThrow());
        assertThrows(IllegalArgumentException.class, () -> ALICE.getAttendanceRecord(14));
        assertThrows(NullPointerException.class, () -> ALICE.withAttendanceRecord(1, null));
    }

    @Test
    public void builder_copyPreservesWeeklyRecords() {
        Student recordedStudent = ALICE.withAttendanceRecord(2, "ABSENT").withParticipationRecord(2, 3);
        assertEquals(recordedStudent, new StudentBuilder(recordedStudent).build());
    }

    @Test
    public void isSameStudent() {
        // same object -> returns true
        assertTrue(ALICE.isSameStudent(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameStudent(null));

        // same student ID, different name -> returns true
        assertTrue(ALICE.isSameStudent(new Student(NAME_BEN, ID_ALICE)));

        // same student ID in a different case -> returns true
        assertTrue(ALICE.isSameStudent(new Student(NAME_ALICE, new StudentId("a0123456x"))));

        // same name, different student ID -> returns false
        assertFalse(ALICE.isSameStudent(new Student(NAME_ALICE, ID_BEN)));
    }

    @Test
    public void equals() {
        // same values -> returns true
        assertTrue(ALICE.equals(new Student(NAME_ALICE, ID_ALICE)));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different name -> returns false
        assertFalse(ALICE.equals(new Student(NAME_BEN, ID_ALICE)));

        // different student ID -> returns false
        assertFalse(ALICE.equals(new Student(NAME_ALICE, ID_BEN)));

        // different weekly records -> returns false
        assertFalse(ALICE.equals(ALICE.withAttendanceRecord(1, "PRESENT")));
    }

    @Test
    public void hashCode_sameValues_sameHashCode() {
        assertEquals(ALICE.hashCode(), new Student(NAME_ALICE, ID_ALICE).hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Student.class.getCanonicalName() + "{name=" + NAME_ALICE + ", studentId=" + ID_ALICE
                + ", attendanceRecords=" + ALICE.getAttendanceRecords() + ", participationRecords="
                + ALICE.getParticipationRecords() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
