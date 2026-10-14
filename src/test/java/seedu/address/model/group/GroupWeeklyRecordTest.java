package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.student.Student;
import seedu.address.model.student.WeeklyRecordEntry;
import seedu.address.testutil.TypicalGroups;
import seedu.address.testutil.TypicalStudents;

public class GroupWeeklyRecordTest {
    @Test
    public void getWeeklyRecordEntries_allStudentsInOrderAndShowsMissingRecords() {
        Group group = new Group(TypicalGroups.NAME_T01);
        Student alice = TypicalStudents.ALICE.withAttendanceRecord(2, "PRESENT");
        group.addStudent(alice);
        group.addStudent(TypicalStudents.BEN);
        group.addStudent(TypicalStudents.CARL.withAttendanceRecord(2, "ABSENT"));

        List<WeeklyRecordEntry<String>> entries = group.getWeeklyRecordEntries(2, Student::getAttendanceRecords);

        assertEquals(List.of(alice, TypicalStudents.BEN, group.getStudentList().get(2)),
                entries.stream().map(WeeklyRecordEntry::getStudent).toList());
        assertEquals("PRESENT", entries.get(0).formatValueOrNotRecorded(String::toString));
        assertEquals("Not recorded", entries.get(1).formatValueOrNotRecorded(String::toString));
        assertEquals("ABSENT", entries.get(2).formatValueOrNotRecorded(String::toString));
        assertThrows(UnsupportedOperationException.class, () -> entries.clear());
    }

    @Test
    public void getWeeklyRecordEntries_emptyGroup_returnsNoRows() {
        Group group = new Group(TypicalGroups.NAME_T01);
        assertEquals(List.of(), group.getWeeklyRecordEntries(1, Student::getParticipationRecords));
    }

    @Test
    public void getWeeklyRecordEntries_invalidArguments_throwsException() {
        Group group = new Group(TypicalGroups.NAME_T01);
        group.addStudent(TypicalStudents.ALICE);

        assertThrows(IllegalArgumentException.class, () ->
                group.getWeeklyRecordEntries(14, Student::getAttendanceRecords));
        assertThrows(NullPointerException.class, () -> group.getWeeklyRecordEntries(1, null));
        assertThrows(NullPointerException.class, () -> group.getWeeklyRecordEntries(1, student -> null));
    }
}
