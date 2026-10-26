package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;

import java.util.Objects;
import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.WeeklyRecord;

/**
 * Records or replaces one student's attendance in the active group for a teaching week.
 */
public class AttendanceMarkCommand extends Command {
    public static final String MESSAGE_USAGE = "attendance mark id/STUDENT_ID w/WEEK s/STATUS";
    public static final String MESSAGE_RECORDED = "Recorded %s as %s for Week %d.";
    public static final String MESSAGE_UPDATED = "Updated %s's attendance for Week %d: %s to %s.";
    public static final String MESSAGE_UNCHANGED = "%s's attendance for Week %d is already %s. Nothing changed.";
    public static final String MESSAGE_UNKNOWN_STUDENT = "Student does not exist in the active group.";

    private final StudentId studentId;
    private final int week;
    private final AttendanceStatus status;

    /**
     * Creates a command to mark the given student present or absent for a valid teaching week.
     */
    public AttendanceMarkCommand(StudentId studentId, int week, AttendanceStatus status) {
        this.studentId = requireNonNull(studentId);
        WeeklyRecord.validateWeek(week);
        this.week = week;
        this.status = requireNonNull(status);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = model.getActiveGroup().orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        Student student = group.findStudent(studentId)
                .orElseThrow(() -> new CommandException(MESSAGE_UNKNOWN_STUDENT));
        Optional<String> oldStatus = student.getAttendanceRecord(week).map(AttendanceMarkCommand::toCommandValue);

        model.setStudent(group.getName(), student.withAttendanceRecord(week, status.name()));
        return new CommandResult(createFeedback(student, oldStatus));
    }

    /**
     * Returns feedback that shows the old and new status when an earlier mark exists.
     */
    private String createFeedback(Student student, Optional<String> oldStatus) {
        String newStatus = status.getCommandValue();
        if (oldStatus.isEmpty()) {
            return String.format(MESSAGE_RECORDED, student.getName(), newStatus, week);
        }
        if (oldStatus.get().equals(newStatus)) {
            return String.format(MESSAGE_UNCHANGED, student.getName(), week, newStatus);
        }
        return String.format(MESSAGE_UPDATED, student.getName(), week, oldStatus.get(), newStatus);
    }

    private static String toCommandValue(String storedStatus) {
        return AttendanceStatus.fromValue(storedStatus).getCommandValue();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AttendanceMarkCommand otherCommand
                && studentId.equals(otherCommand.studentId)
                && week == otherCommand.week && status == otherCommand.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, week, status);
    }
}
