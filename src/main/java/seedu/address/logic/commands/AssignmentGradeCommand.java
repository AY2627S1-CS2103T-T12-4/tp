package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_ASSIGNMENT;

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
 * Records or replaces the grade for one student's submitted assignment.
 */
public class AssignmentGradeCommand extends Command {
    public static final String MESSAGE_USAGE = "assignment grade a/ASSIGNMENT_NAME id/STUDENT_ID m/GRADE";
    public static final String MESSAGE_SUCCESS = "Recorded grade %d for %s's %s.";
    public static final String MESSAGE_NOT_SUBMITTED =
            "Cannot record a grade for an unsubmitted assignment. Record it first with: "
                    + "assignment submit a/%s id/%s s/yes";
    public static final String MESSAGE_UNKNOWN_STUDENT = "Student does not exist in the active group.";

    private static final Logger logger = LogsCenter.getLogger(AssignmentGradeCommand.class);

    private final AssignmentName assignmentName;
    private final StudentId studentId;
    private final int grade;

    /**
     * Creates a command with an assignment grade from 0 to 100.
     */
    public AssignmentGradeCommand(AssignmentName assignmentName, StudentId studentId, int grade) {
        this.assignmentName = requireNonNull(assignmentName);
        this.studentId = requireNonNull(studentId);
        checkArgument(AssignmentRecord.isValidGrade(grade), AssignmentRecord.MESSAGE_INVALID_GRADE);
        this.grade = grade;
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
        if (!assignment.getStudentRecord(studentId).isSubmitted()) {
            throw new CommandException(String.format(MESSAGE_NOT_SUBMITTED,
                    assignment.getName(), student.getStudentId()));
        }

        model.setAssignment(group.getName(), assignment.withGrade(studentId, grade));
        logger.fine("Recorded an assignment grade for " + student.getStudentId()
                + " in " + group.getName());
        return new CommandResult(String.format(MESSAGE_SUCCESS, grade, student.getName(), assignment.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AssignmentGradeCommand otherCommand
                && assignmentName.equals(otherCommand.assignmentName)
                && studentId.equals(otherCommand.studentId) && grade == otherCommand.grade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentName, studentId, grade);
    }
}
