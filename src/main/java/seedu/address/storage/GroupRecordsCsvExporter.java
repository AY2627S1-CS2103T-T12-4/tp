package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import seedu.address.model.group.Group;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.Student;
import seedu.address.model.student.WeeklyRecord;

/**
 * Lays out the attendance and participation of one tutorial group as a table for a CSV file.
 * There is one row per student, in roster order, with their name, student ID, and attendance and participation
 * for every week of the term. A week without a record is left empty.
 */
public class GroupRecordsCsvExporter {
    static final String HEADER_NAME = "Name";
    static final String HEADER_STUDENT_ID = "Student ID";
    static final String HEADER_ATTENDANCE_FORMAT = "Week %d Attendance";
    static final String HEADER_PARTICIPATION_FORMAT = "Week %d Participation";

    /**
     * Returns the rows of the CSV file for {@code group}: a header row, then one row per student.
     */
    static List<List<String>> toRows(Group group) {
        requireNonNull(group);
        List<List<String>> rows = new ArrayList<>();
        rows.add(createHeader());
        group.getStudentList().forEach(student -> rows.add(createRow(student)));
        return rows;
    }

    private static List<String> createHeader() {
        List<String> header = new ArrayList<>(List.of(HEADER_NAME, HEADER_STUDENT_ID));
        weeks().forEach(week -> header.add(String.format(HEADER_ATTENDANCE_FORMAT, week)));
        weeks().forEach(week -> header.add(String.format(HEADER_PARTICIPATION_FORMAT, week)));
        return header;
    }

    private static List<String> createRow(Student student) {
        List<String> row = new ArrayList<>(List.of(student.getName().toString(), student.getStudentId().toString()));
        weeks().forEach(week -> row.add(student.getAttendanceRecord(week)
                .map(AttendanceStatus::formatStoredValue).orElse("")));
        weeks().forEach(week -> row.add(student.getParticipationRecord(week)
                .map(String::valueOf).orElse("")));
        return row;
    }

    private static IntStream weeks() {
        return IntStream.rangeClosed(WeeklyRecord.MIN_WEEK, WeeklyRecord.MAX_WEEK);
    }
}
