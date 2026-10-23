package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.Student;
import seedu.address.testutil.GroupBuilder;
import seedu.address.testutil.StudentBuilder;

public class GroupRecordsCsvExporterTest {
    private static final int WEEKS = 13;
    private static final Student ALICE = new StudentBuilder().withName("Alice Tan").withStudentId("A0123456X")
            .build().withAttendanceRecord(1, "PRESENT").withAttendanceRecord(13, "ABSENT")
            .withParticipationRecord(1, 3).withParticipationRecord(2, 0);
    private static final Student BEN = new StudentBuilder().withName("Ben Lim").withStudentId("A0234567Y").build();

    @TempDir
    public Path temporaryFolder;

    @Test
    public void getExportFile_groupName_namesFileAfterGroupInExportFolder() {
        GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(temporaryFolder.resolve("exports"));

        assertEquals(temporaryFolder.resolve("exports").resolve("T01-records.csv"), exporter.getExportFile(NAME_T01));
        assertEquals("Thursday_1_Lab-records.csv",
                exporter.getExportFile(new GroupName(" Thursday   1 Lab")).getFileName().toString());
        assertNotEquals(exporter.getExportFile(new GroupName("Lab 1")), exporter.getExportFile(new GroupName("Lab-1")));
    }

    @Test
    public void export_group_writesUtf8CsvWithByteOrderMarkAndCrlf() throws IOException {
        Group group = new GroupBuilder().withName(NAME_T01).withStudents(ALICE, BEN).build();
        GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(temporaryFolder.resolve("exports"));

        Path exportFile = exporter.export(group);

        assertEquals(exporter.getExportFile(NAME_T01), exportFile);
        String csv = Files.readString(exportFile, StandardCharsets.UTF_8);
        assertTrue(csv.startsWith("\uFEFFName,Student ID,Week 1 Attendance,"));
        assertTrue(csv.contains("\r\nAlice Tan,A0123456X,Present,"));
        assertTrue(csv.endsWith("\r\n"));
        assertEquals(3, csv.split("\r\n").length);
    }

    @Test
    public void export_sameGroupAgain_replacesEarlierExportWithoutLeavingTemporaryFiles() throws IOException {
        GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(temporaryFolder);
        exporter.export(new GroupBuilder().withName(NAME_T01).withStudents(ALICE, BEN).build());

        Path exportFile = exporter.export(new Group(NAME_T01));

        assertEquals(1, Files.readString(exportFile).split("\r\n").length);
        try (var files = Files.list(temporaryFolder)) {
            assertEquals(List.of(exportFile), files.toList());
        }
    }

    @Test
    public void export_folderIsAFile_throwsIoExceptionAndWritesNothing() throws IOException {
        Path notAFolder = Files.writeString(temporaryFolder.resolve("exports"), "not a folder");
        GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(notAFolder);

        assertThrows(IOException.class, () -> exporter.export(new Group(NAME_T01)));
        assertEquals("not a folder", Files.readString(notAFolder));
    }

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
    public void nullArguments_throwNullPointerException() {
        GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(temporaryFolder);
        assertThrows(NullPointerException.class, () -> new GroupRecordsCsvExporter(null));
        assertThrows(NullPointerException.class, () -> exporter.getExportFile(null));
        assertThrows(NullPointerException.class, () -> exporter.export(null));
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
