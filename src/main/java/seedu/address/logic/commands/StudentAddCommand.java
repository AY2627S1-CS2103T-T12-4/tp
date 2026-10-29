package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;

/**
 * Adds a student to the active tutorial group.
 */
public class StudentAddCommand extends Command {
    public static final String MESSAGE_USAGE = "student add n/NAME id/STUDENT_ID";
    public static final String MESSAGE_SUCCESS = "Added %s to %s.";
    public static final String MESSAGE_SAME_NAME_WARNING =
            " Warning: %s already has a student named %s (%s). Check that the student ID is correct.";
    public static final String MESSAGE_DUPLICATE_STUDENT = "Student ID %s already exists in %s.";

    private static final Logger logger = LogsCenter.getLogger(StudentAddCommand.class);
    private final Student student;

    /**
     * Creates a command to add the given student.
     */
    public StudentAddCommand(Student student) {
        this.student = requireNonNull(student);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = getActiveGroup(model);
        if (group.hasStudent(student.getStudentId())) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT,
                    student.getStudentId(), group.getName()));
        }

        Optional<Student> sameNameStudent = group.findStudentNamed(student.getName());
        model.addStudent(group.getName(), student);
        assert group.hasStudent(student.getStudentId()) : "The added student must belong to the active group";
        logger.fine("Added a student to " + group.getName());
        String message = String.format(MESSAGE_SUCCESS, student.getName(), group.getName());
        if (sameNameStudent.isPresent()) {
            Student existing = sameNameStudent.get();
            message += String.format(MESSAGE_SAME_NAME_WARNING, group.getName(), existing.getName(),
                    existing.getStudentId());
        }
        return new CommandResult(message);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof StudentAddCommand otherCommand && student.equals(otherCommand.student);
    }

    @Override
    public int hashCode() {
        return student.hashCode();
    }
}
