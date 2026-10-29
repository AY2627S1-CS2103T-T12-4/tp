package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;

/**
 * Corrects the name or student ID of a student in the active tutorial group, keeping the student's records.
 */
public class StudentEditCommand extends Command {
    public static final String MESSAGE_USAGE = "student edit id/STUDENT_ID [n/NEW_NAME] [nid/NEW_ID]";
    public static final String MESSAGE_SUCCESS = "Edited %s (%s) to %s (%s) in %s. Their records are kept.";
    public static final String MESSAGE_UNCHANGED = "%s (%s) already has those details. Nothing changed.";
    public static final String MESSAGE_SAME_NAME_WARNING = " Warning: %s already has another student named %s.";

    private static final Logger logger = LogsCenter.getLogger(StudentEditCommand.class);

    private final StudentId studentId;
    private final Optional<StudentName> newName;
    private final Optional<StudentId> newStudentId;

    /**
     * Creates a command to change the name, the student ID, or both of the student with {@code studentId}.
     *
     * @param studentId The current ID of the student to edit.
     * @param newName The new name, or empty to keep the name.
     * @param newStudentId The new student ID, or empty to keep the ID.
     * @throws IllegalArgumentException if neither a new name nor a new student ID is given.
     */
    public StudentEditCommand(StudentId studentId, Optional<StudentName> newName,
            Optional<StudentId> newStudentId) {
        requireAllNonNull(studentId, newName, newStudentId);
        checkArgument(newName.isPresent() || newStudentId.isPresent(), "Give a new name, a new student ID, or both.");
        this.studentId = studentId;
        this.newName = newName;
        this.newStudentId = newStudentId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = getActiveGroup(model);
        Student target = getStudent(group, studentId);
        Student edited = target.withNameAndId(newName.orElse(target.getName()),
                newStudentId.orElse(target.getStudentId()));
        if (edited.equals(target)) {
            return new CommandResult(String.format(MESSAGE_UNCHANGED, target.getName(), target.getStudentId()));
        }
        if (hasOtherStudentWithId(group, target, edited)) {
            throw new CommandException(String.format(StudentAddCommand.MESSAGE_DUPLICATE_STUDENT,
                    edited.getStudentId(), group.getName()));
        }

        boolean hasSameName = isNameChanged(target, edited) && hasOtherStudentNamed(group, target, edited);
        model.replaceStudent(group.getName(), studentId, edited);
        assert group.hasStudent(edited.getStudentId()) : "The edited student must belong to the active group";
        logger.fine("Edited a student in " + group.getName());
        return new CommandResult(createFeedback(group, target, edited, hasSameName));
    }

    private static boolean hasOtherStudentWithId(Group group, Student target, Student edited) {
        return !target.isSameStudent(edited) && group.hasStudent(edited.getStudentId());
    }

    private static boolean isNameChanged(Student target, Student edited) {
        return !target.getName().isSameName(edited.getName());
    }

    private static boolean hasOtherStudentNamed(Group group, Student target, Student edited) {
        return group.getStudentList().stream()
                .anyMatch(student -> !student.isSameStudent(target) && student.getName().isSameName(edited.getName()));
    }

    private static String createFeedback(Group group, Student target, Student edited, boolean hasSameName) {
        String message = String.format(MESSAGE_SUCCESS, target.getName(), target.getStudentId(),
                edited.getName(), edited.getStudentId(), group.getName());
        if (hasSameName) {
            message += String.format(MESSAGE_SAME_NAME_WARNING, group.getName(), edited.getName());
        }
        return message;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof StudentEditCommand otherCommand
                && studentId.equals(otherCommand.studentId)
                && newName.equals(otherCommand.newName)
                && newStudentId.equals(otherCommand.newStudentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, newName, newStudentId);
    }
}
