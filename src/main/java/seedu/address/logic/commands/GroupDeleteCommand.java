package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

/**
 * Deletes a tutorial group together with its students and their records.
 */
public class GroupDeleteCommand extends Command {
    public static final String MESSAGE_USAGE = "group delete g/GROUP_NAME";
    public static final String MESSAGE_SUCCESS = "Deleted tutorial group %s and its %d student(s).";
    public static final String MESSAGE_NOW_NO_ACTIVE_GROUP =
            "\nNo group is active now. Select one with: group use g/GROUP_NAME";
    public static final String MESSAGE_UNKNOWN_GROUP = "Tutorial group %s does not exist.";

    private static final Logger logger = LogsCenter.getLogger(GroupDeleteCommand.class);
    private final GroupName name;

    /**
     * Creates a command to delete the group with the given name.
     */
    public GroupDeleteCommand(GroupName name) {
        this.name = requireNonNull(name);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.hasGroup(name)) {
            throw new CommandException(String.format(MESSAGE_UNKNOWN_GROUP, name));
        }

        Group removed = model.removeGroup(name);
        assert !model.hasGroup(name) : "The deleted group must no longer exist";
        logger.fine("Deleted a group with " + removed.getStudentList().size() + " student(s)");
        String feedback = String.format(MESSAGE_SUCCESS, removed.getName(), removed.getStudentList().size());
        return new CommandResult(model.getActiveGroup().isEmpty() ? feedback + MESSAGE_NOW_NO_ACTIVE_GROUP : feedback);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof GroupDeleteCommand otherCommand && name.equals(otherCommand.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
