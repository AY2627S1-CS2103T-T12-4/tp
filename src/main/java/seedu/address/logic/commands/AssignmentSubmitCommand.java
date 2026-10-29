package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.AssignmentRecord;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Records or replaces one student's submission status for an assignment.
 * Marking the assignment not submitted clears any grade recorded for it.
 */
public class AssignmentSubmitCommand extends Command {
    public static final String MESSAGE_USAGE = "assignment submit a/ASSIGNMENT_NAME id/STUDENT_ID s/STATUS";
    public static final String MESSAGE_SUCCESS = "Updated %s's %s submission status to %s.";
    public static final String MESSAGE_UNCHANGED =
            "%s's %s submission status is already %s. Nothing changed.";
    public static final String MESSAGE_SUCCESS_WITHDRAWN =
            "Updated %s's %s submission status to not submitted; the recorded grade was cleared.";

    private static final Logger logger = LogsCenter.getLogger(AssignmentSubmitCommand.class);

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
        Group group = getActiveGroup(model);
        Assignment assignment = getAssignment(group, assignmentName);
        Student student = getStudent(group, studentId);
        AssignmentRecord previousRecord = assignment.getStudentRecord(studentId);

        if (isSubmitted == previousRecord.isSubmitted()) {
            return new CommandResult(String.format(MESSAGE_UNCHANGED,
                    student.getName(), assignment.getName(), describeStatus()));
        }

        model.setAssignment(group.getName(), assignment.withSubmissionStatus(studentId, isSubmitted));
        logger.fine("Updated an assignment submission for " + student.getStudentId()
                + " in " + group.getName());
        if (!isSubmitted && previousRecord.getGrade().isPresent()) {
            return new CommandResult(String.format(MESSAGE_SUCCESS_WITHDRAWN,
                    student.getName(), assignment.getName()));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                student.getName(), assignment.getName(), describeStatus()));
    }

    private String describeStatus() {
        return isSubmitted ? "submitted" : "not submitted";
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
