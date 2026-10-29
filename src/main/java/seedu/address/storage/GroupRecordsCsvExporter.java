package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.CsvUtil;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentRecord;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.Student;
import seedu.address.model.student.WeeklyRecord;

/**
 * Exports the records of one tutorial group to a CSV file in an exports folder.
 * The file has one row per student, in roster order, with their name, student ID, attendance and participation for
 * every week of the term, and submission status and grade for every assignment of the group, in creation order.
 * A week or grade without a record is left empty.
 */
public class GroupRecordsCsvExporter {
    /** Ending of every export's file name, after the group name. */
    public static final String FILE_NAME_SUFFIX = "-records.csv";

    static final String HEADER_NAME = "Name";
    static final String HEADER_STUDENT_ID = "Student ID";
    static final String HEADER_ATTENDANCE_FORMAT = "Week %d Attendance";
    static final String HEADER_PARTICIPATION_FORMAT = "Week %d Participation";
    static final String HEADER_SUBMITTED_FORMAT = "%s Submitted";
    static final String HEADER_GRADE_FORMAT = "%s Grade";
    static final String SUBMITTED_YES = "Yes";
    static final String SUBMITTED_NO = "No";
    /** Marks the file as UTF-8, so that spreadsheets show accented names correctly. */
    static final String BYTE_ORDER_MARK = "\uFEFF";

    private static final Logger logger = LogsCenter.getLogger(GroupRecordsCsvExporter.class);

    private final Path exportFolder;

    /**
     * Creates an exporter that writes its files to {@code exportFolder}, creating the folder when needed.
     */
    public GroupRecordsCsvExporter(Path exportFolder) {
        requireNonNull(exportFolder);
        this.exportFolder = exportFolder.toAbsolutePath().normalize();
    }

    /**
     * Returns the file that an export of the group named {@code groupName} writes, such as
     * {@code T01-records.csv}. Spaces in the name become underscores, so the file name needs no quoting.
     * Group names cannot contain underscores, so {@code Lab 1} and {@code Lab-1} get different files.
     * Names that differ only in Unicode normalization can still map to the same file on macOS.
     */
    public Path getExportFile(GroupName groupName) {
        requireNonNull(groupName);
        return exportFolder.resolve(groupName.toString().replace(' ', '_') + FILE_NAME_SUFFIX);
    }

    /**
     * Writes the records of {@code group} to its export file, replacing an earlier export of the same group.
     * The file is written completely before it replaces the earlier one, so a failed export leaves no partial file.
     *
     * @return The absolute path of the file written.
     * @throws IOException if the folder or the file cannot be written.
     */
    public Path export(Group group) throws IOException {
        requireNonNull(group);
        Path exportFile = getExportFile(group.getName());
        String csv = BYTE_ORDER_MARK + CsvUtil.formatTable(toRows(group));

        Files.createDirectories(exportFolder);
        // Files.createTempFile would make the file readable by its owner only, and the move would keep that.
        // A plain file gets the same permissions as any other file the user creates in the folder.
        Path temporaryFile = exportFolder.resolve("." + exportFile.getFileName() + ".tmp");
        try {
            Files.writeString(temporaryFile, csv, StandardCharsets.UTF_8);
            Files.move(temporaryFile, exportFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
        logger.info("Exported " + group.getStudentList().size() + " students and "
                + group.getAssignmentList().size() + " assignments of " + group.getName() + " to " + exportFile);
        return exportFile;
    }

    /**
     * Returns the rows of the CSV file for {@code group}: a header row, then one row per student.
     */
    static List<List<String>> toRows(Group group) {
        requireNonNull(group);
        List<List<String>> rows = new ArrayList<>();
        List<Assignment> assignments = group.getAssignmentList();
        rows.add(createHeader(assignments));
        group.getStudentList().forEach(student -> rows.add(createRow(student, assignments)));
        return rows;
    }

    private static List<String> createHeader(List<Assignment> assignments) {
        List<String> header = new ArrayList<>(List.of(HEADER_NAME, HEADER_STUDENT_ID));
        weeks().forEach(week -> header.add(String.format(HEADER_ATTENDANCE_FORMAT, week)));
        weeks().forEach(week -> header.add(String.format(HEADER_PARTICIPATION_FORMAT, week)));
        for (Assignment assignment : assignments) {
            header.add(String.format(HEADER_SUBMITTED_FORMAT, assignment.getName()));
            header.add(String.format(HEADER_GRADE_FORMAT, assignment.getName()));
        }
        return header;
    }

    private static List<String> createRow(Student student, List<Assignment> assignments) {
        List<String> row = new ArrayList<>(List.of(student.getName().toString(), student.getStudentId().toString()));
        weeks().forEach(week -> row.add(student.getAttendanceRecord(week)
                .map(AttendanceStatus::formatStoredValue).orElse("")));
        weeks().forEach(week -> row.add(student.getParticipationRecord(week)
                .map(String::valueOf).orElse("")));
        for (Assignment assignment : assignments) {
            AssignmentRecord record = assignment.getStudentRecord(student.getStudentId());
            row.add(record.isSubmitted() ? SUBMITTED_YES : SUBMITTED_NO);
            row.add(record.getGrade().map(String::valueOf).orElse(""));
        }
        return row;
    }

    /**
     * Returns true if {@code other} is an exporter that writes to the same folder.
     */
    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof GroupRecordsCsvExporter otherExporter
                && exportFolder.equals(otherExporter.exportFolder);
    }

    @Override
    public int hashCode() {
        return exportFolder.hashCode();
    }

    private static IntStream weeks() {
        return IntStream.rangeClosed(WeeklyRecord.MIN_WEEK, WeeklyRecord.MAX_WEEK);
    }
}
