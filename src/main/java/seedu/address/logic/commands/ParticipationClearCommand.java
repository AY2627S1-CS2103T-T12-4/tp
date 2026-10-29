package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Clears the participation score of one or more students in the active group for a week, so that the week shows
 * Not recorded. This corrects a score entered for the wrong student or week.
 */
public class ParticipationClearCommand extends WeeklyRecordClearCommand {
    public static final String MESSAGE_USAGE = "participation clear id/STUDENT_ID [id/STUDENT_ID]... w/WEEK";
    public static final String MESSAGE_CLEARED = "Cleared %s's participation score for Week %d: %s to Not recorded.";
    public static final String MESSAGE_NOT_RECORDED =
            "%s has no participation score recorded for Week %d. Nothing changed.";

    /**
     * Creates a command to clear the given student's participation score for a valid teaching week.
     */
    public ParticipationClearCommand(StudentId studentId, int week) {
        this(List.of(requireNonNull(studentId)), week);
    }

    /**
     * Creates a command to clear the given students' participation scores for a valid teaching week.
     * The list must not be empty.
     */
    public ParticipationClearCommand(List<StudentId> studentIds, int week) {
        super(studentIds, week);
    }

    @Override
    protected Optional<String> getRecordedValue(Student student, int week) {
        return student.getParticipationRecord(week).map(String::valueOf);
    }

    @Override
    protected Student withoutRecord(Student student, int week) {
        return student.withoutParticipationRecord(week);
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
