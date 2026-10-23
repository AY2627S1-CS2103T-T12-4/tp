package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.group.Group;
import seedu.address.model.student.Student;
import seedu.address.testutil.GroupBuilder;
import seedu.address.testutil.StudentBuilder;

public class GroupRecordsCsvExporterTest {
    private static final int WEEKS = 13;
    private static final Student ALICE = new StudentBuilder().withName("Alice Tan").withStudentId("A0123456X")
            .build().withAttendanceRecord(1, "PRESENT").withAttendanceRecord(13, "ABSENT")
            .withParticipationRecord(1, 3).withParticipationRecord(2, 0);
    private static final Student BEN = new StudentBuilder().withName("Ben Lim").withStudentId("A0234567Y").build();

    @Test
    public void toRows_header_namesEveryWeekOfBothRecords() {
        List<String> header = GroupRecordsCsvExporter.toRows(new Group(NAME_T01)).getFirst();

        assertEquals(2 + 2 * WEEKS, header.size());
        assertEquals(List.of("Name", "Student ID", "Week 1 Attendance"), header.subList(0, 3));
        assertEquals("Week 13 Attendance", header.get(1 + WEEKS));
        assertEquals("Week 1 Participation", header.get(2 + WEEKS));
        assertEquals("Week 13 Participation", header.getLast());
    }

    @Test
    public void toRows_students_oneRowEachInRosterOrderWithEmptyCellsForMissingRecords() {
        Group group = new GroupBuilder().withName(NAME_T01).withStudents(ALICE, BEN).build();

        List<List<String>> rows = GroupRecordsCsvExporter.toRows(group);

        assertEquals(3, rows.size());
        assertEquals(expectedRow("Alice Tan", "A0123456X", weeksWith(1, "Present", 13, "Absent"),
                weeksWith(1, "3", 2, "0")), rows.get(1));
        assertEquals(expectedRow("Ben Lim", "A0234567Y", noRecords(), noRecords()), rows.get(2));
    }

    @Test
    public void toRows_emptyGroup_headerOnly() {
        assertEquals(1, GroupRecordsCsvExporter.toRows(new Group(NAME_T01)).size());
    }

    @Test
    public void toRows_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> GroupRecordsCsvExporter.toRows(null));
    }

    private static List<String> expectedRow(String name, String studentId, List<String> attendance,
            List<String> participation) {
        List<String> row = new ArrayList<>(List.of(name, studentId));
        row.addAll(attendance);
        row.addAll(participation);
        return row;
    }

    private static List<String> noRecords() {
        return new ArrayList<>(Collections.nCopies(WEEKS, ""));
    }

    /**
     * Returns the cells of the 13 weeks, empty except {@code first} in {@code firstWeek} and {@code second} in
     * {@code secondWeek}.
     */
    private static List<String> weeksWith(int firstWeek, String first, int secondWeek, String second) {
        List<String> weeks = noRecords();
        weeks.set(firstWeek - 1, first);
        weeks.set(secondWeek - 1, second);
        return weeks;
    }
}
