package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.WeeklyRecord;

/**
 * Clears one student's attendance in the active group for a teaching week, so that the week shows Not recorded.
 * This corrects a mark entered for the wrong student or week.
 */
public class AttendanceClearCommand extends Command {
    public static final String MESSAGE_USAGE = "attendance clear id/STUDENT_ID w/WEEK";
    public static final String MESSAGE_CLEARED = "Cleared %s's attendance for Week %d: %s to Not recorded.";
    public static final String MESSAGE_NOT_RECORDED = "%s has no attendance recorded for Week %d. Nothing changed.";

    private static final Logger logger = LogsCenter.getLogger(AttendanceClearCommand.class);

    private final StudentId studentId;
    private final int week;

    /**
     * Creates a command to clear the given student's attendance for a valid teaching week.
     */
    public AttendanceClearCommand(StudentId studentId, int week) {
        this.studentId = requireNonNull(studentId);
        WeeklyRecord.validateWeek(week);
        this.week = week;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = getActiveGroup(model);
        Student student = getStudent(group, studentId);
        Optional<String> oldStatus = student.getAttendanceRecord(week);
        if (oldStatus.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NOT_RECORDED, student.getName(), week));
        }

        Student cleared = student.withoutAttendanceRecord(week);
        model.setStudent(group.getName(), cleared);
        assert cleared.getAttendanceRecord(week).isEmpty() : "The week must no longer have a mark";
        logger.fine("Cleared attendance for Week " + week + " in " + group.getName());
        return new CommandResult(String.format(MESSAGE_CLEARED, student.getName(), week,
                AttendanceStatus.fromValue(oldStatus.get()).getCommandValue()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AttendanceClearCommand otherCommand
                && studentId.equals(otherCommand.studentId) && week == otherCommand.week;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, week);
    }
}
