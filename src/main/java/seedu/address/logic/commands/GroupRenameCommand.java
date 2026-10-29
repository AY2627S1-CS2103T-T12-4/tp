package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_GROUP;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_GROUP;

import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

/**
 * Renames a tutorial group without changing its students or records.
 */
public class GroupRenameCommand extends Command {
    public static final String MESSAGE_USAGE = "group rename g/GROUP_NAME n/NEW_NAME";
    public static final String MESSAGE_SUCCESS = "Renamed tutorial group %s to %s.";
    public static final String MESSAGE_UNCHANGED = "Tutorial group %s already has that name. Nothing changed.";

    private final GroupName name;
    private final GroupName newName;

    /**
     * Creates a command to rename the group named {@code name} to {@code newName}.
     */
    public GroupRenameCommand(GroupName name, GroupName newName) {
        requireAllNonNull(name, newName);
        this.name = name;
        this.newName = newName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.hasGroup(name)) {
            throw new CommandException(String.format(MESSAGE_UNKNOWN_GROUP, name));
        }

        Group target = model.getGroup(name);
        if (model.hasGroup(newName) && !model.getGroup(newName).isSameGroup(target)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_GROUP, model.getGroup(newName).getName()));
        }

        GroupName oldName = target.getName();
        if (oldName.fullName.equals(newName.fullName)) {
            return new CommandResult(String.format(MESSAGE_UNCHANGED, oldName));
        }

        model.renameGroup(oldName, newName);
        assert model.hasGroup(newName) : "The renamed group must exist under its new name";
        return new CommandResult(String.format(MESSAGE_SUCCESS, oldName, newName));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof GroupRenameCommand otherCommand
                && name.equals(otherCommand.name) && newName.equals(otherCommand.newName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, newName);
    }
}
