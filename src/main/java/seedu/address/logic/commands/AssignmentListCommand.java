package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_ASSIGNMENT;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;

/**
 * Lists every student's record for one assignment of the active group, in roster order.
 */
public class AssignmentListCommand extends Command {
    public static final String MESSAGE_USAGE = "assignment list a/ASSIGNMENT_NAME";
    public static final String MESSAGE_NO_STUDENTS = "No students in %s.";
    public static final String STATUS_NOT_SUBMITTED = "Not submitted";
    public static final String GRADE_NOT_RECORDED = "-";

    private final AssignmentName assignmentName;

    /**
     * Creates a command to list the records of the assignment with the given valid name.
     */
    public AssignmentListCommand(AssignmentName assignmentName) {
        this.assignmentName = requireNonNull(assignmentName);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = model.getActiveGroup().orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        Assignment assignment = group.findAssignment(assignmentName).orElseThrow(() ->
                new CommandException(String.format(MESSAGE_UNKNOWN_ASSIGNMENT,
                        assignmentName, group.getName(), assignmentName)));
        List<Student> students = group.getStudentList();
        if (students.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NO_STUDENTS, group.getName()));
        }

        StringBuilder feedback = new StringBuilder(assignment.getName() + " records for " + group.getName() + ":");
        for (Student student : students) {
            feedback.append('\n').append(student.getName()).append(" | ")
                    .append(student.getStudentId()).append(" | ")
                    .append(STATUS_NOT_SUBMITTED).append(" | ")
                    .append(GRADE_NOT_RECORDED);
        }
        return new CommandResult(feedback.toString());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof AssignmentListCommand otherCommand
                && assignmentName.equals(otherCommand.assignmentName);
    }

    @Override
    public int hashCode() {
        return assignmentName.hashCode();
    }
}
