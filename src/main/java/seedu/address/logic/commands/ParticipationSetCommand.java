package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.Messages.MESSAGE_NO_ACTIVE_GROUP;

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
 * Records or replaces a student's participation score in the active group for one week.
 */
public class ParticipationSetCommand extends Command {
    public static final String MESSAGE_USAGE = "participation set id/STUDENT_ID w/WEEK s/SCORE";
    public static final String MESSAGE_RECORDED = "Recorded participation score %s for %s in Week %s.";
    public static final String MESSAGE_UPDATED = "Updated %s's participation score for Week %s: %s to %s.";
    public static final String MESSAGE_UNCHANGED =
            "%s's participation score for Week %s is already %s. Nothing changed.";
    public static final String MESSAGE_UNKNOWN_STUDENT = "Student does not exist in the active group.";

    private static final Logger logger = LogsCenter.getLogger(ParticipationSetCommand.class);
    private final StudentId studentId;
    private final int week;
    private final int score;

    /**
     * Creates a command with a valid student ID, week from 1 to 13, and score from 0 to 5.
     */
    public ParticipationSetCommand(StudentId studentId, int week, int score) {
        this.studentId = requireNonNull(studentId);
        WeeklyRecord.validateWeek(week);
        checkArgument(score >= 0 && score <= 5, "Participation score must be a whole number from 0 to 5.");
        this.week = week;
        this.score = score;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = model.getActiveGroup().orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        Student student = group.findStudent(studentId)
                .orElseThrow(() -> new CommandException(MESSAGE_UNKNOWN_STUDENT));
        Optional<Integer> oldScore = student.getParticipationRecord(week);
        Student updated = student.withParticipationRecord(week, score);
        model.setStudent(group.getName(), updated);
        assert updated.getParticipationRecord(week).orElseThrow() == score
                : "The new score must replace this week's value";
        logger.fine("Set participation for Week " + week + " in " + group.getName());
        return new CommandResult(createFeedback(student, oldScore));
    }

    /**
     * Returns feedback that shows the old and new score when an earlier score exists.
     */
    private String createFeedback(Student student, Optional<Integer> oldScore) {
        if (oldScore.isEmpty()) {
            return String.format(MESSAGE_RECORDED, score, student.getName(), week);
        }
        if (oldScore.get() == score) {
            return String.format(MESSAGE_UNCHANGED, student.getName(), week, score);
        }
        return String.format(MESSAGE_UPDATED, student.getName(), week, oldScore.get(), score);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ParticipationSetCommand otherCommand
                && studentId.equals(otherCommand.studentId) && week == otherCommand.week && score == otherCommand.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, week, score);
    }
}
