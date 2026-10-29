package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.Student;
import seedu.address.model.student.WeeklyRecord;
import seedu.address.model.student.WeeklyRecordEntry;

/**
 * Lists every student's attendance, including missing records, for one week in the active tutorial group.
 * A summary of the counts for each status, and of the students without a mark, follows the header.
 */
public class AttendanceListCommand extends Command {
    public static final String MESSAGE_USAGE = "attendance list w/WEEK";
    public static final String MESSAGE_HEADER = "Attendance for %s, Week %d:";
    public static final String MESSAGE_NO_STUDENTS = "No students in %s.";

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
        Group group = getActiveGroup(model);
        List<WeeklyRecordEntry<String>> entries = group.getWeeklyRecordEntries(week, Student::getAttendanceRecords);
        if (entries.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NO_STUDENTS, group.getName()));
        }

        String header = String.format(MESSAGE_HEADER, group.getName(), week);
        String summary = WeeklyRecordSummary.summarize(entries, getStatusCategories());
        String rows = entries.stream().map(AttendanceListCommand::formatRow).collect(Collectors.joining("\n"));
        return new CommandResult(header + "\n" + summary + "\n" + rows);
    }

    /**
     * Returns one category per attendance status, labeled with the status as the TA types it, in declaration order.
     */
    private static Map<String, Predicate<String>> getStatusCategories() {
        Map<String, Predicate<String>> categories = new LinkedHashMap<>();
        for (AttendanceStatus status : AttendanceStatus.values()) {
            categories.put(status.getCommandValue(), status.name()::equals);
        }
        return categories;
    }

    private static String formatRow(WeeklyRecordEntry<String> entry) {
        Student student = entry.getStudent();
        String value = entry.formatValueOrNotRecorded(AttendanceStatus::formatStoredValue);
        return student.getName() + " | " + student.getStudentId() + " | " + value;
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
