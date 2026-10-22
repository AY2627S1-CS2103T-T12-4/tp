package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_ASSIGNMENT;

import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Records or replaces one student's submission status for an assignment.
 */
public class AssignmentSubmitCommand extends Command {
    public static final String MESSAGE_USAGE = "assignment submit a/ASSIGNMENT_NAME id/STUDENT_ID s/STATUS";
    public static final String MESSAGE_SUCCESS = "Updated %s's %s submission status to %s.";
    public static final String MESSAGE_UNKNOWN_STUDENT = "Student does not exist in the active group.";

    private final AssignmentName assignmentName;
    private final StudentId studentId;
    private final boolean isSubmitted;

    /**
     * Creates a command to set the student's status to submitted or not submitted.
     */
    public AssignmentSubmitCommand(AssignmentName assignmentName, StudentId studentId, boolean isSubmitted) {
        this.assignmentName = requireNonNull(assignmentName);
        this.studentId = requireNonNull(studentId);
        this.isSubmitted = isSubmitted;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = model.getActiveGroup().orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        Assignment assignment = group.findAssignment(assignmentName).orElseThrow(() ->
                new CommandException(String.format(MESSAGE_UNKNOWN_ASSIGNMENT,
                        assignmentName, group.getName(), assignmentName)));
        Student student = group.findStudent(studentId)
                .orElseThrow(() -> new CommandException(MESSAGE_UNKNOWN_STUDENT));

        model.setAssignment(group.getName(), assignment.withSubmissionStatus(studentId, isSubmitted));
        String status = isSubmitted ? "submitted" : "not submitted";
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), assignment.getName(), status));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AssignmentSubmitCommand otherCommand
                && assignmentName.equals(otherCommand.assignmentName)
                && studentId.equals(otherCommand.studentId) && isSubmitted == otherCommand.isSubmitted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentName, studentId, isSubmitted);
    }
}
