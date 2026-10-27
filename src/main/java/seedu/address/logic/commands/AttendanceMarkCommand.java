package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;

import java.util.Objects;

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
    public static final String PREFIX_STUDENT_ID = "id/";
    public static final String PREFIX_WEEK = "w/";
    public static final String PREFIX_STATUS = "s/";
    public static final String MESSAGE_USAGE = "attendance mark " + PREFIX_STUDENT_ID + "STUDENT_ID "
            + PREFIX_WEEK + "WEEK " + PREFIX_STATUS + "STATUS";
    public static final String MESSAGE_RECORDED = "Recorded %s as %s for Week %d.";
    public static final String MESSAGE_UPDATED = "Updated %s's attendance for Week %d to %s.";
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

    /**
     * Returns an incomplete command for {@code studentId} that the TA finishes by entering the week and status.
     */
    public static String buildTemplate(StudentId studentId) {
        requireNonNull(studentId);
        return "attendance mark " + PREFIX_STUDENT_ID + studentId + " " + PREFIX_WEEK;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = model.getActiveGroup().orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        Student student = group.findStudent(studentId)
                .orElseThrow(() -> new CommandException(MESSAGE_UNKNOWN_STUDENT));
        boolean hasRecord = student.getAttendanceRecord(week).isPresent();

        model.setStudent(group.getName(), student.withAttendanceRecord(week, status.name()));
        String feedback = hasRecord
                ? String.format(MESSAGE_UPDATED, student.getName(), week, status.getCommandValue())
                : String.format(MESSAGE_RECORDED, student.getName(), status.getCommandValue(), week);
        return new CommandResult(feedback);
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
