package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.WeeklyRecord;

/**
 * Clears one kind of weekly record of one or more students in the active group for a teaching week, so that the week
 * shows Not recorded for them. Subclasses choose the kind of record and the feedback wording.
 * Every student is found before any record changes, so an unknown ID leaves all records unchanged.
 */
public abstract class WeeklyRecordClearCommand extends Command {
    private static final Logger logger = LogsCenter.getLogger(WeeklyRecordClearCommand.class);

    private final List<StudentId> studentIds;
    private final int week;

    /**
     * Creates a command to clear the records of the given students for a valid teaching week.
     * The list must not be empty.
     */
    protected WeeklyRecordClearCommand(List<StudentId> studentIds, int week) {
        requireNonNull(studentIds);
        checkArgument(!studentIds.isEmpty(), "At least one student ID is required.");
        this.studentIds = List.copyOf(studentIds);
        WeeklyRecord.validateWeek(week);
        this.week = week;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = getActiveGroup(model);
        List<Student> students = getStudents(group, studentIds);

        List<String> feedback = new ArrayList<>();
        for (Student student : students) {
            feedback.add(clearRecord(model, group, student));
        }
        return new CommandResult(String.join("\n", feedback));
    }

    /**
     * Clears {@code student}'s record for the week if there is one, and returns the feedback line for that student.
     */
    private String clearRecord(Model model, Group group, Student student) {
        Optional<String> oldValue = getRecordedValue(student, week);
        if (oldValue.isEmpty()) {
            return String.format(getNotRecordedMessage(), student.getName(), week);
        }

        Student cleared = withoutRecord(student, week);
        model.setStudent(group.getName(), cleared);
        assert getRecordedValue(cleared, week).isEmpty() : "The week must no longer have a record";
        logger.fine(getClass().getSimpleName() + " cleared Week " + week + " for " + student.getStudentId()
                + " in " + group.getName());
        return String.format(getClearedMessage(), student.getName(), week, oldValue.get());
    }

    /**
     * Returns {@code student}'s record for {@code week} as it appears in the feedback, or empty if there is none.
     */
    protected abstract Optional<String> getRecordedValue(Student student, int week);

    /**
     * Returns a copy of {@code student} without a record for {@code week}.
     */
    protected abstract Student withoutRecord(Student student, int week);

    /**
     * Returns the feedback format for a cleared record, taking the student's name, the week and the old value.
     */
    protected abstract String getClearedMessage();

    /**
     * Returns the feedback format for a student with nothing to clear, taking the student's name and the week.
     */
    protected abstract String getNotRecordedMessage();

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof WeeklyRecordClearCommand otherCommand
                && getClass() == otherCommand.getClass()
                && studentIds.equals(otherCommand.studentIds) && week == otherCommand.week;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), studentIds, week);
    }
}
