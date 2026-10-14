package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.Student;
import seedu.address.model.student.WeeklyRecord;
import seedu.address.model.student.WeeklyRecordEntry;

/**
 * Lists every student's attendance for one week in the active tutorial group.
 */
public class AttendanceListCommand extends Command {
    public static final String MESSAGE_USAGE = "attendance list w/WEEK";
    public static final String MESSAGE_HEADER = "Attendance for %s, Week %d:";
    public static final String MESSAGE_NO_RECORDS = "No attendance records have been recorded for Week %d.";

    private final int week;

    /**
     * Creates a listing command for a valid teaching week.
     */
    public AttendanceListCommand(int week) {
        WeeklyRecord.validateWeek(week);
        this.week = week;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = model.getActiveGroup().orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        List<WeeklyRecordEntry<String>> entries = group.getWeeklyRecordEntries(week, Student::getAttendanceRecords);
        if (entries.stream().noneMatch(entry -> entry.getRecord().isPresent())) {
            return new CommandResult(String.format(MESSAGE_NO_RECORDS, week));
        }

        String rows = entries.stream().map(AttendanceListCommand::formatRow).collect(Collectors.joining("\n"));
        return new CommandResult(String.format(MESSAGE_HEADER, group.getName(), week) + "\n" + rows);
    }

    private static String formatRow(WeeklyRecordEntry<String> entry) {
        Student student = entry.getStudent();
        String value = entry.formatValueOrNotRecorded(AttendanceListCommand::formatStoredStatus);
        return student.getName() + " | " + student.getStudentId() + " | " + value;
    }

    /**
     * Formats a stored status for display without parsing it again.
     * {@code PRESENT} and {@code ABSENT} are the values mark and JSON write. Any other stored text is shown as-is.
     */
    private static String formatStoredStatus(String status) {
        if (AttendanceStatus.PRESENT.name().equals(status)) {
            return AttendanceStatus.PRESENT.getDisplayValue();
        }
        if (AttendanceStatus.ABSENT.name().equals(status)) {
            return AttendanceStatus.ABSENT.getDisplayValue();
        }
        return status;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AttendanceListCommand otherCommand && week == otherCommand.week;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(week);
    }
}
