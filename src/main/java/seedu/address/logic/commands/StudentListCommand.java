package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;

/**
 * Lists the active group's students in roster order.
 */
public class StudentListCommand extends Command {
    public static final String MESSAGE_USAGE = "student list";
    public static final String MESSAGE_NO_STUDENTS =
            "No students in %s. Add one using: student add n/NAME id/STUDENT_ID";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = model.getActiveGroup().orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        model.resetStudentDisplay();
        List<Student> students = group.getStudentList();
        if (students.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NO_STUDENTS, group.getName()));
        }

        StringBuilder feedback = new StringBuilder("Students in " + group.getName() + ":");
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            feedback.append('\n').append(i + 1).append(". ").append(student.getName())
                    .append(" | ").append(student.getStudentId());
        }
        return new CommandResult(feedback.toString());
    }
}
