package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Clears the attendance of one or more students in the active group for a teaching week, so that the week shows
 * Not recorded. This corrects a mark entered for the wrong student or week.
 */
public class AttendanceClearCommand extends WeeklyRecordClearCommand {
    public static final String MESSAGE_USAGE = "attendance clear id/STUDENT_ID [id/STUDENT_ID]... w/WEEK";
    public static final String MESSAGE_CLEARED = "Cleared %s's attendance for Week %d: %s to Not recorded.";
    public static final String MESSAGE_NOT_RECORDED = "%s has no attendance recorded for Week %d. Nothing changed.";

    /**
     * Creates a command to clear the given student's attendance for a valid teaching week.
     */
    public AttendanceClearCommand(StudentId studentId, int week) {
        this(List.of(requireNonNull(studentId)), week);
    }

    /**
     * Creates a command to clear the given students' attendance for a valid teaching week.
     * The list must not be empty.
     */
    public AttendanceClearCommand(List<StudentId> studentIds, int week) {
        super(studentIds, week);
    }

    @Override
    protected Optional<String> getRecordedValue(Student student, int week) {
        return student.getAttendanceRecord(week)
                .map(storedStatus -> AttendanceStatus.formatStoredValue(storedStatus).toLowerCase(Locale.ROOT));
    }

    @Override
    protected Student withoutRecord(Student student, int week) {
        return student.withoutAttendanceRecord(week);
    }

    @Override
    protected String getClearedMessage() {
        return MESSAGE_CLEARED;
    }

    @Override
    protected String getNotRecordedMessage() {
        return MESSAGE_NOT_RECORDED;
    }
}
