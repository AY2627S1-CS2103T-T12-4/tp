package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_GROUP;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.GroupName;

/**
 * Switches the active tutorial group without changing its records.
 */
public class GroupUseCommand extends Command {
    public static final String MESSAGE_USAGE = "group use g/GROUP_NAME";
    public static final String MESSAGE_SUCCESS = "Switched active group to %s.";
    public static final String MESSAGE_ALREADY_ACTIVE = "%s is already the active group. Nothing changed.";

    private final GroupName name;

    /**
     * Creates a command to activate the group with the given name.
     */
    public GroupUseCommand(GroupName name) {
        this.name = requireNonNull(name);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.hasGroup(name)) {
            throw new CommandException(String.format(MESSAGE_UNKNOWN_GROUP, name));
        }

        GroupName storedName = model.getGroup(name).getName();
        if (isActive(model, storedName)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_ACTIVE, storedName));
        }

        model.setActiveGroup(storedName);
        return new CommandResult(String.format(MESSAGE_SUCCESS, storedName));
    }

    private static boolean isActive(Model model, GroupName groupName) {
        return model.getActiveGroup().map(group -> group.getName().equals(groupName)).orElse(false);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof GroupUseCommand otherCommand && name.equals(otherCommand.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
