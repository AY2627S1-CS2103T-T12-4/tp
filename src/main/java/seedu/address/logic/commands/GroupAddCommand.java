package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

/**
 * Creates a tutorial group and makes it active.
 */
public class GroupAddCommand extends Command {
    public static final String MESSAGE_USAGE = "group add g/GROUP_NAME";
    public static final String MESSAGE_SUCCESS = "Created tutorial group: %1$s.\nActive group is now %1$s.";
    public static final String MESSAGE_DUPLICATE_GROUP = "A tutorial group named %s already exists.";

    private final GroupName name;

    /**
     * Creates a command to add an empty group with the given name.
     */
    public GroupAddCommand(GroupName name) {
        this.name = requireNonNull(name);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.hasGroup(name)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_GROUP, model.getGroup(name).getName()));
        }

        model.addGroup(new Group(name));
        model.setActiveGroup(name);
        assert model.getActiveGroup().orElseThrow().getName().equals(name) : "The new group must be active";
        return new CommandResult(String.format(MESSAGE_SUCCESS, name));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof GroupAddCommand otherCommand && name.equals(otherCommand.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
