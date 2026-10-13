package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.Model;
import seedu.address.model.group.Group;

/**
 * Lists tutorial groups in creation order and identifies the active group.
 */
public class GroupListCommand extends Command {
    public static final String MESSAGE_USAGE = "group list";
    public static final String MESSAGE_NO_GROUPS =
            "No tutorial groups exist. Create one using: group add g/GROUP_NAME";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Group> groups = model.getTAssist().getGroupList();
        if (groups.isEmpty()) {
            return new CommandResult(MESSAGE_NO_GROUPS);
        }

        StringBuilder feedback = new StringBuilder();
        for (int i = 0; i < groups.size(); i++) {
            feedback.append(i + 1).append(". ").append(groups.get(i).getName()).append('\n');
        }
        String activeName = model.getActiveGroup().map(group -> group.getName().toString()).orElse("None");
        feedback.append("Active group: ").append(activeName);
        return new CommandResult(feedback.toString());
    }
}
